package com.seb.musicapp.window;

import com.hawolt.logger.Logger;
import com.seb.io.Reader;
import com.seb.io.Writer;
import com.seb.musicapp.Main;
import com.seb.musicapp.common.RepeatState;
import com.seb.musicapp.common.Server;
import com.seb.musicapp.common.Song;
import com.seb.musicapp.common.Theme;
import com.seb.musicapp.connect.Connector;
import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.MediaKey;
import com.tulskiy.keymaster.common.Provider;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.*;
import java.util.List;


/**
 * <p>MainWindowController class.</p>
 *
 * @author xXTheSebXx
 * @version 1.0-SNAPSHOT
 */
public class MainWindowController implements HotKeyListener, PropertyChangeListener {

    public Label getTitle() {
        return title;
    }

    @FXML
    private ImageView imageView;
    @FXML
    private Label title;
    @FXML
    private GridPane gridPane;
    @FXML
    private Label PlayPause;
    @FXML
    private Label Skip;
    @FXML
    private Button changePlay;
    @FXML
    private Button changeSkip;
    @FXML
    private Button mediaPlay;
    @FXML
    private Button mediaSkip;
    @FXML
    private Button join;
    @FXML
    private Button leave;
    @FXML
    private Button shuffle;
    @FXML
    private Button play;
    @FXML
    private Button queue;
    @FXML
    private Button stop;
    @FXML
    private Button streamerStuff;
    @FXML
    private TextField url;
    @FXML
    private Button modeChange;
    @FXML
    private Button repeat;
    @FXML
    private Slider volume;
    @FXML
    private Label prev;
    @FXML
    private Button changePrev;
    @FXML
    private Button mediaPrev;
    @FXML
    private HBox outerBox;
    @FXML
    public TableView<Song> queueTable;
    @FXML
    public TableColumn<Song, String> queueTitle;
    @FXML
    public TableColumn<Song, String> queueArtist;
    @FXML
    public TableColumn<Song, String> queueLength;
    @FXML
    private HBox tableHBox;
    @FXML
    Label volLabel;
    @FXML
    Button pausebtn;
    @FXML
    Button prevbtn;
    @FXML
    Button skipbtn;
    @FXML
    ComboBox<Server> serverBox;
    double queueWidth;
    private Main application;
    private final Provider provider = Provider.getCurrentProvider(false);
    private final HashMap<String, String> buttons = new HashMap<>();
    private static final List<Integer> MODIFIERS = Arrays.asList(KeyEvent.VK_ALT, KeyEvent.VK_CONTROL, KeyEvent.VK_SHIFT, KeyEvent.VK_META);
    private JSONObject file;

    /**
     * <p>Constructor for MainWindowController.</p>
     */
    public MainWindowController() {
    }

    /**
     * <p>init.</p>
     */
    public void init() {
        url.setOnKeyPressed(e -> {
            if (e.getCode().getCode() == 10) {
                onPlay();
            }
        });

        if (application.theme == Theme.Dark) Platform.runLater(() -> modeChange.setText("Light Mode"));
        else if (application.theme == Theme.Light) Platform.runLater(() -> modeChange.setText("Dark Mode"));
        Main.dragHandler(outerBox);
        Connector.code.keySet().forEach((key) -> {
            serverBox.getItems().add(new Server(key, Connector.code.getString(key)));
        });

        serverBox.setValue(serverBox.getItems().stream().filter(server -> server.getServerName().equals(Reader.read(new File("code.txt")))).findFirst().orElse(null));
        serverBox.getItems().add(new Server("+", null));
    }

