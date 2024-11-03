package com.seb.musicapp;

import com.hawolt.logger.Logger;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.Instant;
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
    @FXML
    TableView<Song> queueTable;
    @FXML
    TableColumn<Song, String> queueTitle;
    @FXML
    TableColumn<Song, String> queueArtist;
    @FXML
    TableColumn<Song, String> queueLength;
    Main application;
    private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");
    private RepeatState repeatState = RepeatState.NO_REPEAT;
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

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
    }

    /**
     * <p>setItems.</p>
     */
    public void setItems() {
        while (queueTable == null) System.out.println("null");
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
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    if (row.getIndex() != ((Integer) db.getContent(SERIALIZED_MIME_TYPE)).intValue()) {
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
                    queue.remove(draggedIndex);
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
            queue.clear();
        }
        if (data.has("insert")) {
            JSONObject obj = data.getJSONObject("insert");
            for (String s : obj.keySet()) {
                JSONObject insObj = obj.getJSONObject(s);
                if (Integer.parseInt(s) >= 0)
                    queue.add(Integer.parseInt(s), new Song(insObj.getString("title"), insObj.getString("author"), insObj.getString("duration"), insObj.getString("url")));
                else queue.add(new Song(insObj.getString("title"), insObj.getString("author"), insObj.getString("duration"), insObj.getString("url")));
                insObj.clear();
            }
            obj.clear();
        }
        if (data.has("queue")) {
            JSONArray add = data.optJSONArray("queue");
            for (Object o : add) {
                queue.add(new Song(((JSONObject) o).getString("title"), ((JSONObject) o).getString("author"), ((JSONObject) o).getString("duration"), ((JSONObject) o).getString("url")));
            }
            add.clear();
        }
        if (data.has("next")) {
            if (!queue.isEmpty()) {
                for (int i = 0; i < data.getInt("next"); i++) {
                    String dur = queue.get(0).getDuration();
                    int minutes = Integer.parseInt(dur.substring(0, dur.indexOf(":")));
                    int seconds = Integer.parseInt(dur.substring(dur.indexOf(":") + 1)) + (minutes * 60);
                    application.discordActivity.set(queue.get(0).getSongName(), queue.get(0).getArtist(), seconds, queue.get(0).getUrl(), true);
                    queue.remove(0);
                }
            } else application.discordActivity.setIdlePresence();
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
            Platform.runLater(() -> {
                application.mainWindowController.setVolume(vol);
            });
        }
        queueTable.refresh();
        data.clear();
    }

    /**
     * <p>addPropertyChangeListener.</p>
     *
     * @param listener a {@link java.beans.PropertyChangeListener} object
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
}
