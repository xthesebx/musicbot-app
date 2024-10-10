package com.seb;


import javax.swing.*;
import java.awt.*;

public class Main {

    public static JTextField tf;
    public static JFrame frame;
    public static JPanel panel;
    public static ConnectButton button;
    public static boolean DEBUG = false;

    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        frame = new JFrame();
        frame.setTitle("HotKeys");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = new JPanel();
        frame.add(panel);
        panel.setLayout(new BorderLayout());
        tf = new JTextField();
        tf.setSize(500, 200);
        tf.setText("Code");
        panel.add(tf, BorderLayout.CENTER);
        button = new ConnectButton(this);
        panel.add(button, BorderLayout.EAST);
        frame.setSize(500, 200);
        frame.setVisible(true);
    }
}