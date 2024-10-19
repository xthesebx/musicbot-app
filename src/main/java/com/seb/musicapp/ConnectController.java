package com.seb.musicapp;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ConnectController {
    @FXML
    private TextField ID;
    private Main application;

    @FXML
    protected void onHelloButtonClick() {
        application.connect(ID.getText());
    }

    public ConnectController() {
        ID.setOnKeyPressed(e -> application.connect(ID.getText()));
    }

    public void setApplication(Main application) {
        this.application = application;
    }
}