package com.seb;

import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketException;

public class QueueListener implements Runnable {

    public QueueListener() {
    }

    @Override
    public void run()  {
        String s;
        try {
            while ((s = ConnectButton.in.readLine()) != null) {
                RealFrame.queue.updateTable(new JSONObject(s));
            }
        } catch (IOException e) {
            if (e instanceof SocketException) {
                ConnectButton.frame.dispose();
                RealFrame.queue.dispose();
                Main.frame.setVisible(true);
                ConnectButton.frame.getProvider().reset();
            }
            else
                e.printStackTrace();
        }
    }
}
