package com.seb;

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.MediaKey;
import com.tulskiy.keymaster.common.Provider;
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
import java.util.HashMap;
import java.util.List;

public class RealFrame extends JFrame implements HotKeyListener {

    private final Provider provider;
    private final HashMap<String, String> buttons = new HashMap<>();
    public static QueueFrame queue;
    private static final List<Integer> MODIFIERS = Arrays.asList(KeyEvent.VK_ALT, KeyEvent.VK_CONTROL, KeyEvent.VK_SHIFT, KeyEvent.VK_META);
    private final RealFrame me = this;

    public Provider getProvider() {
        return provider;
    }

    public RealFrame(Main main) {
        super("Controller");
        provider = Provider.getCurrentProvider(false);
        queue = new QueueFrame();
        String play = "", skip = "";
        JSONObject file = new JSONObject();
        try {
            file = new JSONObject(Files.readString(Path.of("hotkeys")));
            play = file.getString("play");
            if (play.isEmpty()) play = "MEDIA_PLAY_PAUSE";
            skip = file.getString("skip");
            if (skip.isEmpty()) skip = "MEDIA_NEXT_TRACK";
        } catch (IOException | JSONException e) {
            if (!(e instanceof NoSuchFileException))
                System.err.println(e);
        }
        if (!play.equals("MEDIA_PLAY_PAUSE"))
            provider.register(KeyStroke.getKeyStroke(play), this);
        else provider.register(MediaKey.MEDIA_PLAY_PAUSE, this);
        buttons.put("play", play);
        if (!skip.equals("MEDIA_NEXT_TRACK"))
            provider.register(KeyStroke.getKeyStroke(skip), this);
        else provider.register(MediaKey.MEDIA_NEXT_TRACK, this);

        buttons.put("skip", skip);
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
                            provider.unregister(KeyStroke.getKeyStroke(buttons.get("play")));
                            buttons.put("play", keyStroke.toString().replaceAll("pressed ", ""));
                            provider.register(keyStroke, me);
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
                if (buttons.get("skip").equals("MEDIA_PLAY_PAUSE"))
                    provider.unregister(MediaKey.MEDIA_PLAY_PAUSE);
                else provider.unregister(KeyStroke.getKeyStroke(buttons.get("skip")));
                buttons.put("play", "MEDIA_PLAY_PAUSE");
                provider.register(MediaKey.MEDIA_PLAY_PAUSE, this);
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
                            provider.unregister(KeyStroke.getKeyStroke(buttons.get("skip")));
                            buttons.put("skip", keyStroke.toString().replaceAll("pressed ", ""));
                            provider.register(keyStroke, me);
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
                if (buttons.get("skip").equals("MEDIA_NEXT_TRACK"))
                    provider.unregister(MediaKey.MEDIA_NEXT_TRACK);
                else provider.unregister(KeyStroke.getKeyStroke(buttons.get("skip")));
                buttons.put("skip", "MEDIA_NEXT_TRACK");
                provider.register(MediaKey.MEDIA_NEXT_TRACK, this);
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });
        midpanel.add(useSkipKey, BorderLayout.EAST);

        JPanel bottompanel = new JPanel(new BorderLayout());
        JButton join = new JButton("Join");
        join.addActionListener(e -> {
            ConnectButton.out.println("join");
        });
        JButton leave = new JButton("Leave");
        leave.addActionListener(e -> {
            ConnectButton.out.println("leave");
        });
        JTextField url = new JTextField();
        JButton playSong = new JButton("play");
        playSong.addActionListener(e -> {
            ConnectButton.out.println("play " + url.getText().strip());
            url.setText("");
        });
        url.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    ConnectButton.out.println("play " + url.getText().strip());
                    url.setText("");
                }
            }
        });
        JPanel bottompanel2 = new JPanel(new BorderLayout());
        JButton shuffle = new JButton("Shuffle");
        shuffle.addActionListener(e -> {
            ConnectButton.out.println("shuffle");
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
        Main.frame.setVisible(false);
        this.add(panel);
        this.pack();
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    @Override
    public void onHotKey(HotKey hotKey) {
        if (hotKey.keyStroke != null) {
            if (buttons.get("play").equals(hotKey.keyStroke.toString().replaceAll("pressed ", ""))) {
                ConnectButton.out.println("playpause");
            } else if (buttons.get("skip").equals(hotKey.keyStroke.toString().replaceAll("pressed ", ""))) {
                ConnectButton.out.println("nexttrack");
            }
        } else if (hotKey.mediaKey != null) {
            if (buttons.get("play").equals(hotKey.mediaKey.toString())) {
                ConnectButton.out.println("playpause");
            } else if (buttons.get("skip").equals(hotKey.mediaKey.toString())) {
                ConnectButton.out.println("nexttrack");
            }
        }
    }
}
