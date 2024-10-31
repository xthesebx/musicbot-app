package com.seb.musicapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Connector {

    public Socket socket;
    public PrintWriter out;
    public BufferedReader in;
    Main application;
    String ip;

    public Connector(Main application) {
        this.application = application;
        ip = "212.132.98.148";
        if (Main.DEBUG) ip = "127.0.0.1";
    }

    public void connect(String id) throws IOException {
        socket = new Socket(ip, 4269);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out.println(id.strip());
        String s = in.readLine();
        if (s.equals("no")) {
            throw new IOException();
        }
        new Thread(new ConnectionListener(this, application)).start();
    }

    public void shuffle() {
        out.println("shuffle");
    }
}
