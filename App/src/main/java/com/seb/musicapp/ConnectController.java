package com.seb.musicapp;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;

import java.io.IOException;

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
    @FXML
    private Label error;
    /**
     * <p>onHelloButtonClick.</p>
     */
    @FXML
    protected void onHelloButtonClick() {
        try {
            application.connect(ID.getText());
        } catch (IOException e) {
            if (e instanceof WrongCodeException) {
                error.setVisible(true);
                error.setTextFill(Paint.valueOf("#ff0000"));
            }
        }
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
                if (e.getCode().equals(KeyCode.ENTER))
                    try {
                        application.connect(ID.getText());
                    } catch (IOException ex) {
                        if (ex instanceof WrongCodeException) {
                            error.setVisible(true);
                            error.setTextFill(Paint.valueOf("#ff0000"));
                        }
                    }
            });
            StreamerController.dragHandler(outerBox);
        }).start();
    }

    @FXML
    private void onQuitButtonClick() {
        application.exitController.open(false);
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
