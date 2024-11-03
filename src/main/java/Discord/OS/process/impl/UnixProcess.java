package Discord.OS.process.impl;

import Discord.OS.process.ProcessReference;


/**
 * <p>UnixProcess class.</p>
 *
 * @author xXTheSebXx
 * @version 1.0-SNAPSHOT
 */
public class UnixProcess extends ProcessReference {

    /**
     * <p>Constructor for UnixProcess.</p>
     *
     * @param line a {@link java.lang.String} object
     */
    public UnixProcess(String line) {
        super(line);
    }

    /** {@inheritDoc} */
    @Override
    public void configure(String line) {
        String[] data = line.split(" ", 12);
        this.name = data[10];
        this.pid = Integer.parseInt(data[1]);
        if (name.startsWith("[") && name.endsWith("]")) {
            this.name = name.substring(1, name.length() - 1);
        } else if (name.contains("/")) {
            this.name = name.substring(name.lastIndexOf("/") + 1);
        }
        if (name.startsWith("-")) {
            this.name = name.substring(1);
        }
        if (name.endsWith(":")) {
            this.name = name.substring(0, name.length() - 1);
        }
        this.name = name.trim();
    }

}
