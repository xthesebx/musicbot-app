package com.seb.musicapp;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>QueueController class.</p>
 *
 * @author xXTheSebXx
 * @version 1.0-SNAPSHOT
 */
public class QueueController {
    /**
     * Liste für die queue
     */
    public List<Song> queue = new ArrayList<>();
    public List<Song> songList = new ArrayList<>();
    int i = 0;

    @FXML
    TableView<Song> queueTable;
    @FXML
    TableColumn<Song, String> queueTitle;
    @FXML
    TableColumn<Song, String> queueArtist;
    @FXML
    TableColumn<Song, String> queueLength;
    @FXML
    private HBox outerBox;
    Main application;
    private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");
    private RepeatState repeatState = RepeatState.NO_REPEAT;
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * <p>Constructor for QueueController.</p>
     */
    public QueueController() {
        super();
    }

    /**
     * <p>setApp.</p>
     *
     * @param application a {@link com.seb.musicapp.Main} object
     */
    public void setApp(Main application) {
        this.application = application;

        StreamerController.dragHandler(outerBox);
    }

    /**
     * <p>setItems.</p>
     */
    public void setItems() {
        while (queueTable == null) System.out.println("null");
        queueTable.getStylesheets().add(Main.class.getResource("dark.css").toExternalForm());
        queueTable.setItems(FXCollections.observableArrayList(queue));
        queueTitle.setCellValueFactory(new PropertyValueFactory<>("songName"));
        queueArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
        queueLength.setCellValueFactory(new PropertyValueFactory<>("duration"));
        queueTable.refresh();
        queueTable.setItems(FXCollections.observableArrayList(queue));
        queueTable.refresh();
        queueTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        queueTable.setOnKeyPressed(e -> {
            if (e.getCode().getCode() == KeyEvent.VK_DELETE || e.getCode().getCode() == KeyEvent.VK_BACK_SPACE) {
                JSONArray delete = new JSONArray();
                queueTable.getSelectionModel().getSelectedItems().forEach(o -> {
                    delete.put(queue.indexOf(o));
                    songList.remove(o);
                    queue.remove(o);
                });
                JSONObject deleteObj = new JSONObject();
                deleteObj.put("delete", delete);
                application.connector.out.println(deleteObj);
                queueTable.setItems(FXCollections.observableArrayList(queue));
                queueTable.refresh();
            }
        });
        queueTable.setRowFactory(tv -> {
            TableRow<Song> row = new TableRow<>();
            row.getStylesheets().add(Main.class.getResource("dark.css").toExternalForm());

            row.setOnDragDetected(event -> {
                if (!row.isEmpty()) {
                    Integer index = row.getIndex();
                    Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                    db.setDragView(row.snapshot(null, null));
                    ClipboardContent cc = new ClipboardContent();
                    cc.put(SERIALIZED_MIME_TYPE, index);
                    db.setContent(cc);
                    event.consume();
                }
            });

            row.setOnDragOver(event -> {

                final double scrollAreaHeight = 30;
                final double scrollSpeed = .02;

                Pane header = (Pane)queueTable.lookup("TableHeaderRow");
                double yTable = queueTable.localToScreen(queueTable.getBoundsInLocal()).getMinY();
                double yContentStart = yTable + header.getHeight();
                double yContentEnd = yTable + queueTable.getHeight();

                double y = event.getScreenY();
                boolean scrollUp = y < yContentStart + scrollAreaHeight;
                boolean scrollDown = y > yContentEnd - scrollAreaHeight;

                if (scrollUp || scrollDown) {
                    ScrollBar verticalScrollBar = (ScrollBar)queueTable.lookup(".scroll-bar:vertical");
                    double offset = scrollUp ? - scrollSpeed : scrollSpeed;
                    if (verticalScrollBar.getValue() + offset < verticalScrollBar.getMin()) verticalScrollBar.setValue(verticalScrollBar.getMin());
                    else
                        verticalScrollBar.setValue(Math.min(verticalScrollBar.getValue() + offset, verticalScrollBar.getMax()));
                }

                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    if (row.getIndex() != (Integer) db.getContent(SERIALIZED_MIME_TYPE)) {
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        event.consume();
                    }
                }
            });

            row.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    int draggedIndex = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
                    Song draggedPerson = queueTable.getItems().remove(draggedIndex);

                    int dropIndex;

                    if (row.isEmpty()) {
                        dropIndex = queueTable.getItems().size();
                    } else {
                        dropIndex = row.getIndex();
                    }

                    queueTable.getItems().add(dropIndex, draggedPerson);

                    event.setDropCompleted(true);
                    queueTable.getSelectionModel().select(dropIndex);
                    application.connector.out.println("move " + draggedIndex + " " + dropIndex);
                    songList.remove(draggedIndex);
                    queue.remove(draggedIndex);
                    songList.add(dropIndex, draggedPerson);
                    queue.add(dropIndex, draggedPerson);
                    event.consume();
                }
            });

            return row;
        });
    }

    /**
     * <p>updateTable.</p>
     *
     * @param data a {@link org.json.JSONObject} object
     */
    public void updateTable(JSONObject data) {
        if (data.has("clear")) {
            songList.clear();
            queue.clear();
            this.i = 0;
        }
        if (data.has("insert")) {
            JSONObject obj = data.getJSONObject("insert");
            for (String s : obj.keySet()) {
                JSONObject insObj = obj.getJSONObject(s);
                if (Integer.parseInt(s) >= 0) {
                    queue.add(Integer.parseInt(s), new Song(insObj.getString("title"), insObj.getString("author"), insObj.getString("duration"), insObj.getString("url")));
                    songList.add(Integer.parseInt(s), new Song(insObj.getString("title"), insObj.getString("author"), insObj.getString("duration"), insObj.getString("url")));
                }
                else {
                    queue.add(new Song(insObj.getString("title"), insObj.getString("author"), insObj.getString("duration"), insObj.getString("url")));
                    songList.add(new Song(insObj.getString("title"), insObj.getString("author"), insObj.getString("duration"), insObj.getString("url")));
                }
                insObj.clear();
            }
            obj.clear();
        }
        if (data.has("queue")) {
            JSONArray add = data.optJSONArray("queue");
            for (Object o : add) {
                queue.add(new Song(((JSONObject) o).getString("title"), ((JSONObject) o).getString("author"), ((JSONObject) o).getString("duration"), ((JSONObject) o).getString("url")));
                songList.add(new Song(((JSONObject) o).getString("title"), ((JSONObject) o).getString("author"), ((JSONObject) o).getString("duration"), ((JSONObject) o).getString("url")));
            }
            add.clear();
        }
        if (data.has("next")) {
            for (int i = 0; i < data.getInt("next"); i++) {
                if (repeatState == RepeatState.REPEAT_SINGLE) {
                    break;
                }
                this.i++;
                if (!queue.isEmpty()) {
                    String dur = queue.getFirst().getDuration();
                    int minutes = Integer.parseInt(dur.substring(0, dur.indexOf(":")));
                    int seconds = Integer.parseInt(dur.substring(dur.indexOf(":") + 1)) + (minutes * 60);
                    String songName = queue.getFirst().getSongName();
                    application.discordActivity.set(queue.getFirst().getSongName(), queue.getFirst().getArtist(), seconds, queue.getFirst().getUrl(), true);
                    Platform.runLater(() -> {
                        application.stage.setTitle(songName);
                        application.mainWindowController.getTitle().setText(songName);
                    });
                    queue.removeFirst();
                } else if (repeatState == RepeatState.NO_REPEAT) {
                    application.discordActivity.setIdlePresence();
                    Platform.runLater(() -> {
                        application.mainWindowController.getTitle().setText("Music Bot App");
                        application.stage.setTitle("Music Bot App");
                    });
                } else if (repeatState == RepeatState.REPEAT_QUEUE) {
                    queue = new ArrayList<>(songList);
                    i--;
                    this.i = 0;
                }
            }
        }
        queueTable.setItems(FXCollections.observableArrayList(queue));
        if (data.has("repeat")) {
            switch (data.getString("repeat")) {
                case "NO_REPEAT" -> repeatState = RepeatState.NO_REPEAT;
                case "REPEAT_QUEUE" -> repeatState = RepeatState.REPEAT_QUEUE;
                case "REPEAT_SINGLE" -> repeatState = RepeatState.REPEAT_SINGLE;
            }
            propertyChangeSupport.firePropertyChange("repeatState", null, repeatState);
        }
        if (data.has("volume")) {
            int vol = data.getInt("volume");
            Platform.runLater(() -> application.mainWindowController.setVolume(vol));
        }
        queueTable.refresh();
        data.clear();
    }

    public void clearQueue() {
        songList.clear();
        queue.clear();
        this.i = 0;
        application.discordActivity.setIdlePresence();
        Platform.runLater(() -> {
            application.mainWindowController.getTitle().setText("Music Bot App");
            application.stage.setTitle("Music Bot App");
        });
        queueTable.setItems(FXCollections.observableArrayList(queue));
        queueTable.refresh();

    }

    /**
     * <p>addPropertyChangeListener.</p>
     *
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    @FXML
    private void onQuitButtonClick() {
        application.queueStage.close();
    }

    @FXML
    private void onMinimizeButtonClick() {
        application.queueStage.setIconified(true);
    }

    @FXML
    private void onMaximizeButtonClick() {
        application.queueStage.setMaximized(!application.queueStage.isMaximized());
    }
}
