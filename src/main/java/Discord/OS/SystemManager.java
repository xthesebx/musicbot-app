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

    public static SystemManager getInstance() {
        return INSTANCE;
    }

    @Override
    public Optional<ProcessReference> getProcessByPID(int pid) throws IOException {
        return get().getProcessByPID(pid);
    }

    @Override
    public List<ProcessReference> getProcessList() throws IOException {
        return get().getProcessList();
    }

    @Override
    public int[] getProcessByName(String name) throws IOException {
        return get().getProcessByName(name);
    }

    @Override
    public boolean isProcessRunning(String name) throws IOException {
        return get().isProcessRunning(name);
    }

    @Override
    public String translate(String path) {
        return get().translate(path);
    }
}
