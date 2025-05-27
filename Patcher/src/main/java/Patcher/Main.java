package Patcher;

import com.hawolt.logger.Logger;
import com.seb.io.Reader;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

public class Main {

    public static void main(String[] args) throws IOException {
        String version = Reader.read(new File("version.txt"));
        URL url = new URL("https://api.github.com/repos/xthesebx/musicbot-app/releases/latest");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        JSONObject obj = new JSONObject(content.toString());
        String versionNew = obj.getString("name");
        con.disconnect();
        if (version.equals(versionNew)) {
            JSONArray assets = obj.getJSONArray("assets");
            AtomicReference<String> downloadlink = new AtomicReference<>("");
            assets.forEach(asset -> {
                if (((JSONObject) asset).getString("name").equals("MusicApp.jar"))
                    downloadlink.set(((JSONObject) asset).getString("browser_download_url"));
            });
            try (BufferedInputStream bis = new BufferedInputStream(new URL(downloadlink.get()).openStream());
                 FileOutputStream fos = new FileOutputStream("MusicApp.jar")) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = bis.read(buffer, 0, 1024)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                Logger.error(e);
            }
        }
        Process p = new ProcessBuilder("jdk-23/bin/java", "-jar", "MusicApp.jar").start();
    }

}
