package qa.ios.base;

import org.openqa.selenium.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qa.ios.util.PropertyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Device discovery only: real USB devices via {@code ideviceinfo} and booted simulators via {@code xcrun simctl}. Capability decisions live
 * in {@link DriverManager}.
 * <p>
 * Deliberately does not extend {@link TestBase}: TestBase triggers discovery, so inheriting from it created a static-initialisation cycle.
 */
public class DeviceConfiguration {

    private static final Logger Log = LoggerFactory.getLogger(DeviceConfiguration.class);

    private static final Pattern RUNTIME_VERSION = Pattern.compile("iOS-(\\d+(?:-\\d+)*)$");

    CommandPrompt cmd = new CommandPrompt();
    Map<String, String> devices = new HashMap<String, String>();

    /**
     * Picks a booted iOS simulator: highest iOS version, then device name, then UDID (deterministic). Returns empty when {@code xcrun} is
     * unavailable or nothing is booted.
     */
    @SuppressWarnings("unchecked")
    public static Optional<BootedSimulator> findBootedSimulator() {
        String simulatorDevicesJson;
        try {
            Process simulatorDiscoveryProcess = new ProcessBuilder("xcrun", "simctl", "list", "devices", "booted", "-j")
                            .redirectErrorStream(false)
                            .start();
            try(InputStream simulatorOutputStream = simulatorDiscoveryProcess.getInputStream()) {
                simulatorDevicesJson = new String(simulatorOutputStream.readAllBytes(), StandardCharsets.UTF_8);
            }
            if(!simulatorDiscoveryProcess.waitFor(30, TimeUnit.SECONDS) || simulatorDiscoveryProcess.exitValue() != 0) {
                Log.info("xcrun simctl did not succeed; skipping simulator auto-discovery.");
                return Optional.empty();
            }
        }
        catch(IOException discoveryException) {
            Log.info("xcrun is not available ({}); skipping simulator auto-discovery.", discoveryException.getMessage());
            return Optional.empty();
        }
        catch(InterruptedException discoveryInterruptedException) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        }

        List<BootedSimulator> bootedSimulators = new ArrayList<>();
        Map<String, Object> simulatorDiscoveryResponse = new Json().toType(simulatorDevicesJson, Map.class);
        Object devicesByRuntimeValue = simulatorDiscoveryResponse.get("devices");
        if(devicesByRuntimeValue instanceof Map<?, ?> devicesByRuntime) {
            for(Map.Entry<?, ?> runtimeDevicesEntry: devicesByRuntime.entrySet()) {
                Matcher runtimeVersionMatcher = RUNTIME_VERSION.matcher(String.valueOf(runtimeDevicesEntry.getKey()));
                if(!runtimeVersionMatcher.find() || !(runtimeDevicesEntry.getValue() instanceof List<?> runtimeDevices)) {
                    continue;
                }
                String iosVersion = runtimeVersionMatcher.group(1).replace('-', '.');
                for(Object runtimeDevice: runtimeDevices) {
                    if(runtimeDevice instanceof Map<?, ?> simulatorDetails && "Booted".equals(simulatorDetails.get("state"))) {
                        bootedSimulators.add(new BootedSimulator(String.valueOf(simulatorDetails.get("name")), iosVersion, String.valueOf(simulatorDetails.get("udid"))));
                    }
                }
            }
        }

