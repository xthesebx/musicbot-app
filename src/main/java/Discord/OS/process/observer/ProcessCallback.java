package Discord.OS.process.observer;

/**
 * <p>ProcessCallback interface.</p>
 *
 * @author xXTheSebXx
 * @version 1.0-SNAPSHOT
 */
public interface ProcessCallback {
    /**
     * <p>onStateChange.</p>
     *
     * @param state a {@link Discord.OS.process.observer.ProcessCallback.State} object
     */
    void onStateChange(State state);

    /**
     * enum for the process state
     */
    enum State {
        /**
         * unknown state
         */
        UNKNOWN,
        /**
         * process started
         */
        STARTED,
        /**
         * process running
         */
        RUNNING,
        /**
         * process terminated
         */
        TERMINATED,
        /**
         * process not running
         */
        NOT_RUNNING
    }
}
