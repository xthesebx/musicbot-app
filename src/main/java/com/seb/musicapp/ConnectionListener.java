package com.seb.musicapp;


import com.hawolt.logger.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketException;

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
     * @param connector a {@link com.seb.musicapp.Connector} object
     * @param application a {@link com.seb.musicapp.Main} object
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
                if (s.equals("close")) {
                    application.reset();
                    connector.socket.close();
                    return;
                } else if (s.equals("idle")) application.discordActivity.setIdlePresence();
                else if (s.startsWith("channel ")) application.discordActivity.addJoin(s.substring(s.indexOf(" ") + 1));
                else try {
                        application.queueController.updateTable(new JSONObject(s));
                    } catch (JSONException e) {
                        Logger.error(s);
                    }
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof SocketException) {
                application.reset();
            }
            else
                e.printStackTrace();
        }
    }
}
