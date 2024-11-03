package Discord.OS;

import Discord.OS.process.ProcessReference;
import Discord.OS.utility.SystemUtility;
import Discord.OS.utility.impls.LinuxSystemUtility;
import Discord.OS.utility.impls.MacSystemUtility;
import Discord.OS.utility.impls.WindowsSystemUtility;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * <p>SystemManager class.</p>
 *
 * @author xXTheSebXx
 * @version 1.0-SNAPSHOT
 */
public class SystemManager implements SystemUtility {

    private static final Map<OperatingSystem.OSType, SystemUtility> managers = new HashMap<>();
    private static final SystemManager INSTANCE = new SystemManager();

    static {
        managers.put(OperatingSystem.OSType.MAC, new MacSystemUtility());
        managers.put(OperatingSystem.OSType.LINUX, new LinuxSystemUtility());
        managers.put(OperatingSystem.OSType.WINDOWS, new WindowsSystemUtility());
    }

    private static SystemUtility get() {
        if (!managers.containsKey(OperatingSystem.getOperatingSystemType())) {
            throw new IllegalArgumentException("OS not supported");
        }
        return managers.get(OperatingSystem.getOperatingSystemType());
    }

    /**
     * <p>getInstance.</p>
     *
     * @return a {@link Discord.OS.SystemManager} object
     */
    public static SystemManager getInstance() {
        return INSTANCE;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<ProcessReference> getProcessByPID(int pid) throws IOException {
        return get().getProcessByPID(pid);
    }

    /** {@inheritDoc} */
    @Override
    public List<ProcessReference> getProcessList() throws IOException {
        return get().getProcessList();
    }

    /** {@inheritDoc} */
    @Override
    public int[] getProcessByName(String name) throws IOException {
        return get().getProcessByName(name);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isProcessRunning(String name) throws IOException {
        return get().isProcessRunning(name);
    }

    /** {@inheritDoc} */
    @Override
    public String translate(String path) {
        return get().translate(path);
    }
}
