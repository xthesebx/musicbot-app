package com.seb.musicapp.connect;

import com.hawolt.logger.Logger;
import com.seb.musicapp.Main;
import com.seb.musicapp.common.WrongCodeException;
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
                ID.setText("");
                error.setTextFill(Paint.valueOf("#ff0000"));
            } else {
                Logger.error(e);
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
                } catch (InterruptedException ignored) {}
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
            Main.dragHandler(outerBox);
        }).start();
    }

    @FXML
    private void onQuitButtonClick() {
        if (application.connectStage != null) {
            application.connectStage.close();
            return;
        }
        int width = 320;
        int height = 180;
        application.exitStage.setX(application.stage.getX() + ((application.stage.getWidth() - width) / 2));
        application.exitStage.setY(application.stage.getY() + ((application.stage.getHeight() - height) / 2));
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
     * @param application a {@link Main} object
     */
    public void setApplication(Main application) {
        this.application = application;
    }
}