        Optional<BootedSimulator> selectedSimulator = bootedSimulators.stream()
                                               .max(Comparator.comparing((BootedSimulator simulator) -> versionKey(simulator.version()))
                                                              .thenComparing(BootedSimulator::name, Comparator.reverseOrder())
                                                              .thenComparing(BootedSimulator::udid, Comparator.reverseOrder()));
        selectedSimulator.ifPresentOrElse(
            simulator -> Log.info("Auto-discovered booted simulator: {} (iOS {}) {} [{} booted]", simulator.name(), simulator.version(), simulator.udid(), bootedSimulators.size()),
            () -> Log.info("No booted iOS simulator found.")
        );
        return selectedSimulator;
    }

    /**
     * Checks via {@code simctl get_app_container} whether {@code bundleId} is installed on the simulator. Empty when {@code xcrun} is
     * unavailable, so callers can let Appium report the problem instead.
     */
    public static Optional<Boolean> isAppInstalled(String udid, String bundleId) {
        try {
            Process installedAppCheckProcess = new ProcessBuilder("xcrun", "simctl", "get_app_container", udid, bundleId)
                            .redirectErrorStream(true)
                            .start();
            installedAppCheckProcess.getInputStream().readAllBytes();
            if(!installedAppCheckProcess.waitFor(30, TimeUnit.SECONDS)) {
                installedAppCheckProcess.destroyForcibly();
                return Optional.empty();
            }
            return Optional.of(installedAppCheckProcess.exitValue() == 0);
        }
        catch(IOException installedAppCheckException) {
            Log.info("xcrun is not available ({}); skipping installed-app check.", installedAppCheckException.getMessage());
            return Optional.empty();
        }
        catch(InterruptedException installedAppCheckInterruptedException) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        }
    }

    private static String versionKey(String version) {
        StringBuilder sortableVersionKey = new StringBuilder();
        for(String versionComponent: version.split("\\.")) {
            sortableVersionKey.append(String.format("%05d", Integer.parseInt(versionComponent)));
        }
        return sortableVersionKey.toString();
    }

    /**
     * Returns real-device info from {@code ideviceinfo}; an empty map when no device is attached or the tool is not installed (typical on
     * CI runners), so callers fall back to simulator mode.
     */
    public Map<String, String> getDevices() throws Exception {
        String deviceInfoCommand = PropertyUtils.resolve("ideviceinfo", "ideviceinfo");
        String deviceInfoOutput;
        try {
            deviceInfoOutput = cmd.runCommand(deviceInfoCommand);
        }
        catch(IOException deviceInfoException) {
            Log.info("'{}' is not available ({}); assuming no USB device is attached.", deviceInfoCommand, deviceInfoException.getMessage());
            return devices;
        }

        String[] deviceInfoLines = deviceInfoOutput.split("\n");

        if(deviceInfoLines.length <= 1) {
            Log.info("No devices are plugged in");
            return devices;
        }

        for(int deviceInfoLineIndex = 1; deviceInfoLineIndex < deviceInfoLines.length; deviceInfoLineIndex++) {
            if(deviceInfoLines[deviceInfoLineIndex].contains("DeviceName")) {
                TestBase.DeviceName = deviceInfoLines[deviceInfoLineIndex].replaceAll("DeviceName:", "").trim();
                devices.put("deviceName", TestBase.DeviceName);
                Log.info(deviceInfoLines[deviceInfoLineIndex]);
            }
            if(deviceInfoLines[deviceInfoLineIndex].contains("DeviceClass")) {
                TestBase.DeviceClass = deviceInfoLines[deviceInfoLineIndex].replaceAll("DeviceClass:", "").trim();
                devices.put("DeviceClass", TestBase.DeviceClass);
                Log.info(deviceInfoLines[deviceInfoLineIndex]);
            }
            if(deviceInfoLines[deviceInfoLineIndex].contains("UniqueDeviceID")) {
                TestBase.UniqueDeviceID = deviceInfoLines[deviceInfoLineIndex].replaceAll("UniqueDeviceID:", "").trim();
                devices.put("UniqueDeviceID", TestBase.UniqueDeviceID);
                Log.info(deviceInfoLines[deviceInfoLineIndex]);
            }
            if(deviceInfoLines[deviceInfoLineIndex].contains("ProductVersion")) {
                TestBase.ProductVersion = deviceInfoLines[deviceInfoLineIndex].replaceAll("ProductVersion:", "").trim();
                devices.put("ProductVersion", TestBase.ProductVersion);
                Log.info(deviceInfoLines[deviceInfoLineIndex]);
            }
        }
        return devices;
    }

    /**
     * A booted iOS simulator discovered via {@code xcrun simctl}.
     */
    public record BootedSimulator(String name, String version, String udid) {

    }

}