    public void setHotkeys() {
        String play = "", skip = "", prevString = "";
        file = new JSONObject();
        try {
            file = new JSONObject(Files.readString(Path.of("hotkeys")));
            play = file.getString("play");
            if (play.isEmpty()) play = "MEDIA_PLAY_PAUSE";
            skip = file.getString("skip");
            if (skip.isEmpty()) skip = "MEDIA_NEXT_TRACK";
            prevString = file.getString("prev");
            if (prevString.isEmpty()) prevString = "MEDIA_PREV_TRACK";
        } catch (IOException | JSONException e) {
            if (!(e instanceof NoSuchFileException))
                Logger.error(e);
        }
        if (!play.equals("MEDIA_PLAY_PAUSE") && !play.isEmpty()){
            provider.register(KeyStroke.getKeyStroke(play), this);
            buttons.put("play", play);
        }
        else if (!play.isEmpty()) {
            provider.register(MediaKey.MEDIA_PLAY_PAUSE, this);
            buttons.put("play", play);
        }
        PlayPause.setText(play);

        if (!skip.equals("MEDIA_NEXT_TRACK") && !skip.isEmpty()) {
            provider.register(KeyStroke.getKeyStroke(skip), this);
            buttons.put("skip", skip);
        }
        else if (!skip.isEmpty()) {
            provider.register(MediaKey.MEDIA_NEXT_TRACK, this);
            buttons.put("skip", skip);
        }
        Skip.setText(skip);

        if (!prevString.equals("MEDIA_PREV_TRACK") && !prevString.isEmpty()) {
            provider.register(KeyStroke.getKeyStroke(prevString), this);
            buttons.put("prev", prevString);
        }
        else if (!prevString.isEmpty()) {
            provider.register(MediaKey.MEDIA_PREV_TRACK, this);
            buttons.put("prev", prevString);
        }
        prev.setText(prevString);
    }

    /**
     * <p>Setter for the field <code>application</code>.</p>
     *
     * @param application a {@link Main} object
     */
    public void setApplication(Main application) {
        this.application = application;
    }

    /**
     * <p>Getter for the field <code>provider</code>.</p>
     *
     * @return a {@link com.tulskiy.keymaster.common.Provider} object
     */
    public Provider getProvider() {
        return provider;
    }

