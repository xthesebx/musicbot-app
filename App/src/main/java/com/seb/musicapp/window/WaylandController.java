package com.seb.musicapp.window;

import com.hawolt.logger.Logger;
import com.seb.musicapp.MPRIS.MPRIS;
import com.seb.musicapp.MPRIS.MPRISBuilder;
import com.seb.musicapp.MPRIS.TrackListImpl;
import javafx.scene.control.Label;
import org.freedesktop.dbus.exceptions.DBusException;

import javax.swing.*;


import static com.seb.musicapp.Main.application;

public class WaylandController implements HotkeyController {

    public static MPRIS mpris;
    public static TrackListImpl trackList = new TrackListImpl.TrackListBuilder().build();

    public WaylandController() throws DBusException {

        mpris = new MPRISBuilder()
                // Indicates that the application can be quit via MPRIS
                .setCanQuit(false)

                .setHasTrackList(true)
                .addTrackListSupport(trackList)
                // Set the display name of the player
                .setIdentity("musicbotApp")
                // Indicates that the player can be controlled via MPRIS
                .setCanControl(true)
                // Indicates that playback can be started via MPRIS
                .setCanPlay(true)
                // Indicates that playback can be paused via MPRIS
                .setCanPause(true)
                // Indicates that the player can skip to the next track via MPRIS
                .setCanGoNext(true)
                // Indicates that the player can skip to the previous track via MPRIS
                .setCanGoPrevious(true)
                // Handle MPRIS Play/Pause toggle
                .setOnPlayPause(() -> {
                    application.connector.out.println("playpause");
                })
                // Handle MPRIS Play command
                .setOnPlay(() -> application.connector.out.println("playpause"))
                // Handle MPRIS Pause command
                .setOnPause(() -> {
                    application.connector.out.println("playpause");
                })
                // Handle MPRIS Next Track command
                .setOnNext(() -> application.connector.out.println("nexttrack"))
                // Handle MPRIS Previous Track command
                .setOnPrevious(() -> application.connector.out.println("prevtrack"))
                .setOnSeek((seek) -> {})
                .build("musicbotApp");
    }

    @Override
    public void setHotkeys(String play, String skip, String prevString) {

    }

    @Override
    public void onChangePlay(boolean media, KeyStroke keyStroke) {

    }

    @Override
    public void onChangeSkip(boolean media, KeyStroke keyStroke) {

    }

    @Override
    public void onChangePrev(boolean media, KeyStroke keyStroke) {

    }

    @Override
    public void onMediaPlay(Label PlayPause) {

    }

    @Override
    public void onMediaSkip(Label Skip) {

    }

    @Override
    public void onMediaPrev(Label prev) {

    }
}
