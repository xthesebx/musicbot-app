package com.seb.musicapp.connect;

import com.hawolt.logger.Logger;
import com.seb.io.Writer;
import com.seb.musicapp.Main;
import com.seb.musicapp.common.WrongCodeException;

import java.io.*;
import java.net.*;

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
     * @param application a {@link Main} object
     */
    public Connector(Main application) {
        this.application = application;
        try {
            ip = InetAddress.getByName("sebgameservers.de").getHostAddress();
        } catch (UnknownHostException e) {
            Logger.error(e);
        }
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
            throw new WrongCodeException("Wrong code, maybe the code got reset?");
        }
        new Thread(new ConnectionListener(this, application)).start();
        socket.setKeepAlive(true);
        Writer.write(id, new File("code.txt"));
    }

    /**
     * <p>shuffle.</p>
     */
    public void shuffle() {
        out.println("shuffle");
    }
}
