# Appium TestNG — SwiftCinemas iOS UI Tests

Automated UI tests for the SwiftCinemas iOS app using **Appium 3** + **XCUITest** driver + **TestNG**.

> **⚠️ Backend required:** The test flow navigates Movies → Categories → Venues → Seats → Checkout.
> Every screen after the home page fetches data from the backend API. Without a running
> Cinemas system (MySQL, Kafka, mbooks, dalogin, etc.), the Movies table is empty, the
> segmented control never renders, and all tests fail with `NoSuchElement`.
>
> See [Backend setup](#step-0--start-the-cinemas-backend) below, or the
> [full test fix plan](../.github/docs/appium-test-fix-plan.md) for details.

---

## Prerequisites

Install these before running the tests:

| Tool                | Install command                   | Verify                           |
|---------------------|-----------------------------------|----------------------------------|
| **Java 17**         | (via SDKMAN, Homebrew, or Oracle) | `java -version`                  |
| **Maven 3.9+**      | `brew install maven`              | `mvn -version`                   |
| **Xcode 16+**       | Mac App Store                     | `xcodebuild -version`            |
| **Xcode CLI tools** | `xcode-select --install`          | `xcode-select -p`                |
| **Carthage**        | `brew install carthage`           | `carthage version`               |
| **Node.js 18+**     | `brew install node`               | `node -v`                        |
| **Appium 3**        | `npm install -g appium`           | `appium -v`                      |
| **XCUITest driver** | `appium driver install xcuitest`  | `appium driver list --installed` |
| **Docker CLI**      | `brew install docker`             | `docker --version`               |
| **colima**          | `brew install colima`             | `colima status`                  |
| **Minikube**        | `brew install minikube`           | `minikube version`               |
| **kubectl**         | `brew install kubectl`            | `kubectl version --client`       |

---

## Test preparation — step by step

### Step 0 — Start the Cinemas backend

The iOS app connects to `https://milo.crabdance.com`. The full local Kubernetes stack
must be running. Follow the [local K8s runbook](../k8infra/README-k8s-local.md) or these
condensed steps:

```bash
# Start colima (Docker-compatible daemon — no Docker Desktop licence needed)
colima start --cpu 4 --memory 4 --disk 20

# Start Minikube with the Docker driver
minikube start --driver=docker --cpus=4 --memory=3900
minikube addons enable ingress

# Build Quarkus artifacts (from repo root)
(cd dalogin-quarkus            && ./mvnw package -DskipTests)
(cd mbook-quarkus              && ./mvnw package -DskipTests)
(cd mbooks-quarkus             && ./mvnw package -DskipTests)
(cd simple-service-webapp-quarkus && ./mvnw package -DskipTests)

# Build images inside Minikube's Docker (fastest — no loading/retag needed)
eval $(minikube docker-env)
docker build -t dalogin-quarkus:local              ./dalogin-quarkus
docker build -t mbook-quarkus:local                ./mbook-quarkus
docker build -t mbooks-quarkus:local               ./mbooks-quarkus
docker build -t simple-service-webapp-quarkus:local ./simple-service-webapp-quarkus
eval $(minikube docker-env --unset)

# Deploy
kubectl apply -f k8infra/quarkus-backend.yaml
kubectl -n cinemas get pods   # wait until all Running

# Seed databases (both scripts are self-contained)
kubectl -n cinemas exec -i deploy/mysql -- mysql -uroot -prootpw < mysql_8/login.sql
kubectl -n cinemas exec -i deploy/mysql -- mysql -uroot -prootpw < mysql_8/book.sql

# Copy pictures into the PVC
kubectl -n cinemas cp pictures/ \
  $(kubectl -n cinemas get pod -l app=simple-service-webapp \
      -o jsonpath='{.items[0].metadata.name}'):/pictures/

# HTTPS access for the iOS simulator
echo "127.0.0.1 milo.crabdance.com" | sudo tee -a /etc/hosts
sudo minikube tunnel   # keeps running — use a dedicated terminal
```

> **Why Docker instead of qemu2?** The Docker driver (via colima) provides stable
> networking through `minikube tunnel`, which assigns `127.0.0.1` as the external IP
> for the ingress. No more fragile `kubectl port-forward` + `pf` redirect that dies
> on sleep/wake. See the [Appendix in the K8s runbook](../k8infra/README-k8s-local.md#appendix-qemu2-driver-legacy)
> if you must use qemu2.

Verify:

```bash
curl -sk https://milo.crabdance.com/mbooks-1/rest/book/movies | head -c 200
# Should return JSON array of movies
```

### Step 1 — iOS dependencies

The app uses **Carthage** (`../SwiftCinemas/Cartfile`). Make sure the frameworks are built once (see the iOS project for
details); the Appium tests do not depend on how they are resolved.

### Step 2 — Build the iOS app for the simulator

Build the `SwiftCinemas` scheme from `SwiftCinemas.xcodeproj` for the iOS Simulator:

```bash
cd ../SwiftCinemas

xcodebuild build \
  -project SwiftCinemas.xcodeproj \
  -scheme SwiftCinemas \
  -sdk iphonesimulator \
  -configuration Debug \
  -derivedDataPath build \
  -quiet \
  ONLY_ACTIVE_ARCH=YES \
  CODE_SIGNING_ALLOWED=NO
```

The `.app` bundle (bundle id `com.georgegaspar.SwiftLoginScreen`) will be at:

```
SwiftCinemas/build/Build/Products/Debug-iphonesimulator/SwiftCinemas.app
```

> **First build** takes several minutes. Subsequent builds are incremental and much faster.

#### Build flags explained

| Flag                      | Purpose                                                                         |
|---------------------------|---------------------------------------------------------------------------------|
| `-sdk iphonesimulator`    | Build for simulator, not a real device                                          |
| `ONLY_ACTIVE_ARCH=YES`    | Build only for the host architecture (arm64 on Apple Silicon)                   |
| `CODE_SIGNING_ALLOWED=NO` | Skip code signing (not needed for simulator)                                    |
| `-derivedDataPath build`  | Keep build artifacts in-tree instead of `~/Library/Developer/Xcode/DerivedData` |
| `-quiet`                  | Suppress verbose build output; errors still shown                               |

### Step 3 — Create and boot a simulator

List available simulators:

```bash
xcrun simctl list devices available | grep iPhone
```

Boot one (use the UDID from the listing):

```bash
xcrun simctl boot <UDID>
```

Open the Simulator UI:

```bash
open -a Simulator
```

Verify it's booted:

```bash
xcrun simctl list devices booted
```

Example output:

```
-- iOS 26.1 --
    iPhone 16e (D7A0E565-5DEF-488A-B770-4A35578775FE) (Booted)
```

### Step 4 — Install the app on the simulator

```bash
xcrun simctl install booted ../SwiftCinemas/build/Build/Products/Debug-iphonesimulator/SwiftCinemas.app
```

Or targeting a specific simulator by UDID:

```bash
xcrun simctl install <UDID> ../SwiftCinemas/build/Build/Products/Debug-iphonesimulator/SwiftCinemas.app
```

### Step 5 — Start the Appium server

```bash
appium --relaxed-security --log-timestamp --local-timezone
```

Or on a custom port/address:

```bash
appium -p 4724 -a 0.0.0.0 --relaxed-security
```

Verify it's running:

```bash
curl http://127.0.0.1:4723/status
# {"value":{"ready":true,"message":"The server is ready to accept new connections","build":{"version":"3.2.2"}}}
```

> **Tip:** Run Appium in a separate terminal window so you can see its log output during test execution.

#### Useful Appium CLI flags

| Flag                  | Purpose                                                       |
|-----------------------|---------------------------------------------------------------|
| `-p <port>`           | Listen on a specific port (default: `4723`)                   |
| `-a <address>`        | Bind to a specific IP (default: `0.0.0.0`)                    |
| `--base-path <path>`  | Set the base path (default: `/`)                              |
| `--relaxed-security`  | Allow insecure features (needed for some XCUITest operations) |
| `--log <file>`        | Write server log to a file                                    |
| `--log-timestamp`     | Add timestamps to log output                                  |
| `--local-timezone`    | Use local timezone for timestamps                             |
| `--log-level <level>` | Set log level: `debug`, `info`, `warn`, `error`               |

### Step 6 — Run the tests

The app is already installed on the simulator (Step 4), so no `.app` path is needed: the driver
launches it by bundle id. Resolution order is `APP_PATH` → TestNG `app` parameter (only if the file
exists) → installed app via `BUNDLE_ID` (default from `properties.properties`). If the app is not
installed on the target simulator, the run fails fast with a `simctl install` hint.

#### Option A — Maven (command line)

Run against the booted simulator (auto-discovered via `simctl` when only one is booted):

```bash
cd appium
mvn clean test
```

Pin a specific simulator and bundle id (system properties or env vars — same keys):

```bash
mvn clean test \
  -Dsim.device="iPhone 16e" \
  -Dsim.version="26.1" \
  -Dsim.udid="D7A0E565-5DEF-488A-B770-4A35578775FE" \
  -Dbundle.id=com.georgegaspar.SwiftLoginScreen

# equivalent, CI style
SIM_UDID=D7A0E565-5DEF-488A-B770-4A35578775FE \
BUNDLE_ID=com.georgegaspar.SwiftLoginScreen \
mvn -B clean test
```

Let Appium (re)install a fresh build instead of using the installed app:

```bash
mvn clean test -Dsim.udid="<UDID>" \
  -Dapp.path=../SwiftCinemas/build/Build/Products/Debug-iphonesimulator/SwiftCinemas.app
```

If Appium is on a non-default port/host:

```bash
mvn clean test -Dsim.udid="<UDID>" -Dappium.port=4724 -Dappium.host=192.168.1.100
```

If behind a corporate proxy, use the bundled Maven settings:

```bash
mvn -s ../k8infra/settings-local.xml clean test -Dsim.udid="<UDID>"
```

#### Option B — IntelliJ run configurations

Shared run configurations in `.run/` are auto-detected by IntelliJ. No `mvnw` wrapper exists for `appium/` — all configs
use the system `mvn` or run `java` directly.

| Run configuration                  | Purpose                                                                                                                                       |
|------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|
| **appium - compile**               | Compile main + test classes (`compile test-compile -DskipTests`). Prerequisite for java-direct configs.                                       |
| **appium - mvn test**              | Run `testng.xml` via Maven Surefire                                                                                                           |
| **appium - mvn test (inspect)**    | Run `testng-inspect.xml` (InspectUI page-source dump)                                                                                         |
| **appium - java direct (testng)**  | Bypass `exec-maven-plugin`: resolves classpath via `mvn dependency:build-classpath`, then runs `java … org.testng.TestNG testng.xml` directly |
| **appium - java direct (inspect)** | Same direct approach for `testng-inspect.xml`                                                                                                 |
| **appium - CreateXml GUI**         | Launch the `qa.ios.util.CreateXml` Swing GUI for interactive TestNG XML suite creation                                                        |

> **Corporate proxy note:** Maven-type run configs set the user settings file via `myGeneralSettings` →
> `userSettingsFile` pointing to `$PROJECT_DIR$/k8infra/settings-local.xml`. Do **not** put `-s` or `--settings` in
> `cmdOptions` — IntelliJ's Maven runner misinterprets it as `-f/--file`.

> **CreateXml GUI:** The classpath includes `target/test-classes` so `Class.forName()` finds test classes like
> `TestNavigations`. Run **appium - compile** first to populate the target directories.

### Step 7 — Check results

Test reports are at:

```
appium/target/surefire-reports/
```

Appium server log: the terminal running Appium, or the file given with `--log`.

---

## Quick reference — all-in-one

Once everything is installed, the full pipeline is (run from `appium/`):

```bash
# 1. Build the app
(cd ../SwiftCinemas && xcodebuild build -project SwiftCinemas.xcodeproj -scheme SwiftCinemas \
  -sdk iphonesimulator -configuration Debug -derivedDataPath build -quiet \
  ONLY_ACTIVE_ARCH=YES CODE_SIGNING_ALLOWED=NO)

# 2. Boot the simulator
xcrun simctl boot <UDID>
xcrun simctl bootstatus <UDID> -b

# 3. Install the app
xcrun simctl install <UDID> ../SwiftCinemas/build/Build/Products/Debug-iphonesimulator/SwiftCinemas.app

# 4. Appium (in a separate terminal), then wait until ready
appium --relaxed-security --log-timestamp --local-timezone
curl -s http://127.0.0.1:4723/status

# 5. Tests — launches the installed app by bundle id
SIM_UDID=<UDID> mvn clean test
```

For re-runs (app already built & installed, Appium already running):

```bash
SIM_UDID=<UDID> mvn clean test
```

Or use the IntelliJ run configurations: **appium - mvn test** or **appium - java direct (testng)**.
CI runs the same flow on a dedicated, throw-away simulator — see `.github/mobile-smoke-test.yaml`.

---

## WebDriverAgent (WDA) — how it works

The XCUITest driver uses **WebDriverAgent** to control the iOS Simulator. Understanding this is key to troubleshooting.

### Lifecycle (managed by Appium)

1. Appium's XCUITest driver **builds** WDA from source using Xcode
2. **Installs** the WDA runner app on the simulator
3. **Launches** it — WDA starts an HTTP server on the simulator
4. Appium **discovers** the host:port and proxies all commands through it

This all happens automatically when you create a session. The first run takes longer because WDA must be compiled.

### Important: do NOT set `webDriverAgentUrl` for simulators

Setting the `webDriverAgentUrl` capability tells Appium: "WDA is already running at this URL — don't build or start it,
just connect." If nothing is actually listening there, you get:

```
ETIMEDOUT 192.168.0.103:8100
```

The `DriverManager` is configured to let Appium handle WDA automatically (`usePreinstalledWDA: false`, no
`webDriverAgentUrl`).

### WDA build cache

After the first successful session, WDA is cached. Subsequent sessions reuse the cached build and start much faster. If
WDA seems stuck:

```bash
# Clear the WDA build cache
rm -rf ~/Library/Developer/Xcode/DerivedData/WebDriverAgent-*

# Or uninstall WDA from the simulator
xcrun simctl uninstall <UDID> com.facebook.WebDriverAgentRunner.xctrunner
```

---

## Configuration reference

### Files

| File                        | Purpose                                                                    |
|-----------------------------|----------------------------------------------------------------------------|
| `testng.xml`                | Test suite — device type, app path (relative), test classes to run         |
| `properties.properties`     | Bundle ID, Appium server connection, ideviceinfo path                      |
| `pom.xml`                   | Maven dependencies, surefire config (`-D` flags reach tests automatically) |
| `src/resources/logback.xml` | Logging configuration (SLF4J + Logback)                                    |

### Runtime properties

Every key is resolved by `PropertyUtils` in this order (first non-blank wins):

1. JVM system property: `-Dkey=value`
2. Environment variable: key upper-cased, `.`/`-` → `_` (e.g. `appium.port` → `APPIUM_PORT`)
3. `properties.properties`
4. Code default

Blank values and unresolved Maven placeholders (`${...}`) count as *unset*, so empty pom defaults never mask env vars.
Non-numeric ports and missing app bundles fail fast with a message naming the key.
URL parsing and connection errors propagate from Java and Appium without custom validation.

| Property          | Env var           | Default                                     | Purpose                                                                               |
|-------------------|-------------------|---------------------------------------------|---------------------------------------------------------------------------------------|
| `sim.device`      | `SIM_DEVICE`      | booted simulator → `sim.defaultDevice`     | Simulator device name                                                                 |
| `sim.defaultDevice` | `SIM_DEFAULTDEVICE` | `iPhone 16 Pro` (`properties.properties`) | Fallback device name; required when no device name is configured or discovered |
| `sim.version`     | `SIM_VERSION`     | booted simulator → latest                   | iOS version                                                                           |
| `sim.udid`        | `SIM_UDID`        | booted simulator                            | Simulator UDID; when set, forces simulator mode                                       |
| `app.path`        | `APP_PATH`        | TestNG `app` parameter (if file exists)     | Optional `.app` bundle (absolute or relative to `appium/`); explicit value must exist |
| `appium.host`     | `APPIUM_HOST`     | `127.0.0.1`                                 | Appium server host                                                                    |
| `appium.port`     | `APPIUM_PORT`     | `4723`                                      | Appium server port (1–65535)                                                          |
| `appium.basePath` | `APPIUM_BASEPATH` | `/`                                         | Appium server base path                                                               |
| `bundle.id`       | `BUNDLE_ID`       | legacy `bundleid` (`properties.properties`) | App bundle id; launches the installed app when no `.app` is given                     |
| `ideviceinfo`     | `IDEVICEINFO`     | `ideviceinfo`                               | Path to libimobiledevice `ideviceinfo` (optional)                                     |

**Simulator auto-discovery:** if none of `sim.device`/`sim.version`/`sim.udid` is set, the booted simulator
(`xcrun simctl list devices booted`, highest iOS runtime) is used. Explicit values are never mixed with discovered ones.
The fallback device name comes from `sim.defaultDevice` in `properties.properties`; it does not disable auto-discovery.
Like other properties, it can be overridden using `-Dsim.defaultDevice` or `SIM_DEFAULTDEVICE`.

```bash
# Local: boot a simulator, then just
mvn clean test
# CI: app pre-installed on a dedicated simulator (no APP_PATH)
xcrun simctl install <UDID> /path/to/App.app
SIM_UDID=<UDID> BUNDLE_ID=com.georgegaspar.SwiftLoginScreen mvn -B clean test
# Or let Appium install a build
SIM_UDID=<UDID> APP_PATH=/path/to/App.app mvn -B clean test
```

**App resolution (simulator):** `APP_PATH` → TestNG `app` parameter (only if the file exists, otherwise a warning) →
installed app launched by bundle id. With a known UDID, the bundle id is checked via `xcrun simctl get_app_container`
and the run fails fast with an install hint if the app is missing. See `.github/mobile-smoke-test.yaml` for the CI flow.

### Simulator vs. real device

`DriverManager` auto-detects: if `ideviceinfo` finds a connected USB device (and `sim.udid` is not set), it uses
real-device capabilities (UDID, signing identity, `xcodeOrgId`). If `ideviceinfo` is not installed (typical on CI) or
no device is attached, it uses simulator mode.

---

## Test flow

The `TestNavigations.pageObject()` test exercises:

1. **Home screen** — swipe gesture on the landing page
2. **Movie selection** — navigate to Movies → Categories → Drama → "12 Angry Men"
3. **Venue selection** — tap "Cinema City Allee"
4. **Screening date picker** — select a date from the picker wheel
5. **Seat booking** — open the seat popover, select seats A1/A2/A3, scroll to row B, add B5
6. **Checkout** — tap CheckOut (expects 502 when backend is not running)

---

## Troubleshooting

### `SessionNotCreatedException: Unable to start WebDriverAgent session`

- **"Application is unknown to FrontBoard"** — The app is not installed on the simulator. Run `xcrun simctl install`.
- **`ETIMEDOUT` on port 8100** — Remove any `webDriverAgentUrl` capability; let Appium manage WDA.
- **WDA build fails** — Run `appium driver update xcuitest` and clear the WDA cache (see above).

### `NoSuchElement` during test execution

The app launched successfully but the UI element wasn't found. Possible causes:

- Screen layout differs from expected (different device size, iOS version)
- Animation not finished — increase implicit wait or add explicit waits
- App state changed — the element locators may need updating

### Appium server not reachable

```bash
# Check if Appium is running
curl http://127.0.0.1:4723/status

# Check what's on port 4723
lsof -i :4723
```

### Wrong architecture

The `.app` binary must match the simulator platform:

- **Apple Silicon Mac** (M1/M2/M3/M4) → `arm64` simulator build
- **Intel Mac** → `x86_64` simulator build

Check with: `file SwiftCinemas.app/SwiftCinemas`

Build with `ONLY_ACTIVE_ARCH=YES` to match the host machine.

---

## Dependencies

| Library            | Version        | Purpose                                 |
|--------------------|----------------|-----------------------------------------|
| Appium Java Client | 11.0.0         | iOS automation (W3C protocol, XCUITest) |
| Selenium           | 4.51.0         | WebDriver foundation (aligned via BOM)  |
| TestNG             | 7.12.0         | Test framework + reporting              |
| SLF4J + Logback    | 2.0.20 / 1.6.5 | Logging                                 |
| Commons IO         | 2.22.0         | Screenshot file handling                |
| Commons Lang       | 3.21.0         | String utilities                        |

Build plugins: `maven-compiler-plugin` 3.16.0 (Java 17), `maven-surefire-plugin` 3.5.6.
Java client 11 requires an **Appium 3** server. Surefire is intentionally kept on 3.5.x:
3.6 runs TestNG through the JUnit Platform provider, so upgrade it deliberately.

## Migration notes (from original project)

Upgraded from Java 8 / Appium 7.6 / Selenium 2.53:

- **Java 8 → 17**, **Appium Java Client 7.6.0 → 9.3.0**, **Selenium 2.53 → 4.27**, **TestNG 6.9 → 7.10**
- **log4j 1.x → SLF4J 2 + Logback 1.5**
- `IOSElement` / `MobileElement` → `WebElement`
- `IOSDriver<IOSElement>` → `IOSDriver` (no longer generic)
- `DesiredCapabilities` → `XCUITestOptions`
- `MobileBy` → `AppiumBy`
- `findElementByAccessibilityId()` → `findElement(AppiumBy.accessibilityId())`
- `findElementByIosClassChain()` → `findElement(AppiumBy.iOSClassChain())`
- `findElementByIosNsPredicate()` → `findElement(AppiumBy.iOSNsPredicateString())`
- `closeApp()` / `resetApp()` / `launchApp()` → `terminateApp()` / `activateApp()`
- `TimeUnit`-based timeouts → `Duration`-based
- ReportNG removed (abandoned) — uses built-in TestNG HTML reporter

## Tested on

- macOS Sequoia 26.x (Apple Silicon)
- Xcode 16+ / iOS 26.1 simulator
- Appium 3.2.2 / XCUITest driver 10.32.1
