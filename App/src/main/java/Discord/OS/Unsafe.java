package Discord.OS;

/**
 * <p>Unsafe class.</p>
 *
 * @author xXTheSebXx
 * @version 1.0-SNAPSHOT
 */
public class Unsafe {
    /**
     * <p>cast.</p>
     *
     * @param o a {@link java.lang.Object} object
     * @param <T> a T class
     * @return a T object
     */
    @SuppressWarnings(value = "all")
    public static <T> T cast(Object o) {
        return (T) o;
    }
}
