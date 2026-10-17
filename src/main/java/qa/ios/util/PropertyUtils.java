package qa.ios.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qa.ios.base.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Properties;

/**
 * Central configuration resolver.
 * <p>
 * Precedence (first non-blank wins): JVM system property ({@code -Dkey}) → environment variable ({@code KEY} with dots replaced by
 * underscores, upper-cased, e.g. {@code APPIUM_PORT}) → {@code properties.properties} → caller default. Blank values and unresolved Maven
 * placeholders ({@code ${...}}) are treated as unset.
 * <p>
 * Deliberately does not extend {@code TestBase}: TestBase reads properties during its own static initialisation, so inheriting from it
 * created a class-initialisation cycle.
 */
public final class PropertyUtils implements Constants {

    private static final Logger Log = LoggerFactory.getLogger(PropertyUtils.class);
    private static final Properties FILE_PROPS = new Properties();

    static {
        Path file = Paths.get(System.getProperty("user.dir"), proprtyFile);
        if(Files.isRegularFile(file)) {
            try(InputStream in = Files.newInputStream(file)) {
                FILE_PROPS.load(in);
                Log.info("{} is loaded.", file);
            }
            catch(IOException e) {
                throw new IllegalStateException("Unable to read property file " + file, e);
            }
        }
        else {
            Log.warn("Property file {} not found; using system properties, environment and defaults only.", file);
        }
    }

    private PropertyUtils() {
    }

    /**
     * Resolve a value using the documented precedence; returns {@code null} when unset everywhere.
     */
    public static String getProperty(String key) {
        return resolve(key, null);
    }

    public static String resolve(String key, String defaultValue) {
        String k = key.trim();

        String value = clean(System.getProperty(k));
        if(value != null) {
            Log.debug("{} resolved from system property", k);
            return value;
        }
        value = clean(System.getenv(envName(k)));
        if(value != null) {
            Log.debug("{} resolved from environment variable {}", k, envName(k));
            return value;
        }
        value = clean(FILE_PROPS.getProperty(k));
        if(value != null) {
            Log.debug("{} resolved from {}", k, proprtyFile);
            return value;
        }
        if(defaultValue == null) {
            Log.debug("{} is not set", k);
        }
        return defaultValue;
    }

    /**
     * Resolve an integer value; fails fast with a descriptive message when the value is not numeric.
     */
    public static int resolveInt(String key, int defaultValue) {
        String value = resolve(key, null);
        if(value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        }
        catch(NumberFormatException e) {
            throw new IllegalStateException("Property '" + key + "' must be an integer but was '" + value + "'", e);
        }
    }

    /**
     * Resolve a mandatory value; fails fast naming every place it could have been supplied.
     */
    public static String required(String key) {
        String value = resolve(key, null);
        if(value == null) {
            throw new IllegalStateException("Required property '" + key + "' is not set. Provide -D" + key
                                                + ", environment variable " + envName(key) + " or an entry in " + proprtyFile);
        }
        return value;
    }

    public static void setProperty(String propertyKey, String value) {
        FILE_PROPS.setProperty(propertyKey, value);
    }

    public static void listProperties() {
        FILE_PROPS.stringPropertyNames().stream().sorted()
                  .forEach(k -> Log.info("{} -- {}", k, FILE_PROPS.getProperty(k)));
    }

    static String envName(String key) {
        return key.trim().replace('.', '_').replace('-', '_').toUpperCase(Locale.ROOT);
    }

    private static String clean(String raw) {
        if(raw == null) {
            return null;
        }
        String v = raw.trim();
        if(v.isEmpty() || (v.startsWith("${") && v.endsWith("}"))) {
            return null;
        }
        return v;
    }
}
