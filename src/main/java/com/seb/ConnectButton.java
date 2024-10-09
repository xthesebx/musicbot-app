package com.seb;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class ConnectButton extends JButton implements ActionListener {

    private final Main main;
    public static JFrame frame;

    public ConnectButton(Main main) {
        super("Connect");
        this.main = main;
        this.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            Main.socket = new Socket("212.132.98.148", 4269);
            Main.out = new PrintWriter(Main.socket.getOutputStream(), true);
            Main.in = new BufferedReader(new InputStreamReader(Main.socket.getInputStream()));
            Main.out.println(Main.tf.getText().strip());
            String s = Main.in.readLine();
            System.out.println(s);
            if (s.equals("no")) return;
            System.out.println("connected");
            new Thread(new QueueListener()).start();
            System.out.println("thread running");
            frame = new RealFrame(main);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
