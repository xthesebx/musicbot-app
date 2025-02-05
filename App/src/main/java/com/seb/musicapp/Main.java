package com.seb.musicapp;

import Discord.DiscordActivity;
import com.hawolt.logger.Logger;
import com.seb.io.Reader;
import com.seb.io.Writer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>Main class.</p>
 *
 * @author xXTheSebXx
 * @version 1.0-SNAPSHOT
 */
public class Main extends Application {
    String cssDark = Main.class.getResource("dark.css").toExternalForm();

    /** Constant <code>DEBUG=false</code> */
    public static boolean DEBUG = false;
    /**
     * Connector object for connection to
     */
    public Connector connector;
    public Theme theme;
    Scene queueScene;
    public Stage stage;
    /**
     * Scene for connector and main window
     */
    public Scene scene, streamerscene;
    /**
     * Stage for the queue window
     */
    public Stage queueStage, streamerStage;
    /**
     * Controller for the queue window
     */
    public QueueController queueController;
    /**
     * controller for the mainwindow
     */
    public MainWindowController mainWindowController;
    private ConnectController connectController;
    private StreamerController streamerController;
    /**
     * the discord activity
     */
    public DiscordActivity discordActivity;

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        initialise();
        DiscordActivity.create().ifPresent(this::setDiscordActivity);
    }

    private void setDiscordActivity(DiscordActivity discordActivity) {
        this.discordActivity = discordActivity;
    }

    /**
     * <p>initialise.</p>
     *
     * @throws java.io.IOException if any.
     */
    public void initialise() throws IOException {
        this.connector = new Connector(this);
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Connect.fxml"));
        scene = new Scene(fxmlLoader.load(), 400, 200);
        connectController = fxmlLoader.getController();
        fxmlLoader = new FXMLLoader(Main.class.getResource("Queue.fxml"));
        queueScene = new Scene(fxmlLoader.load(), 320, 800);
        stage.setScene(queueScene);
        queueController = fxmlLoader.getController();
        fxmlLoader = new FXMLLoader(Main.class.getResource("Streamer.fxml"));
        streamerscene = new Scene(fxmlLoader.load(), 320, 800);
        streamerController = fxmlLoader.getController();
        String theme = Reader.read(new File("theme"));
        setTheme(theme);
        stage.setTitle("Connect");
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(_ -> System.exit(0));
        connectController.setApplication(this);
    }

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects
     */
    public static void main(String[] args) throws IOException {
        URL path = Main.class.getResource("Main.class");
        if (!(path != null && path.toString().startsWith("file"))) {
            patchPatcher();
        }
        launch();
    }

    /**
     * <p>connect.</p>
     *
     * @param id a {@link java.lang.String} object
     */
    public void connect(String id) {
        try {
            connector.connect(id);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("MainWindow.fxml"));
        try {
            scene.setRoot(fxmlLoader.load());
            mainWindowController = fxmlLoader.getController();
            mainWindowController.setApplication(this);
            stage.setTitle("Music Bot App");
            stage.setScene(scene);
            mainWindowController.init();
            queueStage = new Stage();
            queueStage.setTitle("Queue");
            queueStage.setScene(queueScene);
            queueStage.setOnCloseRequest(event -> {
                event.consume();
                queueStage.hide();
            });
            queueController.setApp(this);
            queueController.addPropertyChangeListener(mainWindowController);
            new Thread(() -> queueController.setItems()).start();
            streamerStage = new Stage();
            streamerStage.setTitle("Streamer Stuff");
            streamerStage.setScene(streamerscene);
            streamerStage.setOnCloseRequest(event -> {
                event.consume();
                streamerStage.hide();
            });
            streamerController.initialize(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>reset.</p>
     */
    public void reset() {
        mainWindowController.getProvider().reset();
        Platform.runLater(() -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Connect.fxml"));
                scene.setRoot(fxmlLoader.load());
                stage.setTitle("Connect");
                stage.setScene(scene);
                stage.show();
                connectController = fxmlLoader.getController();
                connectController.setApplication(this);
                queueStage.close();
            } catch (IOException e) {

            }
        });
    }

    private static void patchPatcher() throws IOException {

        String version = Reader.read(new File("version.txt"));
        float f;
        try {
            f = Float.parseFloat(version);
        } catch (NullPointerException e) {
            f = 0.0f;
        }
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
        float versionNew = Float.parseFloat(obj.getString("name"));
        con.disconnect();
        if (f < versionNew) {
            JSONArray assets = obj.getJSONArray("assets");
            AtomicReference<String> downloadlink = new AtomicReference<>("");
            assets.forEach(asset -> {
                if (((JSONObject) asset).getString("name").equals("musicapp.exe"))
                    downloadlink.set(((JSONObject) asset).getString("browser_download_url"));
            });
            try (BufferedInputStream bis = new BufferedInputStream(new URL(downloadlink.get()).openStream());
                 FileOutputStream fos = new FileOutputStream("musicapp.exe")) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = bis.read(buffer, 0, 1024)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                Logger.error(e);
            }
            Writer.write(String.valueOf(versionNew), new File("version.txt"));
        }
    }

    public void setTheme(String theme) {
        if (theme == null || theme.isEmpty() || theme.equals("light")) {
            theme = "light";
            scene.getStylesheets().remove(cssDark);
            queueScene.getStylesheets().remove(cssDark);
            streamerscene.getStylesheets().remove(cssDark);
            this.theme = Theme.Light;
        }
        else if (theme.equals("dark")) {
            scene.getStylesheets().add(cssDark);
            queueScene.getStylesheets().add(cssDark);
            streamerscene.getStylesheets().add(cssDark);
            this.theme = Theme.Dark;
        }
        Writer.write(theme, new File("theme"));
    }
}
