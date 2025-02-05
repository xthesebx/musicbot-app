package com.seb.musicapp;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;

/**
 * <p>ConnectController class.</p>
 *
 * @author xXTheSebXx
 * @version 1.0-SNAPSHOT
 */
public class ConnectController {
    @FXML
    private TextField ID;
    private Main application;
    @FXML
    private HBox outerBox;
    @FXML
    private Button minimize, maximize, quit;
    /**
     * <p>onHelloButtonClick.</p>
     */
    @FXML
    protected void onHelloButtonClick() {
        application.connect(ID.getText());
    }

    /**
     * <p>Constructor for ConnectController.</p>
     */
    public ConnectController() {
        new Thread(() -> {
            while (ID == null) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {}
            }
            ID.setOnKeyPressed(e -> {
                if (e.getCode().equals(KeyCode.ENTER)) application.connect(ID.getText());
            });
            StreamerController.dragHandler(outerBox);
        }).start();
    }

    @FXML
    private void onQuitButtonClick() {
        System.exit(0);
    }

    @FXML
    private void onMinimizeButtonClick() {
        application.stage.setIconified(true);
    }

    @FXML
    private void onMaximizeButtonClick() {
        application.stage.setMaximized(!application.stage.isMaximized());
    }

    /**
     * <p>Setter for the field <code>application</code>.</p>
     *
     * @param application a {@link com.seb.musicapp.Main} object
     */
    public void setApplication(Main application) {
        this.application = application;
    }
}
