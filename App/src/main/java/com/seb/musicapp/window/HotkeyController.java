package com.seb.musicapp.window;

import javafx.scene.control.Label;

import javax.swing.*;
import java.util.HashMap;

public interface HotkeyController {
    void setHotkeys(String play, String skip, String prevString);
    void onChangePlay(boolean media, KeyStroke keyStroke);
    void onChangeSkip(boolean media, KeyStroke keyStroke);
    void onChangePrev(boolean media, KeyStroke keyStroke);
    void onMediaPlay(Label PlayPause);
    void onMediaSkip(Label Skip);
    void onMediaPrev(Label prev);

}
