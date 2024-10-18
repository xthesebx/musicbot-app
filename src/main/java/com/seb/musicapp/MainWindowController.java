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
import javafx.scene.layout.GridPane;
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


public class MainWindowController implements HotKeyListener, PropertyChangeListener {

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
    private Button setStreamerMode;
    @FXML
    private TextField url;
    @FXML
    private TextField twitchname;
    @FXML
    private Button toggleRequests;
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
    private Main application;
    private final Provider provider = Provider.getCurrentProvider(false);
    private final HashMap<String, String> buttons = new HashMap<>();
    private static final List<Integer> MODIFIERS = Arrays.asList(KeyEvent.VK_ALT, KeyEvent.VK_CONTROL, KeyEvent.VK_SHIFT, KeyEvent.VK_META);
    private JSONObject file;

    public MainWindowController() {
    }

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
                System.err.println(e);
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
        twitchname.setOnKeyPressed(e -> {
            if (e.getCode().getCode() == 10) {
                onSetStreamerMode();
            }
        });
    }

    public void setApplication(Main application) {
        this.application = application;
    }

    public Provider getProvider() {
        return provider;
    }

    @FXML
    protected void onChangePlay() {
        changePlay.setDisable(true);
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
    @FXML
    protected void onChangeSkip() {
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
    @FXML
    protected void onChangePrev() {
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
    @FXML
    protected void onJoin() {
        application.connector.out.println("join");
    }
    @FXML
    protected void onLeave() {
        application.connector.out.println("leave");
    }
    @FXML
    protected void onShuffle() {
        application.connector.shuffle();
    }
    @FXML
    protected void onQueue() {
        if (application.queueStage.isShowing()) application.queueStage.toFront();
        else application.queueStage.show();
    }
    @FXML
    protected void onStop() {
        application.connector.out.println("stop");
    }
    @FXML
    protected void onSetStreamerMode() {
        application.connector.out.println("streamer " + twitchname.getText());
        twitchname.setText("");
    }
    @FXML
    protected void onPlay() {
        application.connector.out.println("play " + url.getText().strip());
        url.setText("");
    }
    @FXML
    protected void onRepeat() {
        application.connector.out.println("repeat");
    }
    @FXML
    protected void onToggleRequests() {
        application.connector.out.println("toggle");
    }

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

    public void setRepeatState (RepeatState state) {
    }

    @FXML
    protected void onSliderChange() {
        application.connector.out.println("volume " + Math.round(volume.getValue()));
    }

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

    public void setVolume(int vol) {
        volume.setValue(vol);
    }
}
