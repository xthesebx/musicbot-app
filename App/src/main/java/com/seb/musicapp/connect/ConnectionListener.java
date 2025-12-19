package com.seb.musicapp.connect;


import Discord.DiscordActivity;
import com.hawolt.logger.Logger;
import com.seb.musicapp.MPRIS.Metadata;
import com.seb.musicapp.Main;
import com.seb.musicapp.window.WaylandController;
import javafx.application.Platform;
import org.freedesktop.dbus.DBusPath;
import org.freedesktop.dbus.exceptions.DBusException;
import org.json.JSONException;
import org.json.JSONObject;
import org.mpris.mpris.PlaybackStatus;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * <p>ConnectionListener class.</p>
 *
 * @author xXTheSebXx
 * @version 1.0-SNAPSHOT
 */
public class ConnectionListener implements Runnable {

    private final Connector connector;
    private final Main application;
    private int i;
    public static int j = 0;

    /**
     * <p>Constructor for ConnectionListener.</p>
     *
     * @param connector a {@link Connector} object
     * @param application a {@link Main} object
     */
    public ConnectionListener(Connector connector, Main application) {
        this.connector = connector;
        this.application = application;
    }

    /** {@inheritDoc} */
    @Override
    public void run()  {
        String s;
        try {
            while ((s = connector.in.readLine()) != null) {
                final String message = s;
                if (s.equals("close")) {
                    application.reset();
                    connector.socket.close();
                    return;
                } else if (s.equals("idle")) {
                    application.discordActivity.ifPresent(DiscordActivity::setIdlePresence);
                    Platform.runLater(() -> {
                        application.stage.setTitle("Music Bot App");
                        application.mainWindowController.getTitle().setText("Music Bot App");
                    });
                    if (application.mainWindowController.hotkeyController instanceof WaylandController) {
                        try {
                            WaylandController.mpris.setPlaybackStatus(PlaybackStatus.STOPPED);
                        } catch (DBusException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                else if (s.startsWith("paused")) {

                    application.discordActivity.ifPresent(DiscordActivity::setIdlePresence);
                    if (application.mainWindowController.hotkeyController instanceof WaylandController) {
                        try {
                            WaylandController.mpris.setPlaybackStatus(PlaybackStatus.PAUSED);
                            WaylandController.mpris.setPosition(Integer.parseInt(s.substring(s.indexOf(" ") + 1))*1000);
                        } catch (DBusException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                else if (s.equals("playing")) {
                    Platform.runLater(() ->  {
                        application.stage.setTitle(application.queueController.songList.get(application.queueController.i-1).getSongName());
                        application.mainWindowController.getTitle().setText(application.queueController.songList.get(application.queueController.i-1).getSongName());

                    });
                    if (application.mainWindowController.hotkeyController instanceof WaylandController) {
                        try {
                            DBusPath path = new DBusPath("/" + j);
                            WaylandController.trackList.setTracks(List.of(path));
                            WaylandController.mpris.setMetadata(new Metadata.Builder()
                                    .setTitle(application.queueController.songList.get(application.queueController.i-1).getSongName())
                                    .setArtists(List.of(application.queueController.songList.get(application.queueController.i-1).getArtist()))
                                    .setURL(new URI(application.queueController.songList.get(application.queueController.i-1).getUrl()))
                                    .setLength(application.queueController.songList.get(application.queueController.i-1).getDurationInt())
                                    .setTrackID(path)
                                    .build()

                            );
                            j++;
                            WaylandController.mpris.setPlaybackStatus(PlaybackStatus.PLAYING);
                        } catch (DBusException e) {
                            throw new RuntimeException(e);
                        } catch (URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                else if (s.equals("hello")) {
                    continue;
                }
                else if (s.startsWith("channel ")) application.discordActivity.ifPresent(discord -> discord.addJoin(message.substring(message.indexOf(" ") + 1)));
                else try {
                        application.queueController.updateTable(new JSONObject(s));
                    } catch (JSONException e) {
                        Logger.error(s);
                    }
            }
        } catch (IOException e) {
            Logger.debug(e);
            application.reset();
        }
    }
}
