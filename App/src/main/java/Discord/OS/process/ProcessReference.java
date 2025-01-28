package Discord.OS.process;

/**
 * <p>Abstract ProcessReference class.</p>
 *
 * @author xXTheSebXx
 * @version 1.0-SNAPSHOT
 */
public abstract class ProcessReference {
    /**
     * process name
     */
    protected String name;
    /**
     * process id
     */
    protected int pid;

    /**
     * <p>Constructor for ProcessReference.</p>
     *
     * @param line a {@link java.lang.String} object
     */
    public ProcessReference(String line) {
        this.configure(line);
    }

    /**
     * <p>configure.</p>
     *
     * @param line a {@link java.lang.String} object
     */
    public abstract void configure(String line);

    /**
     * <p>getPID.</p>
     *
     * @return a int
     */
    public int getPID() {
        return pid;
    }

    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "ProcessReference{" +
                "name='" + name + '\'' +
                ", pid=" + pid +
                '}';
    }
}
