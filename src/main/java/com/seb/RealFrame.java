package com.seb;

import com.tulskiy.keymaster.common.MediaKey;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class RealFrame extends JFrame {

    public static QueueFrame queue = new QueueFrame();
    public static final List<Integer> MODIFIERS = Arrays.asList(KeyEvent.VK_ALT, KeyEvent.VK_CONTROL, KeyEvent.VK_SHIFT, KeyEvent.VK_META);

    public RealFrame(Main main) {
        super("Controller");
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
        JPanel midpanel = new JPanel();
        toppanel.setLayout(new BorderLayout());
        midpanel.setLayout(new BorderLayout());
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
        midpanel.add(skipHotkey, BorderLayout.WEST);
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
        midpanel.add(changeSkipHotkey, BorderLayout.CENTER);

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
        midpanel.add(useSkipKey, BorderLayout.EAST);

        JPanel bottompanel = new JPanel(new BorderLayout());
        JButton join = new JButton("Join");
        join.addActionListener(e -> {
            Main.out.println("join");
        });
        JButton leave = new JButton("Leave");
        leave.addActionListener(e -> {
            Main.out.println("leave");
        });
        JTextField url = new JTextField();
        JButton playSong = new JButton("play");
        playSong.addActionListener(e -> {
            Main.out.println("play " + url.getText());
        });
        JPanel bottompanel2 = new JPanel(new BorderLayout());
        JButton shuffle = new JButton("Shuffle");
        shuffle.addActionListener(e -> {
            Main.out.println("shuffle");
        });
        JButton queue1 = new JButton("Queue");
        queue1.addActionListener(e -> {
            queue.setVisible(!queue.isVisible());
        });
        bottompanel2.add(shuffle, BorderLayout.WEST);
        bottompanel2.add(queue1, BorderLayout.EAST);
        bottompanel2.add(playSong, BorderLayout.CENTER);

        bottompanel.add(join, BorderLayout.WEST);
        bottompanel.add(leave, BorderLayout.EAST);
        bottompanel.add(url, BorderLayout.CENTER);
        bottompanel.add(bottompanel2, BorderLayout.SOUTH);

        panel.add(toppanel, BorderLayout.NORTH);
        panel.add(midpanel, BorderLayout.CENTER);
        panel.add(bottompanel, BorderLayout.SOUTH);
        Main.frame.dispose();
        this.add(panel);
        this.pack();
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
}