    /**
     * <p>onChangePlay.</p>
     */
    @FXML
    protected void onChangePlay() {
        changePlay.setDisable(true);
        changePrev.setDisable(false);
        changeSkip.setDisable(false);
        application.mainScene.setOnKeyPressed(e -> {
            if (!MODIFIERS.contains(e.getCode().getCode())) {
                KeyStroke keyStroke = KeyStroke.getKeyStroke(e.getCode().getCode(), 0);
                PlayPause.setText(keyStroke.toString().replaceAll("pressed ", ""));
                file.put("play", PlayPause.getText());
                try {
                    PrintWriter hotkeyWriter = new PrintWriter("hotkeys");
                    hotkeyWriter.println(file);
                    hotkeyWriter.close();
                    boolean media = false;
                    for (MediaKey mk : MediaKey.values()) {
                        if (mk.name().equals(buttons.get("play"))) {
                            media = true;
                        }
                    }
                    if (media) provider.unregister(MediaKey.valueOf(buttons.get("play")));
                    else provider.unregister(KeyStroke.getKeyStroke(buttons.get("play")));
                    buttons.put("play", keyStroke.toString().replaceAll("pressed ", ""));
                    provider.register(keyStroke, this);
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
                application.mainScene.setOnKeyPressed(null);
                changePlay.setDisable(false);
            }
        });
    }
    /**
     * <p>onChangeSkip.</p>
     */
    @FXML
    protected void onChangeSkip() {
        changePlay.setDisable(false);
        changePrev.setDisable(false);
        changeSkip.setDisable(true);
        application.mainScene.setOnKeyPressed(e -> {
            if (!MODIFIERS.contains(e.getCode().getCode())) {
                KeyStroke keyStroke = KeyStroke.getKeyStroke(e.getCode().getCode(), 0);
                Skip.setText(keyStroke.toString().replaceAll("pressed ", ""));
                file.put("skip", Skip.getText());
                try {
                    PrintWriter hotkeyWriter = new PrintWriter("hotkeys");
                    hotkeyWriter.println(file);
                    hotkeyWriter.close();
                    boolean media = false;
                    for (MediaKey mk : MediaKey.values()) {
                        if (mk.name().equals(buttons.get("skip"))) {
                            media = true;
                        }
                    }
                    if (media) provider.unregister(MediaKey.valueOf(buttons.get("skip")));
                    else provider.unregister(KeyStroke.getKeyStroke(buttons.get("skip")));
                    buttons.put("skip", keyStroke.toString().replaceAll("pressed ", ""));
                    provider.register(keyStroke, this);
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
                application.mainScene.setOnKeyPressed(null);
                changeSkip.setDisable(false);
            }
        });
    }
    /**
     * <p>onChangePrev.</p>
     */
    @FXML
    protected void onChangePrev() {
        changePlay.setDisable(false);
        changeSkip.setDisable(false);
        changePrev.setDisable(true);
        application.mainScene.setOnKeyPressed(e -> {
            if (!MODIFIERS.contains(e.getCode().getCode())) {
                KeyStroke keyStroke = KeyStroke.getKeyStroke(e.getCode().getCode(), 0);
                prev.setText(keyStroke.toString().replaceAll("pressed ", ""));
                file.put("prev", prev.getText());
                try {
                    PrintWriter hotkeyWriter = new PrintWriter("hotkeys");
                    hotkeyWriter.println(file);
                    hotkeyWriter.close();
                    boolean media = false;
                    for (MediaKey mk : MediaKey.values()) {
                        if (mk.name().equals(buttons.get("prev"))) {
                            media = true;
                        }
                    }
                    if (media) provider.unregister(MediaKey.valueOf(buttons.get("prev")));
                    else provider.unregister(KeyStroke.getKeyStroke(buttons.get("prev")));
                    buttons.put("prev", keyStroke.toString().replaceAll("pressed ", ""));
                    provider.register(keyStroke, this);
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
                application.mainScene.setOnKeyPressed(null);
                changePrev.setDisable(false);
            }
        });
    }
    /**
     * <p>onMediaPlay.</p>
     */
    @FXML
    protected void onMediaPlay() {
        if (PlayPause.getText().equals("MEDIA_PLAY_PAUSE")) return;
        provider.unregister(KeyStroke.getKeyStroke(PlayPause.getText()));
        PlayPause.setText("MEDIA_PLAY_PAUSE");
        file.put("play", PlayPause.getText());
        try {
            PrintWriter hotkeyWriter = new PrintWriter("hotkeys");
            hotkeyWriter.println(file);
            hotkeyWriter.close();
            buttons.put("play", "MEDIA_PLAY_PAUSE");
            provider.register(MediaKey.MEDIA_PLAY_PAUSE, this);
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }
    /**
     * <p>onMediaSkip.</p>
     */
    @FXML
    protected void onMediaSkip() {
        if (Skip.getText().equals("MEDIA_NEXT_TRACK")) return;
        provider.unregister(KeyStroke.getKeyStroke(Skip.getText()));
        Skip.setText("MEDIA_NEXT_TRACK");
        file.put("skip", Skip.getText());
        try {
            PrintWriter hotkeyWriter = new PrintWriter("hotkeys");
            hotkeyWriter.println(file);
            hotkeyWriter.close();
            buttons.put("skip", "MEDIA_NEXT_TRACK");
            provider.register(MediaKey.MEDIA_NEXT_TRACK, this);
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }
    /**
     * <p>onMediaPrev.</p>
     */
    @FXML
    protected void onMediaPrev() {
        if (prev.getText().equals("MEDIA_PREV_TRACK")) return;
        provider.unregister(KeyStroke.getKeyStroke(prev.getText()));
        prev.setText("MEDIA_PREV_TRACK");
        file.put("prev", prev.getText());
        try {
            PrintWriter hotkeyWriter = new PrintWriter("hotkeys");
            hotkeyWriter.println(file);
            hotkeyWriter.close();
            buttons.put("prev", "MEDIA_PREV_TRACK");
            provider.register(MediaKey.MEDIA_PREV_TRACK, this);
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }
    /**
     * <p>onJoin.</p>
     */
    @FXML
    protected void onJoin() {
        application.connector.out.println("join");
    }
    /**
     * <p>onLeave.</p>
     */
    @FXML
    protected void onLeave() {
        application.connector.out.println("leave");
    }
    /**
     * <p>onShuffle.</p>
     */
    @FXML
    protected void onShuffle() {
        application.connector.shuffle();
    }
    /**
     * <p>onQueue.</p>
     */
    @FXML
    protected void onQueue() {
        if (queue.getText().equals("Unpin Queue")) {
            queueTable.setVisible(false);
            tableHBox.getChildren().remove(queueTable);
            queueWidth = queueTable.getWidth();
            application.stage.setWidth(application.stage.getWidth() - queueWidth);
            if (Double.isNaN(application.queueStage.getX())) {
                setWindowPostions(application.queueStage,
                        application.stage.getX() + application.stage.getWidth(),
                        application.stage.getY() + ((application.stage.getHeight() - 800) / 2));
            }
            if (application.queueStage.isShowing()) {
                application.queueStage.toFront();
                if (application.queueStage.isIconified())
                    application.queueStage.setIconified(false);
            } else application.queueStage.show();
            queue.setText("Pin Queue");
        } else {
            application.queueStage.hide();
            tableHBox.getChildren().add(queueTable);
            application.stage.setWidth(application.stage.getWidth() + queueWidth);
            queueTable.setVisible(true);
            queue.setText("Unpin Queue");
        }
    }
    /**
     * <p>onStop.</p>
     */
    @FXML
    protected void onStop() {
        application.connector.out.println("stop");
    }
    /**
     * <p>onSetStreamerMode.</p>
     */
    @FXML
    protected void onStreamerStuff() {
        if (Double.isNaN(application.streamerStage.getX())) {
            setWindowPostions(application.streamerStage,
                    application.stage.getX() - 320,
                    application.stage.getY() + ((application.stage.getHeight() - 180) / 2));
        }
        if (application.streamerStage.isShowing()) {
            application.streamerStage.toFront();
            if (application.streamerStage.isIconified())
                application.streamerStage.setIconified(false);
        }
        else application.streamerStage.show();
    }

    private void setWindowPostions(Stage stage, double x, double y) {
        //TODO: fix for multiple screens
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;

        for (GraphicsDevice screen : screens) {
            Rectangle bounds = screen.getDefaultConfiguration().getBounds();
            if (bounds.x < minX) {
                minX = bounds.x;
            }
            if (bounds.y < minY) {
                minY = bounds.y;
            }
        }
        if (x < minX) x = minX;
        else if (x > Toolkit.getDefaultToolkit().getScreenSize().getWidth()) x = Toolkit.getDefaultToolkit().getScreenSize().getWidth() - application.stage.getWidth();
        stage.setX(x);
        if (y < minY) y = minY;
        else if (y > Toolkit.getDefaultToolkit().getScreenSize().getHeight()) y = Toolkit.getDefaultToolkit().getScreenSize().getHeight() - application.stage.getHeight();
        stage.setY(y);
    }
    /**
     * <p>onPlay.</p>
     */
    @FXML
    protected void onPlay() {
        application.connector.out.println("play " + url.getText().strip());
        url.setText("");
    }
    /**
     * <p>onRepeat.</p>
     */
    @FXML
    protected void onRepeat() {
        application.connector.out.println("repeat");
        switch (repeat.getText()) {
            case "Currently not repeating" -> repeat.setText("Repeating the Queue");
            case "Repeating the Queue" -> repeat.setText("Repeating the Song");
            case "Repeating the Song" -> repeat.setText("Currently not repeating");
        }
    }
    /**
     * <p>onToggleRequests.</p>
     */
    @FXML
    protected void onChangeTheme() {
        if (application.theme == Theme.Dark) Platform.runLater(() -> {
            application.setTheme("light");
            modeChange.setText("Dark Mode");
        });
        else if (application.theme == Theme.Light) Platform.runLater(() -> {
            application.setTheme("dark");
            modeChange.setText("Light Mode");
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onHotKey(HotKey hotKey) {
        if (hotKey.keyStroke != null) {
            if (buttons.get("play").equals(hotKey.keyStroke.toString().replaceAll("pressed ", ""))) {
                application.connector.out.println("playpause");
            } else if (buttons.get("skip").equals(hotKey.keyStroke.toString().replaceAll("pressed ", ""))) {
                application.connector.out.println("nexttrack");
            } else if (buttons.get("prev").equals(hotKey.keyStroke.toString().replaceAll("pressed ", ""))) {
                application.connector.out.println("prevtrack");
            }
        } else if (hotKey.mediaKey != null) {
            if (buttons.get("play").equals(hotKey.mediaKey.toString())) {
                application.connector.out.println("playpause");
            } else if (buttons.get("skip").equals(hotKey.mediaKey.toString())) {
                application.connector.out.println("nexttrack");
            } else if (buttons.get("prev").equals(hotKey.mediaKey.toString())) {
                application.connector.out.println("prevtrack");
            }
        }
    }

    /**
     * <p>setRepeatState.</p>
     *
     * @param state a {@link RepeatState} object
     */
    public void setRepeatState (RepeatState state) {
    }

    /**
     * <p>onSliderChange.</p>
     */
    @FXML
    protected void onSliderChange() {
        application.connector.out.println("volume " + Math.round(volume.getValue()));
        volLabel.setText("Volume: " + Math.round(volume.getValue()) + "%");

    }

    /** {@inheritDoc} */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Platform.runLater(() -> {
            RepeatState state = (RepeatState) evt.getNewValue();
            switch (state) {
                case NO_REPEAT -> repeat.setText("Currently not repeating");
                case REPEAT_QUEUE -> repeat.setText("Repeating the Queue");
                case REPEAT_SINGLE -> repeat.setText("Repeating the Song");
            }
        });
    }

    /**
     * <p>Setter for the field <code>volume</code>.</p>
     *
     * @param vol a int
     */
    public void setVolume(int vol) {
        volume.setValue(vol);
        volLabel.setText("Volume: " + vol + "%");
    }

    @FXML
    private void onQuitButtonClick() {
        int width = 320;
        int height = 180;
        application.exitStage.setX(application.stage.getX() + ((application.stage.getWidth() - width) / 2));
        application.exitStage.setY(application.stage.getY() + ((application.stage.getHeight() - height) / 2));
        application.exitController.open(true);
    }

    @FXML
    private void onMinimizeButtonClick() {
        application.stage.setIconified(true);
    }

    @FXML
    private void onMaximizeButtonClick() {
        application.stage.setMaximized(!application.stage.isMaximized());
    }

    @FXML
    private void onPauseBtn() {
        application.connector.out.println("playpause");
    }

    @FXML
    private void onSkipBtn() {
        application.connector.out.println("nexttrack");
    }

    @FXML
    private void onPrevBtn() {
        application.connector.out.println("prevtrack");
    }

    @FXML
    private void comboAction() throws IOException {
        if (serverBox.getValue().getServerName().equals("+")) {
            application.reset();
            return;
        }
        Writer.write(serverBox.getValue().getServerName(), new File("code.txt"));
        application.connector.connect(serverBox.getValue().getUUID());
    }

    public void setComboBox(String servername, String id) {
        serverBox.getItems().stream().filter(s -> s.getServerName().equals(servername)).findFirst().ifPresentOrElse(s -> {
            serverBox.setValue(s);
        }, () -> {
            Server s = new Server(servername, id);
            serverBox.getItems().add(serverBox.getItems().size() - 1, s);
            serverBox.setValue(s);
        });
    }
}
