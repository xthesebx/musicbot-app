package Discord.OS;

import java.util.Locale;

/**
 * <p>OperatingSystem class.</p>
 *
 * @author xXTheSebXx
 * @version 1.0-SNAPSHOT
 */
public class OperatingSystem {
    /** Constant <code>detectedOS</code> */
    protected static OSType detectedOS;

    ;

    /**
     * <p>getOperatingSystemType.</p>
     *
     * @return a {@link Discord.OS.OperatingSystem.OSType} object
     */
    public static OSType getOperatingSystemType() {
        if (detectedOS == null) {
            String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
            if ((OS.contains("mac")) || (OS.contains("darwin"))) {
                detectedOS = OSType.MAC;
            } else if (OS.contains("win")) {
                detectedOS = OSType.WINDOWS;
            } else if (OS.contains("nux") || OS.contains("nix")) {
                detectedOS = OSType.LINUX;
            } else {
                detectedOS = OSType.UNKNOWN;
            }
        }
        return detectedOS;
    }

    /**
     * enum for different OS types
     */
    public enum OSType {
        /**
         * Windows
         */
        WINDOWS,
        /**
         * Mac
         */
        MAC,
        /**
         * Linux
         */
        LINUX,
        /**
         * no idea man
         */
        UNKNOWN
    }
}
