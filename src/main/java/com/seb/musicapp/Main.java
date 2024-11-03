package com.seb.musicapp;

import Discord.DiscordActivity;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * <p>Main class.</p>
 *
 * @author xXTheSebXx
 * @version 1.0-SNAPSHOT
 */
public class Main extends Application {

    //TODO: add search function for songs/playlists
    //TODO: add more sources (soundcloud?)

    /** Constant <code>DEBUG=false</code> */
    public static boolean DEBUG = false;
    /**
     * Connector object for connection to
     */
    public Connector connector;
    private Stage stage;
    /**
     * Scene for connector and main window
     */
    public Scene scene;
    /**
     * Stage for the queue window
     */
    public Stage queueStage;
    /**
     * Controller for the queue window
     */
    public QueueController queueController;
    /**
     * controller for the mainwindow
     */
    public MainWindowController mainWindowController;
    private ConnectController connectController;
    /**
     * the discord activity
     */
    public DiscordActivity discordActivity;

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        initialise();
        DiscordActivity.create().ifPresent(this::setDiscordActivity);
    }

    private void setDiscordActivity(DiscordActivity discordActivity) {
        this.discordActivity = discordActivity;
    }

    /**
     * <p>initialise.</p>
     *
     * @throws java.io.IOException if any.
     */
    public void initialise() throws IOException {
        this.connector = new Connector(this);
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Connect.fxml"));
        scene = new Scene(fxmlLoader.load(), 400, 200);
        stage.setTitle("Connect");
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(_ -> System.exit(0));
        connectController = fxmlLoader.getController();
        connectController.setApplication(this);
    }

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * <p>connect.</p>
     *
     * @param id a {@link java.lang.String} object
     */
    public void connect(String id) {
        try {
            connector.connect(id);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("MainWindow.fxml"));
        //Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        try {
            scene.setRoot(fxmlLoader.load());
            mainWindowController = fxmlLoader.getController();
            mainWindowController.setApplication(this);
            stage.setTitle("Music Bot App");
            stage.setScene(scene);
            mainWindowController.init();
            queueStage = new Stage();
            queueStage.setTitle("Queue");
            fxmlLoader = new FXMLLoader(Main.class.getResource("Queue.fxml"));
            Scene queueScene = new Scene(fxmlLoader.load(), 320, 800);
            queueStage.setTitle("Queue");
            queueStage.setScene(queueScene);
            queueStage.setOnCloseRequest(event -> {
                event.consume();
                queueStage.hide();
            });
            FXMLLoader finalFxmlLoader = fxmlLoader;
            queueController = finalFxmlLoader.getController();
            queueController.setApp(this);
            queueController.addPropertyChangeListener(mainWindowController);
            new Thread(() -> ((QueueController) finalFxmlLoader.getController()).setItems()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>reset.</p>
     */
    public void reset() {
        mainWindowController.getProvider().reset();
        Platform.runLater(() -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Connect.fxml"));
                scene.setRoot(fxmlLoader.load());
                stage.setTitle("Connect");
                stage.setScene(scene);
                stage.show();
                connectController = fxmlLoader.getController();
                connectController.setApplication(this);
                queueStage.close();
            } catch (IOException e) {

            }
        });
    }
}
