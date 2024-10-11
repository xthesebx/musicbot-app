package com.seb.musicapp;


import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketException;

public class ConnectionListener implements Runnable {

    private final Connector connector;
    private final Main application;

    public ConnectionListener(Connector connector, Main application) {
        this.connector = connector;
        this.application = application;
    }

    @Override
    public void run()  {
        String s;
        try {
            while ((s = connector.in.readLine()) != null) {
                application.queueController.updateTable(new JSONObject(s));
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof SocketException) {
                try {
                    application.initialise();
                    application.queueStage.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                application.mainWindowController.getProvider().reset();
            }
            else
                e.printStackTrace();
        }
    }
}
