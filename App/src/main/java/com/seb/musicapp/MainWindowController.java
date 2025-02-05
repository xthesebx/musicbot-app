package com.seb.musicapp;

import com.hawolt.logger.Logger;
import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.MediaKey;
import com.tulskiy.keymaster.common.Provider;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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

        url.setOnKeyPressed(e -> {
            if (e.getCode().getCode() == 10) {
                onPlay();
            }
        });

        if (application.theme == Theme.Dark) Platform.runLater(() -> modeChange.setText("Light Mode"));
        else if (application.theme == Theme.Light) Platform.runLater(() -> modeChange.setText("Dark Mode"));
        StreamerController.dragHandler(outerBox);
    }

    /**
     * <p>Setter for the field <code>application</code>.</p>
     *
     * @param application a {@link com.seb.musicapp.Main} object
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
        application.scene.setOnKeyPressed(e -> {
            if (!MODIFIERS.contains(e.getCode().getCode())) {
                KeyStroke keyStroke = KeyStroke.getKeyStroke(e.getCode().getCode(), 0);
                PlayPause.setText(keyStroke.toString().replaceAll("pressed ", ""));
                file.put("play", PlayPause.getText());
                try {
                    PrintWriter hotkeyWriter = new PrintWriter("hotkeys");
                    hotkeyWriter.println(file);
                    hotkeyWriter.close();
                    provider.unregister(KeyStroke.getKeyStroke(buttons.get("play")));
                    buttons.put("play", keyStroke.toString().replaceAll("pressed ", ""));
                    provider.register(keyStroke, this);
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
                application.scene.setOnKeyPressed(null);
                changePlay.setDisable(false);
            }
        });
    }
    /**
     * <p>onChangeSkip.</p>
     */
    @FXML
    protected void onChangeSkip() {
        changePlay.setDisable(true);
        changePrev.setDisable(false);
        changeSkip.setDisable(true);
        application.scene.setOnKeyPressed(e -> {
            if (!MODIFIERS.contains(e.getCode().getCode())) {
                KeyStroke keyStroke = KeyStroke.getKeyStroke(e.getCode().getCode(), 0);
                Skip.setText(keyStroke.toString().replaceAll("pressed ", ""));
                file.put("skip", PlayPause.getText());
                try {
                    PrintWriter hotkeyWriter = new PrintWriter("hotkeys");
                    hotkeyWriter.println(file);
                    hotkeyWriter.close();
                    provider.unregister(KeyStroke.getKeyStroke(buttons.get("skip")));
                    buttons.put("skip", keyStroke.toString().replaceAll("pressed ", ""));
                    provider.register(keyStroke, this);
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
                application.scene.setOnKeyPressed(null);
                changeSkip.setDisable(false);
            }
        });
    }
    /**
     * <p>onChangePrev.</p>
     */
    @FXML
    protected void onChangePrev() {
        changePlay.setDisable(true);
        changeSkip.setDisable(false);
        changePrev.setDisable(true);
        application.scene.setOnKeyPressed(e -> {
            if (!MODIFIERS.contains(e.getCode().getCode())) {
                KeyStroke keyStroke = KeyStroke.getKeyStroke(e.getCode().getCode(), 0);
                prev.setText(keyStroke.toString().replaceAll("pressed ", ""));
                file.put("prev", prev.getText());
                try {
                    PrintWriter hotkeyWriter = new PrintWriter("hotkeys");
                    hotkeyWriter.println(file);
                    hotkeyWriter.close();
                    provider.unregister(KeyStroke.getKeyStroke(buttons.get("prev")));
                    buttons.put("prev", keyStroke.toString().replaceAll("pressed ", ""));
                    provider.register(keyStroke, this);
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
                application.scene.setOnKeyPressed(null);
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
        if (application.queueStage.isShowing()) {
            application.queueStage.toFront();
            if (application.queueStage.isIconified())
                application.queueStage.setIconified(false);
        }
        else application.queueStage.show();
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
        if (application.streamerStage.isShowing()) {
            application.streamerStage.toFront();
            if (application.streamerStage.isIconified())
                application.streamerStage.setIconified(false);
        }
        else application.streamerStage.show();
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
     * @param state a {@link com.seb.musicapp.RepeatState} object
     */
    public void setRepeatState (RepeatState state) {
    }

    /**
     * <p>onSliderChange.</p>
     */
    @FXML
    protected void onSliderChange() {
        application.connector.out.println("volume " + Math.round(volume.getValue()));
    }

    /** {@inheritDoc} */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Platform.runLater(() -> {
            RepeatState state = (RepeatState) evt.getNewValue();
            switch (state) {
                case NO_REPEAT -> repeat.setText("No Repeat");
                case REPEAT_QUEUE -> repeat.setText("Repeats the Queue");
                case REPEAT_SINGLE -> repeat.setText("Repeats the Song");
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
    }

    @FXML
    private void onQuitButtonClick() {
        System.exit(0);
    }

    @FXML
    private void onMinimizeButtonClick() {
        application.stage.setIconified(true);
    }

    @FXML
    private void onMaximizeButtonClick() {
        application.stage.setMaximized(!application.stage.isMaximized());
    }
}
