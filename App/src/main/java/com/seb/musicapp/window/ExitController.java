package com.seb.musicapp.window;

import com.seb.musicapp.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class ExitController {
    @FXML
    Button disconnect;
    @FXML
    Button exit;
    @FXML
    Button cancel;
    @FXML
    HBox outerBox;
    @FXML
    HBox innerBox;
    @FXML
    VBox vBox;
    @FXML
    VBox vBox1;

    Main main;

    public void init(Main main) {
        this.main = main;
        Main.dragHandler(outerBox);
    }

    public void open(boolean disconnect) {
        this.disconnect.setVisible(disconnect);
        this.disconnect.setMaxWidth(0);
        this.disconnect.setMaxHeight(0);
        main.exitStage.show();
    }

    public void onCancel() {
        main.exitStage.close();
    }

    public void onDisconnect() {
        main.reset();
        main.exitStage.close();
    }

    public void onExit() {
        System.exit(0);
    }

    public void onQuitButtonClick() {
        main.exitStage.close();
    }
}
