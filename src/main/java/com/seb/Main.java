package com.seb;

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

public class Main implements HotKeyListener {

    public static Socket socket;
    public static PrintWriter out;
    public static BufferedReader in;
    public static JTextField tf;
    public static JFrame frame;
    public static JPanel panel;
    public static ConnectButton button;
    public static Provider provider;
    public static HashMap<String, String> buttons = new HashMap<>();

    public static void main(String[] args) throws IOException {
        new Main();
    }

    public Main() throws IOException {
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

        provider = Provider.getCurrentProvider(false);
        /*provider.register(KeyStroke.getKeyStroke("ctrl MULTIPLY"), this);
        provider.register(KeyStroke.getKeyStroke("ctrl SUBTRACT"), this);
        provider.register(KeyStroke.getKeyStroke("ctrl ADD"), this);*/

    }

    @Override
    public void onHotKey(HotKey hotKey) {
        if (hotKey.keyStroke != null) {
            if (buttons.get("play").equals(hotKey.keyStroke.toString().replaceAll("pressed ", ""))) {
                out.println("playpause");
            } else if (buttons.get("skip").equals(hotKey.keyStroke.toString().replaceAll("pressed ", ""))) {
                out.println("nexttrack");
            }
        } else if (hotKey.mediaKey != null) {
            if (buttons.get("play").equals(hotKey.mediaKey.toString())) {
                out.println("playpause");
            } else if (buttons.get("skip").equals(hotKey.mediaKey.toString())) {
                out.println("nexttrack");
            }
        }
    }
}