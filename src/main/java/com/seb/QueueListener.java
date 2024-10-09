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
            while ((s = Main.in.readLine()) != null) {
                RealFrame.queue.updateTable(new JSONObject(s).getJSONArray("queue"));
            }
        } catch (IOException e) {
            if (e instanceof SocketException) {
                ConnectButton.frame.dispose();
                RealFrame.queue.dispose();
                new Main();
            }
            else
                e.printStackTrace();
        }
    }
}
