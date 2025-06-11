package com.seb.musicapp.window;

import com.seb.musicapp.Main;
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
        Main.dragHandler(outerBox);
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
