package com.seb.musicapp.connect;


import com.hawolt.logger.Logger;
import com.seb.musicapp.Main;
import javafx.application.Platform;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * <p>ConnectionListener class.</p>
 *
 * @author xXTheSebXx
 * @version 1.0-SNAPSHOT
 */
public class ConnectionListener implements Runnable {

    private final Connector connector;
    private final Main application;

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
                    //application.discordActivity.setIdlePresence();
                    Platform.runLater(() -> {
                        application.stage.setTitle("Music Bot App");
                        application.mainWindowController.getTitle().setText("Music Bot App");
                    });
                }
                else if (s.equals("playing")) {
                    Platform.runLater(() ->  {
                        application.stage.setTitle(application.queueController.songList.get(application.queueController.i-1).getSongName());
                        application.mainWindowController.getTitle().setText(application.queueController.songList.get(application.queueController.i-1).getSongName());
                    });
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
