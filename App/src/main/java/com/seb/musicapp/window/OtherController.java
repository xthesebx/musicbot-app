package com.seb.musicapp.window;

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.MediaKey;
import com.tulskiy.keymaster.common.Provider;
import javafx.scene.control.Label;

import javax.swing.*;
import java.util.HashMap;

import static com.seb.musicapp.Main.application;
import static com.seb.musicapp.window.MainWindowController.buttons;

public class OtherController implements HotkeyController, HotKeyListener {

    private final Provider provider = Provider.getCurrentProvider(false);

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

    @Override
    public void setHotkeys(String play, String skip, String prevString) {
        if (!play.equals("MEDIA_PLAY_PAUSE") && !play.isEmpty()){
            provider.register(KeyStroke.getKeyStroke(play), this);
        }
        else if (!play.isEmpty()) {
            provider.register(MediaKey.MEDIA_PLAY_PAUSE, this);
        }
        if (!skip.equals("MEDIA_NEXT_TRACK") && !skip.isEmpty()) {
            provider.register(KeyStroke.getKeyStroke(skip), this);
        }
        else if (!skip.isEmpty()) {
            provider.register(MediaKey.MEDIA_NEXT_TRACK, this);
        }
        if (!prevString.equals("MEDIA_PREV_TRACK") && !prevString.isEmpty()) {
            provider.register(KeyStroke.getKeyStroke(prevString), this);
        }
        else if (!prevString.isEmpty()) {
            provider.register(MediaKey.MEDIA_PREV_TRACK, this);
        }
    }

    @Override
    public void onChangePlay(boolean media, KeyStroke keyStroke) {
        if (media) provider.unregister(MediaKey.valueOf(buttons.get("play")));
        else provider.unregister(KeyStroke.getKeyStroke(buttons.get("play")));
        provider.register(keyStroke, this);
    }

    @Override
    public void onChangeSkip(boolean media, KeyStroke keyStroke) {
        if (media) provider.unregister(MediaKey.valueOf(buttons.get("skip")));
        else provider.unregister(KeyStroke.getKeyStroke(buttons.get("skip")));
        provider.register(keyStroke, this);
    }

    @Override
    public void onChangePrev(boolean media, KeyStroke keyStroke) {
        if (media) provider.unregister(MediaKey.valueOf(buttons.get("prev")));
        else provider.unregister(KeyStroke.getKeyStroke(buttons.get("prev")));
        provider.register(keyStroke, this);
    }

    @Override
    public void onMediaPlay(Label PlayPause) {
        provider.unregister(KeyStroke.getKeyStroke(PlayPause.getText()));
        provider.register(MediaKey.MEDIA_PLAY_PAUSE, this);
    }

    @Override
    public void onMediaSkip(Label Skip) {
        provider.unregister(KeyStroke.getKeyStroke(Skip.getText()));
        provider.register(MediaKey.MEDIA_NEXT_TRACK, this);
    }

    @Override
    public void onMediaPrev(Label prev) {
        provider.unregister(KeyStroke.getKeyStroke(prev.getText()));
        provider.register(MediaKey.MEDIA_PREV_TRACK, this);
    }

    /**
     * <p>Getter for the field <code>provider</code>.</p>
     *
     * @return a {@link com.tulskiy.keymaster.common.Provider} object
     */
    public Provider getProvider() {
        return provider;
    }


}
