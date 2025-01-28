package Discord.OS.utility;


import Discord.OS.process.ProcessReference;
import Discord.OS.process.observer.ProcessCallback;
import Discord.OS.process.observer.ProcessObserver;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * <p>SystemUtility interface.</p>
 *
 * @author xXTheSebXx
 * @version 1.0-SNAPSHOT
 */
public interface SystemUtility {
    /** Constant <code>service</code> */
    ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    /**
     * <p>listen.</p>
     *
     * @param name a {@link java.lang.String} object
     * @param callback a {@link Discord.OS.process.observer.ProcessCallback} object
     * @return a {@link java.util.concurrent.ScheduledFuture} object
     */
    static ScheduledFuture<?> listen(String name, ProcessCallback callback) {
        return service.scheduleAtFixedRate(new ProcessObserver(name, callback), 0, 1000L, TimeUnit.MILLISECONDS);
    }

    /**
     * <p>getProcessByPID.</p>
     *
     * @param pid a int
     * @return a {@link java.util.Optional} object
     * @throws java.io.IOException if any.
     */
    Optional<ProcessReference> getProcessByPID(int pid) throws IOException;

    /**
     * <p>getProcessList.</p>
     *
     * @return a {@link java.util.List} object
     * @throws java.io.IOException if any.
     */
    List<ProcessReference> getProcessList() throws IOException;

    /**
     * <p>getProcessByName.</p>
     *
     * @param name a {@link java.lang.String} object
     * @return an array of {@link java.lang.Integer} objects
     * @throws java.io.IOException if any.
     */
    int[] getProcessByName(String name) throws IOException;

    /**
     * <p>isProcessRunning.</p>
     *
     * @param name a {@link java.lang.String} object
     * @return a boolean
     * @throws java.io.IOException if any.
     */
    boolean isProcessRunning(String name) throws IOException;

    /**
     * <p>translate.</p>
     *
     * @param path a {@link java.lang.String} object
     * @return a {@link java.lang.String} object
     */
    String translate(String path);
}
