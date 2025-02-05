package com.seb.musicapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * <p>Connector class.</p>
 *
 * @author xXTheSebXx
 * @version 1.0-SNAPSHOT
 */
public class Connector {
    /**
     * Socket connection to the bot
     */
    public Socket socket;
    /**
     * printwriter for the socket
     */
    public PrintWriter out;
    /**
     * bufferedreader for the socket
     */
    public BufferedReader in;
    Main application;
    String ip;

    /**
     * <p>Constructor for Connector.</p>
     *
     * @param application a {@link com.seb.musicapp.Main} object
     */
    public Connector(Main application) {
        this.application = application;
        ip = "212.132.98.148";
        if (Main.DEBUG) ip = "127.0.0.1";
    }

    /**
     * <p>connect.</p>
     *
     * @param id a {@link java.lang.String} object
     * @throws java.io.IOException if any.
     */
    public void connect(String id) throws IOException {
        socket = new Socket(ip, 4269);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out.println(id.strip());
        String s = in.readLine();
        if (s.equals("no")) {
            throw new WrongCodeException("Wrong code");
        }
        new Thread(new ConnectionListener(this, application)).start();
    }

    /**
     * <p>shuffle.</p>
     */
    public void shuffle() {
        out.println("shuffle");
    }
}
