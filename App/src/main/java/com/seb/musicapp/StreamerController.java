package com.seb.musicapp;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class StreamerController {
    @FXML
    protected TextField nameField;
    @FXML
    protected Button activateMode;
    @FXML
    private HBox outerBox;
    Main application;

    public void initialize(Main application) {
        this.application = application;
        nameField.setOnKeyPressed(e -> {
            if (e.getCode().getCode() == 10) {
                onSetStreamerMode();
            }
        });
        dragHandler(outerBox);
    }

    static void dragHandler(HBox outerBox) {
        new Thread(() -> {
            while (outerBox == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            DoubleProperty x = new SimpleDoubleProperty();
            DoubleProperty y = new SimpleDoubleProperty();
            outerBox.setOnMousePressed(e -> {
                x.set(e.getSceneX());
                y.set(e.getSceneY());
            });
            outerBox.setOnMouseDragged(e -> {
                outerBox.getScene().getWindow().setX(e.getScreenX() - x.get());
                outerBox.getScene().getWindow().setY(e.getScreenY() - y.get());
            });
        }).start();
    }

    @FXML
    protected void onSetStreamerMode() {
        application.connector.out.println("streamer " + nameField.getText());
        nameField.setText("");
    }

    @FXML
    private void onQuitButtonClick() {
        application.streamerStage.close();
    }

    @FXML
    private void onMinimizeButtonClick() {
        application.streamerStage.setIconified(true);
    }

    @FXML
    private void onMaximizeButtonClick() {
        application.streamerStage.setMaximized(!application.streamerStage.isMaximized());
    }
}
