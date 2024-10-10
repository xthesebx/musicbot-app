package com.seb;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class ConnectButton extends JButton implements ActionListener {

    public static Socket socket;
    public static PrintWriter out;
    public static BufferedReader in;
    private final Main main;
    public static RealFrame frame;

    public ConnectButton(Main main) {
        super("Connect");
        this.main = main;
        this.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            String ip = "212.132.98.148";
            if (Main.DEBUG) ip = "127.0.0.1";
            socket = new Socket(ip, 4269);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.println(Main.tf.getText().strip());
            Main.tf.setText("");
            String s = in.readLine();
            if (s.equals("no")) return;
            new Thread(new QueueListener()).start();
            frame = new RealFrame(main);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
