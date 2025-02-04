package com.seb.musicapp;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class StreamerController {
    @FXML
    protected TextField nameField;
    @FXML
    protected Button activateMode;
    Main application;

    public void initialize(Main application) {
        this.application = application;
        nameField.setOnKeyPressed(e -> {
            if (e.getCode().getCode() == 10) {
                onSetStreamerMode();
            }
        });
    }

    @FXML
    protected void onSetStreamerMode() {
        application.connector.out.println("streamer " + nameField.getText());
        nameField.setText("");
    }
}
