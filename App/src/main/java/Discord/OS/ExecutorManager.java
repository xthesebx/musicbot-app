package Discord.OS;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created: 16/08/2023 20:06
 * Author: Twitter @hawolt
 *
 * @author xXTheSebXx
 * @version 1.0-SNAPSHOT
 */
public class ExecutorManager {
    private final static Map<String, ExecutorService> map = new HashMap<>();

    /**
     * <p>getService.</p>
     *
     * @param name a {@link java.lang.String} object
     * @param <T> a T class
     * @return a T object
     */
    public static <T> T getService(String name) {
        if (!map.containsKey(name)) map.put(name, Executors.newSingleThreadExecutor());
        return Unsafe.cast(map.get(name));
    }

    /**
     * <p>getScheduledService.</p>
     *
     * @param name a {@link java.lang.String} object
     * @param <T> a T class
     * @return a T object
     */
    public static <T> T getScheduledService(String name) {
        if (!map.containsKey(name)) map.put(name, Executors.newSingleThreadScheduledExecutor());
        return Unsafe.cast(map.get(name));
    }

    /**
     * <p>registerService.</p>
     *
     * @param name a {@link java.lang.String} object
     * @param service a {@link java.util.concurrent.ExecutorService} object
     * @param <T> a T class
     * @return a T object
     */
    public static <T> T registerService(String name, ExecutorService service) {
        map.put(name, service);
        return Unsafe.cast(service);
    }

    /**
     * <p>get.</p>
     *
     * @return a {@link java.util.Collection} object
     */
    public static Collection<ExecutorService> get() {
        return map.values();
    }
}
