package Discord.OS.process.impl;


import Discord.OS.process.ProcessReference;

/**
 * Created: 29/09/2023 15:28
 * Author: Twitter @hawolt
 *
 * @author xXTheSebXx
 * @version 1.0-SNAPSHOT
 */
public class WindowsProcess extends ProcessReference {
    /**
     * <p>Constructor for WindowsProcess.</p>
     *
     * @param line a {@link java.lang.String} object
     */
    public WindowsProcess(String line) {
        super(line);
    }

    /** {@inheritDoc} */
    @Override
    public void configure(String line) {
        int lastIndex = line.lastIndexOf(" ");
        String pid = line.substring(lastIndex + 1);
        String remainder = line.substring(0, lastIndex);
        this.name = remainder.trim();
        this.pid = Integer.parseInt(pid);
    }
}
