package Discord.OS.utility;


import Discord.OS.process.ProcessReference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;

/**
 * <p>Abstract BasicSystemUtility class.</p>
 *
 * @author xXTheSebXx
 * @version 1.0-SNAPSHOT
 */
public abstract class BasicSystemUtility implements SystemUtility {
    /**
     * <p>readStream.</p>
     *
     * @param stream a {@link java.io.InputStream} object
     * @return a {@link java.lang.String} object
     * @throws java.io.IOException if any.
     */
    protected static String readStream(InputStream stream) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        return output.toString();
    }

    /** {@inheritDoc} */
    @Override
    public Optional<ProcessReference> getProcessByPID(int pid) throws IOException {
        return getProcessList()
                .stream()
                .filter(process -> process.getPID() == pid)
                .findAny();
    }

    /** {@inheritDoc} */
    @Override
    public int[] getProcessByName(String name) throws IOException {
        return getProcessList()
                .stream()
                .filter(process -> process.getName().equals(name))
                .mapToInt(ProcessReference::getPID)
                .toArray();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isProcessRunning(String name) throws IOException {
        return getProcessByName(name).length > 0;
    }
}
