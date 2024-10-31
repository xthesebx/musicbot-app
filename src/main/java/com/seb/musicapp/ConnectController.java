package com.seb.musicapp;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class ConnectController {
    @FXML
    private TextField ID;
    private Main application;

    @FXML
    protected void onHelloButtonClick() {
        application.connect(ID.getText());
    }

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
        }).start();
    }

    public void setApplication(Main application) {
        this.application = application;
    }
}