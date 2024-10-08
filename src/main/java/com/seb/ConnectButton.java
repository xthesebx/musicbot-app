package com.seb;

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.MediaKey;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class ConnectButton extends JButton implements ActionListener {

    public static final List<Integer> MODIFIERS = Arrays.asList(KeyEvent.VK_ALT, KeyEvent.VK_CONTROL, KeyEvent.VK_SHIFT, KeyEvent.VK_META);
    private final Main main;

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
            Main.out.println(Main.tf.getText());
            if (Main.in.readLine().equals("no")) return;
            String play = "", skip = "";
            JSONObject file = new JSONObject();
            try {
                file = new JSONObject(Files.readString(Path.of("hotkeys")));
                play = file.getString("play");
                skip = file.getString("skip");
            } catch (IOException | JSONException e) {
                if (!(e instanceof NoSuchFileException))
                    System.err.println(e);
            }
            if (!play.equals("MEDIA_PLAY_PAUSE"))
                Main.provider.register(KeyStroke.getKeyStroke(play), main);
            else Main.provider.register(MediaKey.MEDIA_PLAY_PAUSE, main);
            Main.buttons.put("play", play);
            if (!skip.equals("MEDIA_NEXT_TRACK"))
                Main.provider.register(KeyStroke.getKeyStroke(skip), main);
            else Main.provider.register(MediaKey.MEDIA_NEXT_TRACK, main);
            Main.buttons.put("skip", skip);
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            JPanel toppanel = new JPanel();
            JPanel bottompanel = new JPanel();
            toppanel.setLayout(new BorderLayout());
            bottompanel.setLayout(new BorderLayout());
            JTextField playhotkey = new JTextField(play);
            playhotkey.setEditable(false);
            toppanel.add(playhotkey, BorderLayout.WEST);
            JButton changePlayHotkey = new JButton("Change Play Hotkey");
            JSONObject finalFile = file;
            changePlayHotkey.addActionListener(e -> {
                changePlayHotkey.setEnabled(false);
                playhotkey.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (!MODIFIERS.contains(e.getKeyCode())) {
                            KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(e);
                            playhotkey.setText(keyStroke.toString().replaceAll("pressed ", ""));
                            finalFile.put("play", playhotkey.getText());
                            try {
                                PrintWriter hotkeyWriter = new PrintWriter("hotkeys");
                                hotkeyWriter.println(finalFile);
                                hotkeyWriter.close();
                                Main.provider.unregister(KeyStroke.getKeyStroke(Main.buttons.get("play")));
                                Main.buttons.put("play", keyStroke.toString().replaceAll("pressed ", ""));
                                Main.provider.register(keyStroke, main);
                            } catch (FileNotFoundException ex) {
                                throw new RuntimeException(ex);
                            }
                            playhotkey.removeKeyListener(this);
                            changePlayHotkey.setEnabled(true);
                        }
                    }
                });
            });
            toppanel.add(changePlayHotkey, BorderLayout.CENTER);
            JButton usePlayPause = new JButton("Use Media Key");
            usePlayPause.addActionListener(e -> {
                playhotkey.setText("MEDIA_PLAY_PAUSE");
                finalFile.put("play", playhotkey.getText());
                try {
                    PrintWriter hotkeyWriter = new PrintWriter("hotkeys");
                    hotkeyWriter.println(finalFile);
                    hotkeyWriter.close();
                    if (Main.buttons.get("skip").equals("MEDIA_PLAY_PAUSE"))
                        Main.provider.unregister(MediaKey.MEDIA_PLAY_PAUSE);
                    else Main.provider.unregister(KeyStroke.getKeyStroke(Main.buttons.get("skip")));
                    Main.buttons.put("play", "MEDIA_PLAY_PAUSE");
                    Main.provider.register(MediaKey.MEDIA_PLAY_PAUSE, main);
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            });
            toppanel.add(usePlayPause, BorderLayout.EAST);
            JTextField skipHotkey = new JTextField(skip);
            skipHotkey.setEditable(false);
            bottompanel.add(skipHotkey, BorderLayout.WEST);
            JButton changeSkipHotkey = new JButton("Change Skip Hotkey");
            changeSkipHotkey.addActionListener(e -> {
                changeSkipHotkey.setEnabled(false);
                skipHotkey.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (!MODIFIERS.contains(e.getKeyCode())) {
                            KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(e);
                            skipHotkey.setText(keyStroke.toString().replaceAll("pressed ", ""));
                            finalFile.put("skip", skipHotkey.getText());
                            try {
                                PrintWriter hotkeyWriter = new PrintWriter("hotkeys");
                                hotkeyWriter.println(finalFile);
                                hotkeyWriter.close();
                                Main.provider.unregister(KeyStroke.getKeyStroke(Main.buttons.get("skip")));
                                Main.buttons.put("skip", keyStroke.toString().replaceAll("pressed ", ""));
                                Main.provider.register(keyStroke, main);
                            } catch (FileNotFoundException ex) {
                                throw new RuntimeException(ex);
                            }
                            skipHotkey.removeKeyListener(this);
                            changeSkipHotkey.setEnabled(true);
                        }
                    }
                });
            });
            bottompanel.add(changeSkipHotkey, BorderLayout.CENTER);

            JButton useSkipKey = new JButton("Use Media Key");
            useSkipKey.addActionListener(e -> {
                skipHotkey.setText("MEDIA_NEXT_TRACK");
                finalFile.put("skip", skipHotkey.getText());
                try {
                    PrintWriter hotkeyWriter = new PrintWriter("hotkeys");
                    hotkeyWriter.println(finalFile);
                    hotkeyWriter.close();
                    if (Main.buttons.get("skip").equals("MEDIA_NEXT_TRACK"))
                        Main.provider.unregister(MediaKey.MEDIA_NEXT_TRACK);
                    else Main.provider.unregister(KeyStroke.getKeyStroke(Main.buttons.get("skip")));
                    Main.buttons.put("skip", "MEDIA_NEXT_TRACK");
                    Main.provider.register(MediaKey.MEDIA_NEXT_TRACK, main);
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            });
            bottompanel.add(useSkipKey, BorderLayout.EAST);
            panel.add(toppanel, BorderLayout.NORTH);
            panel.add(bottompanel, BorderLayout.CENTER);
            Main.frame.dispose();
            JFrame frame = new JFrame();
            frame.add(panel);
            frame.pack();
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setVisible(true);
            System.out.println("done");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
