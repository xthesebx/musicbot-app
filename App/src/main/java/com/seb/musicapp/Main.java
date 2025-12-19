package com.seb.musicapp;

import Discord.DiscordActivity;
import com.hawolt.logger.Logger;
import com.seb.io.Reader;
import com.seb.io.Writer;
import com.seb.musicapp.common.Song;
import com.seb.musicapp.common.Theme;
import com.seb.musicapp.common.WrongCodeException;
import com.seb.musicapp.connect.ConnectController;
import com.seb.musicapp.connect.Connector;
import com.seb.musicapp.window.ExitController;
import com.seb.musicapp.window.MainWindowController;
import com.seb.musicapp.window.QueueController;
import com.seb.musicapp.window.StreamerController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Optional;
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
     *
     */
    public Connector connector;
    public Theme theme;
    public Scene queueScene;
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
    public Stage exitStage;
    public ExitController exitController;
    private Scene exitScene;
    public Scene mainScene;
    public static Main application;
    /**
     * the discord activity
     */
    public Optional<DiscordActivity> discordActivity;

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) throws IOException {
        application = this;
        this.stage = stage;
        initialise();
        setDiscordActivity();
    }

    private void setDiscordActivity() {
        this.discordActivity = DiscordActivity.create();
    }

    /**
     * <p>initialise.</p>
     *
     * @throws java.io.IOException if any.
     */
    public void initialise() throws IOException {
        this.connector = new Connector(this);
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Connect.fxml"));
        scene = new Scene(fxmlLoader.load(), 400, 300);
        connectController = fxmlLoader.getController();
        fxmlLoader = new FXMLLoader(Main.class.getResource("Queue.fxml"));
        queueScene = new Scene(fxmlLoader.load(), 320, 800);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(queueScene);
        stage.setTitle("Connect");
        InputStream iconStream = Main.class.getResourceAsStream("icon.jpg");
        if (iconStream != null) {
            Image icon = new Image(iconStream);
            stage.getIcons().add(icon);
            queueStage.getIcons().add(icon);
            streamerStage.getIcons().add(icon);
            exitStage.getIcons().add(icon);
        }
        queueController = fxmlLoader.getController();
        fxmlLoader = new FXMLLoader(Main.class.getResource("Streamer.fxml"));
        streamerscene = new Scene(fxmlLoader.load(), 320, 180);
        streamerController = fxmlLoader.getController();
        exitStage = new Stage();
        fxmlLoader = new FXMLLoader(Main.class.getResource("Exit.fxml"));
        exitScene = new Scene(fxmlLoader.load(), 320, 180);
        exitStage.setScene(exitScene);
        exitStage.initStyle(StageStyle.UNDECORATED);
        exitStage.setTitle("Exit");
        exitController = fxmlLoader.getController();
        exitController.init(this);
        fxmlLoader = new FXMLLoader(Main.class.getResource("MainWindow.fxml"));
        mainScene = new Scene(fxmlLoader.load(),1000, 380);
        mainWindowController = fxmlLoader.getController();
        mainWindowController.setApplication(this);
        mainWindowController.init();
        queueStage = new Stage();
        queueScene.addEventFilter(MouseEvent.ANY, new ResizeHandler(queueStage));
        queueStage.initStyle(StageStyle.UNDECORATED);
        queueStage.setScene(queueScene);
        queueStage.setOnCloseRequest(event -> {
            event.consume();
            queueStage.hide();
        });
        queueStage.setTitle("Queue");
        queueController.setApp(this);
        queueController.addPropertyChangeListener(mainWindowController);
        new Thread(() -> queueController.setItems()).start();
        streamerStage = new Stage();
        streamerStage.initStyle(StageStyle.UNDECORATED);
        streamerStage.setScene(streamerscene);
        streamerscene.addEventFilter(MouseEvent.ANY, new ResizeHandler(streamerStage));
        streamerStage.setOnCloseRequest(event -> {
            event.consume();
            streamerStage.hide();
        });
        streamerController.initialize(this);
        streamerStage.setTitle("Streamer Stuff");
        String theme = Reader.read(new File("theme"));
        setTheme(theme);
        connectController.setApplication(this);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(_ -> System.exit(0));
        File code = new File("code.txt");
        if (code.exists() && !Reader.read(code).isEmpty()) {
            try {
                connect(Reader.read(code));
            } catch (WrongCodeException e) {

            }
        }
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
    public void connect(String id) throws IOException {
        connector.connect(id);
        mainWindowController.setHotkeys();
        stage.setTitle("Music Bot App");
        stage.setWidth(mainScene.getWidth());
        stage.setHeight(mainScene.getHeight());
        stage.setScene(mainScene);
    }

    /**
     * <p>reset.</p>
     */
    public void reset() {
        mainWindowController.reset();
        //discordActivity.setIdlePresence();
        Platform.runLater(() -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Connect.fxml"));
                scene.setRoot(fxmlLoader.load());
                stage.setTitle("Connect");
                stage.setScene(scene);
                stage.setWidth(400);
                stage.setHeight(300);
                stage.show();
                connectController = fxmlLoader.getController();
                connectController.setApplication(this);
                queueStage.close();
                streamerStage.close();
                queueController.clearQueue();
            } catch (IOException e) {
                Logger.error(e);
            }
        });
    }

    private static void patchPatcher() throws IOException {

        String version = Reader.read(new File("version.txt"));
        URL url = URI.create("https://api.github.com/repos/xthesebx/musicbot-app/releases/latest").toURL();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        JSONObject obj = new JSONObject(content.toString());
        String versionNew = obj.getString("name");
        con.disconnect();
        if (!version.equals(versionNew)) {
            JSONArray assets = obj.getJSONArray("assets");
            AtomicReference<String> downloadlink = new AtomicReference<>("");
            assets.forEach(asset -> {
                if (((JSONObject) asset).getString("name").equals("musicapp.exe"))
                    downloadlink.set(((JSONObject) asset).getString("browser_download_url"));
            });
            try (BufferedInputStream bis = new BufferedInputStream(URI.create(downloadlink.get()).toURL().openStream());
                 FileOutputStream fos = new FileOutputStream("musicapp.exe")) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = bis.read(buffer, 0, 1024)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                Logger.error(e);
            }
            Writer.write(versionNew, new File("version.txt"));
        }
    }

    public void setTheme(String theme) {
        if (theme == null || theme.isEmpty() || theme.equals("light")) {
            theme = "light";
            scene.getStylesheets().remove(cssDark);
            queueScene.getStylesheets().remove(cssDark);
            streamerscene.getStylesheets().remove(cssDark);
            exitScene.getStylesheets().remove(cssDark);
            mainScene.getStylesheets().remove(cssDark);
            for (TableView<Song> tableView : queueController.tableViews) {
                tableView.getStylesheets().remove(cssDark);
            }
            this.theme = Theme.Light;
        }
        else if (theme.equals("dark")) {
            scene.getStylesheets().add(cssDark);
            queueScene.getStylesheets().add(cssDark);
            streamerscene.getStylesheets().add(cssDark);
            exitScene.getStylesheets().add(cssDark);
            mainScene.getStylesheets().add(cssDark);
            for (TableView<Song> tableView : queueController.tableViews) {
                tableView.getStylesheets().add(cssDark);
            }
            this.theme = Theme.Dark;
        }
        Writer.write(theme, new File("theme"));
    }

    /**
     * Handler to process the resizing of the the given stage.
     */
    static class ResizeHandler implements EventHandler<MouseEvent> {

        /**
         * Space to consider around the stage border for resizing
         */
        private static final double BORDER = 6;

        /**
         * Space to consider the border width factor while resizing
         */
        private static final double BORDER_WIDTH_FACTOR = 6;

        private final Window window;
        /**
         * Current cursor reference to the scene
         */
        private Cursor cursor = Cursor.DEFAULT;

        /**
         * X position of the drag start
         */
        private double startX = 0;

        /**
         * Y position of the drag start
         */
        private double startY = 0;

        ResizeHandler(final Window window) {
            this.window = window;
        }

        @Override
        public void handle(final MouseEvent event) {
            final EventType<? extends MouseEvent> eventType = event.getEventType();
            final Scene scene = window.getScene();
            final double mouseEventX = event.getSceneX();
            final double mouseEventY = event.getSceneY();
            final double sceneWidth = scene.getWidth();
            final double sceneHeight = scene.getHeight();

            if (MouseEvent.MOUSE_MOVED.equals(eventType)) {
                assignCursor(scene, event, mouseEventX, mouseEventY, sceneWidth, sceneHeight);

            } else if (MouseEvent.MOUSE_PRESSED.equals(eventType)) {
                startX = window.getWidth() - mouseEventX;
                startY = window.getHeight() - mouseEventY;
                consumeEventIfNotDefaultCursor(event);

            } else if (MouseEvent.MOUSE_DRAGGED.equals(eventType) && !Cursor.DEFAULT.equals(cursor)) {
                consumeEventIfNotDefaultCursor(event);
                if (!Cursor.W_RESIZE.equals(cursor) && !Cursor.E_RESIZE.equals(cursor)) {
                    handleHeightResize(event);
                }

                if (!Cursor.N_RESIZE.equals(cursor) && !Cursor.S_RESIZE.equals(cursor)) {
                    handleWidthResize(event);
                }
            }
        }

        private void assignCursor(final Scene scene, final MouseEvent event, final double mouseEventX,
                                  final double mouseEventY, final double sceneWidth, final double sceneHeight) {
            final Cursor cursor1;

            if (mouseEventX < BORDER && mouseEventY < BORDER) {
                cursor1 = Cursor.NW_RESIZE;
            } else if (mouseEventX < BORDER && mouseEventY > sceneHeight - BORDER) {
                cursor1 = Cursor.SW_RESIZE;
            } else if (mouseEventX > sceneWidth - BORDER
                    && mouseEventY < BORDER) {
                cursor1 = Cursor.NE_RESIZE;
            } else if (mouseEventX > sceneWidth - BORDER && mouseEventY > sceneHeight - BORDER) {
                cursor1 = Cursor.SE_RESIZE;
            } else if (mouseEventX < BORDER) {
                cursor1 = Cursor.W_RESIZE;
            } else if (mouseEventX > sceneWidth - BORDER) {
                cursor1 = Cursor.E_RESIZE;
            } else if (mouseEventY < BORDER) {
                cursor1 = Cursor.N_RESIZE;
            } else if (mouseEventY > sceneHeight - BORDER) {
                cursor1 = Cursor.S_RESIZE;
            } else {
                cursor1 = Cursor.DEFAULT;
            }
            cursor = cursor1;
            scene.setCursor(cursor);
        }

/**
 * Consumes the mouse event if the cursor is not the DEFAULT cursor.
 *
 * @param event MouseEvent instance
 */
private void consumeEventIfNotDefaultCursor(final MouseEvent event) {
    if (!cursor.equals(Cursor.DEFAULT)) {
        event.consume();
    }
}

        /**
         * Processes the vertical drag movement and resizes the window height.
         *
         * @param event MouseEvent instance
         */
        private void handleHeightResize(final MouseEvent event) {
            final double mouseEventY = event.getSceneY();
            final double minHeight = 30;
            if (Cursor.NW_RESIZE.equals(cursor)
                    || Cursor.N_RESIZE.equals(cursor)
                    || Cursor.NE_RESIZE.equals(cursor)) {
                if (window.getHeight() > minHeight || mouseEventY < 0) {
                    final double newHeight = window.getY() - event.getScreenY() + window.getHeight();
                    window.setHeight(max(newHeight, minHeight));
                    window.setY(event.getScreenY());
                }
            } else if (window.getHeight() > minHeight || mouseEventY + startY - window.getHeight() > 0) {
                final double newHeight = mouseEventY + startY;
                window.setHeight(max(newHeight, minHeight));
            }
        }

        /**
         * Processes the horizontal drag movement and resizes the window width.
         *
         * @param event MouseEvent instance
         */
        private void handleWidthResize(final MouseEvent event) {
            final double mouseEventX = event.getSceneX();
            final double minWidth = 100;
            if (Cursor.NW_RESIZE.equals(cursor)
                    || Cursor.W_RESIZE.equals(cursor)
                    || Cursor.SW_RESIZE.equals(cursor)) {
                if (window.getWidth() > minWidth || mouseEventX < 0) {
                    final double newWidth = window.getX() - event.getScreenX() + window.getWidth();
                    window.setWidth(max(newWidth, minWidth));
                    window.setX(event.getScreenX());
                }
            } else if (window.getWidth() > minWidth || mouseEventX + startX - window.getWidth() > 0) {
                final double newWidth = mouseEventX + startX;
                window.setWidth(max(newWidth, minWidth));
            }
        }

        /**
         * Determines the max value among the provided two values.
         *
         * @param value1 First value
         * @param value2 Second value
         * @return Maximum value of the given two values.
         */
        private double max(final double value1, final double value2) {
            return Math.max(value1, value2);
        }
    }


    public static void dragHandler(HBox outerBox) {
        new Thread(() -> {
            while (outerBox == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            DoubleProperty x = new SimpleDoubleProperty();
            DoubleProperty y = new SimpleDoubleProperty();
            outerBox.setOnMousePressed(e -> {
                x.set(e.getSceneX());
                y.set(e.getSceneY());
            });
            outerBox.setOnMouseDragged(e -> {
                outerBox.getScene().getWindow().setX(e.getScreenX() - x.get());
                outerBox.getScene().getWindow().setY(e.getScreenY() - y.get());
            });
        }).start();
    }
}
