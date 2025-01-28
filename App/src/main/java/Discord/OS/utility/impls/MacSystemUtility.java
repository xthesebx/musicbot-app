package Discord.OS.utility.impls;

import Discord.OS.process.ProcessReference;
import Discord.OS.process.impl.UnixProcess;
import Discord.OS.utility.BasicSystemUtility;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created: 29/09/2023 15:20
 * Author: Twitter @hawolt
 *
 * @author xXTheSebXx
 * @version 1.0-SNAPSHOT
 */
public class MacSystemUtility extends BasicSystemUtility {
    /** {@inheritDoc} */
    @Override
    public String translate(String path) {
        return path;
    }

    /** {@inheritDoc} */
    @Override
    public List<ProcessReference> getProcessList() throws IOException {
        ProcessBuilder builder = new ProcessBuilder("ps", "aux");
        builder.redirectErrorStream(true);
        Process process = builder.start();
        List<ProcessReference> references = new ArrayList<>();
        try (InputStream stream = process.getInputStream()) {
            String[] list = readStream(stream).split("\n");
            for (int i = 1; i < list.length; i++) {
                String line = list[i];
                line = line.trim().replaceAll(" +", " ");
                if (line.isEmpty()) continue;
                references.add(new UnixProcess(line));
            }
        }
        return references;
    }
}
