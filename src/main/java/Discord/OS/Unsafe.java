package Discord.OS;

public class Unsafe {
    @SuppressWarnings(value = "all")
    public static <T> T cast(Object o) {
        return (T) o;
    }
}