# Appium TestNG — SwiftCinemas iOS UI Tests

Automated UI tests for the SwiftCinemas iOS app using **Appium 2** + **XCUITest** driver + **TestNG**.

> **⚠️ Backend required:** The test flow navigates Movies → Categories → Venues → Seats → Checkout.
> Every screen after the home page fetches data from the backend API. Without a running
> Cinemas system (MySQL, Kafka, mbooks, dalogin, etc.), the Movies table is empty, the
> segmented control never renders, and all tests fail with `NoSuchElement`.
>
> See [Backend setup](#step-0--start-the-cinemas-backend) below, or the
> [full test fix plan](../docs/appium-test-fix-plan.md) for details.

---

## Prerequisites

Install these before running the tests:

| Tool | Install command | Verify |
|------|----------------|--------|
| **Java 17** | (via SDKMAN, Homebrew, or Oracle) | `java -version` |
| **Maven 3.9+** | `brew install maven` | `mvn -version` |
| **Xcode 16+** | Mac App Store | `xcodebuild -version` |
| **Xcode CLI tools** | `xcode-select --install` | `xcode-select -p` |
| **CocoaPods** | `gem install cocoapods` | `pod --version` |
| **Node.js 18+** | `brew install node` | `node -v` |
| **Appium 3** | `npm install -g appium` | `appium -v` |
| **XCUITest driver** | `appium driver install xcuitest` | `appium driver list --installed` |

---

## Test preparation — step by step

### Step 0 — Start the Cinemas backend

The iOS app connects to `https://milo.crabdance.com`. The full local Kubernetes stack
must be running. Follow the [local K8s runbook](../k8infra/README-k8s-local.md) or these
condensed steps:

```bash
# Start Minikube
minikube start --driver=qemu2 --cpus=4 --memory=8192
minikube addons enable ingress

# Build & deploy (from repo root)
(cd dalogin-quarkus            && ./mvnw package -DskipTests)
(cd mbook-quarkus              && ./mvnw package -DskipTests)
(cd mbooks-quarkus             && ./mvnw package -DskipTests)
(cd simple-service-webapp-quarkus && ./mvnw package -DskipTests)

for img in dalogin-quarkus mbook-quarkus mbooks-quarkus simple-service-webapp-quarkus; do
  podman build -t ${img}:local ./${img}
  podman save -o /tmp/${img}.tar localhost/${img}:local
  minikube image load /tmp/${img}.tar
  minikube ssh -- sudo ctr -n k8s.io images tag \
    localhost/${img}:local docker.io/library/${img}:local
done

kubectl apply -f k8infra/quarkus-backend.yaml
kubectl -n cinemas get pods   # wait until all Running

# Seed databases
kubectl -n cinemas exec -i deploy/mysql -- mysql -uroot -prootpw < mysql_8/login.sql
kubectl -n cinemas exec -i deploy/mysql -- mysql -uroot -prootpw < mysql_8/book.sql
kubectl -n cinemas exec -i deploy/mysql -- mysql -uroot -prootpw < k8infra/fix-sprocs.sql
kubectl -n cinemas exec -i deploy/mysql -- mysql -uroot -prootpw < k8infra/fix-triggers.sql

# HTTPS access for the iOS simulator
echo "127.0.0.1 milo.crabdance.com" | sudo tee -a /etc/hosts
kubectl port-forward -n ingress-nginx svc/ingress-nginx-controller 8443:443 &
echo "rdr pass on lo0 inet proto tcp from any to 127.0.0.1 port 443 -> 127.0.0.1 port 8443" \
  | sudo pfctl -ef -
```

Verify:
```bash
curl -sk https://milo.crabdance.com/mbooks-1/rest/book/movies | head -c 200
# Should return JSON array of movies
```

### Step 1 — Install CocoaPods dependencies

The iOS app depends on several pods (Braintree, Firebase, Realm, etc.). These must be installed before building.

```bash
cd ../SwiftCinemas
pod install
```

Expected output: `Pod installation complete! There are 8 dependencies from the Podfile and 27 total pods installed.`

> **Note:** Always open the `.xcworkspace` (not `.xcodeproj`) after running `pod install`.

### Step 2 — Build the iOS app for the simulator

Build the `SwiftCinemas` scheme from the `SwiftLoginScreen.xcworkspace` for the iOS Simulator:

```bash
cd ../SwiftCinemas

xcodebuild build \
  -workspace SwiftLoginScreen.xcworkspace \
  -scheme SwiftCinemas \
  -sdk iphonesimulator \
  -configuration Debug \
  -derivedDataPath build \
  -quiet \
  ONLY_ACTIVE_ARCH=YES \
  CODE_SIGNING_ALLOWED=NO
```

The `.app` bundle will be at:
```
SwiftCinemas/build/Build/Products/Debug-iphonesimulator/SwiftCinemas.app
```

> **First build** takes several minutes (compiles gRPC, Firebase, BoringSSL, etc.).
> Subsequent builds are incremental and much faster.

#### Build flags explained

| Flag | Purpose |
|------|---------|
| `-sdk iphonesimulator` | Build for simulator, not a real device |
| `ONLY_ACTIVE_ARCH=YES` | Build only for the host architecture (arm64 on Apple Silicon) |
| `CODE_SIGNING_ALLOWED=NO` | Skip code signing (not needed for simulator) |
| `-derivedDataPath build` | Keep build artifacts in-tree instead of `~/Library/Developer/Xcode/DerivedData` |
| `-quiet` | Suppress verbose build output; errors still shown |

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

| Flag | Purpose |
|------|---------|
| `-p <port>` | Listen on a specific port (default: `4723`) |
| `-a <address>` | Bind to a specific IP (default: `0.0.0.0`) |
| `--base-path <path>` | Set the base path (default: `/`) |
| `--relaxed-security` | Allow insecure features (needed for some XCUITest operations) |
| `--log <file>` | Write server log to a file |
| `--log-timestamp` | Add timestamps to log output |
| `--local-timezone` | Use local timezone for timestamps |
| `--log-level <level>` | Set log level: `debug`, `info`, `warn`, `error` |

### Step 6 — Copy the app and run the tests

Copy the freshly-built `.app` into the `appium/` directory (the relative path in `testng.xml` resolves from here):
```bash
cp -R ../SwiftCinemas/build/Build/Products/Debug-iphonesimulator/SwiftCinemas.app .
```

Run the tests, passing the simulator details:
```bash
mvn clean test \
  -Dsim.device="iPhone 16e" \
  -Dsim.version="26.1" \
  -Dsim.udid="D7A0E565-5DEF-488A-B770-4A35578775FE"
```

If Appium is on a non-default port/host:
```bash
mvn clean test \
  -Dsim.device="iPhone 16e" \
  -Dsim.version="26.1" \
  -Dsim.udid="D7A0E565-5DEF-488A-B770-4A35578775FE" \
  -Dappium.port=4724 \
  -Dappium.host=192.168.1.100
```

### Step 7 — Check results

Test reports are at:
```
appium/target/surefire-reports/
```

---

## Quick reference — all-in-one

Once everything is installed, the full pipeline is:

```bash
# 1. Pods
cd ../SwiftCinemas && pod install

# 2. Build
xcodebuild build -workspace SwiftLoginScreen.xcworkspace -scheme SwiftCinemas \
  -sdk iphonesimulator -derivedDataPath build -quiet \
  ONLY_ACTIVE_ARCH=YES CODE_SIGNING_ALLOWED=NO

# 3. Sim
xcrun simctl boot <UDID>

# 4. Install app
xcrun simctl install <UDID> build/Build/Products/Debug-iphonesimulator/SwiftCinemas.app

# 5. Appium (in a separate terminal)
appium --relaxed-security

# 6. Tests
cd ../appium
cp -R ../SwiftCinemas/build/Build/Products/Debug-iphonesimulator/SwiftCinemas.app .
mvn clean test -Dsim.device="iPhone 16e" -Dsim.version="26.1" -Dsim.udid="<UDID>"
```

For re-runs (app already built & installed, Appium already running):
```bash
cd appium && mvn clean test -Dsim.device="iPhone 16e" -Dsim.version="26.1" -Dsim.udid="<UDID>"
```

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

Setting the `webDriverAgentUrl` capability tells Appium: "WDA is already running at this URL — don't build or start it, just connect." If nothing is actually listening there, you get:

```
ETIMEDOUT 192.168.0.103:8100
```

The `DriverManager` is configured to let Appium handle WDA automatically (`usePreinstalledWDA: false`, no `webDriverAgentUrl`).

### WDA build cache

After the first successful session, WDA is cached. Subsequent sessions reuse the cached build and start much faster. If WDA seems stuck:

```bash
# Clear the WDA build cache
rm -rf ~/Library/Developer/Xcode/DerivedData/WebDriverAgent-*

# Or uninstall WDA from the simulator
xcrun simctl uninstall <UDID> com.facebook.WebDriverAgentRunner.xctrunner
```

---

## Configuration reference

### Files

| File | Purpose |
|------|---------|
| `testng.xml` | Test suite — device type, app path (relative), test classes to run |
| `properties.properties` | Bundle ID, Appium server connection, ideviceinfo path |
| `pom.xml` | Maven dependencies, surefire plugin config, default sim/appium properties |
| `src/resources/logback.xml` | Logging configuration (SLF4J + Logback) |

### System properties (pass via `-D`)

| Property | Default | Purpose |
|----------|---------|---------|
| `sim.device` | `iPhone 16 Pro` | Simulator device name |
| `sim.version` | `26.1` | iOS version |
| `sim.udid` | *(empty)* | Simulator UDID (recommended — avoids name ambiguity) |
| `appium.host` | `127.0.0.1` | Appium server host |
| `appium.port` | `4723` | Appium server port |
| `appium.basePath` | `/` | Appium server base path |

System properties override `properties.properties` values, which override hardcoded defaults.

### Simulator vs. real device

`DriverManager` auto-detects: if `ideviceinfo` finds a connected USB device, it uses real-device capabilities (UDID, signing identity, `xcodeOrgId`). Otherwise it falls back to simulator mode with the `.app` bundle path from `testng.xml`.

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

| Library | Version | Purpose |
|---------|---------|---------|
| Appium Java Client | 9.3.0 | iOS automation (W3C protocol, XCUITest) |
| Selenium | 4.27.0 | WebDriver foundation |
| TestNG | 7.10.2 | Test framework + reporting |
| SLF4J + Logback | 2.0.16 / 1.5.12 | Logging |
| Commons IO | 2.18.0 | Screenshot file handling |
| Commons Lang | 3.17.0 | String utilities |

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
