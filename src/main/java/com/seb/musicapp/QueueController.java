package com.seb.musicapp;

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
import java.util.ArrayList;
import java.util.List;


public class QueueController {
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

    public QueueController() {
        super();
    }

    public void setApp(Main application) {
        this.application = application;
    }

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
                if (! row.isEmpty()) {
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
                    if (row.getIndex() != ((Integer)db.getContent(SERIALIZED_MIME_TYPE)).intValue()) {
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

                    int dropIndex ;

                    if (row.isEmpty()) {
                        dropIndex = queueTable.getItems().size() ;
                    } else {
                        dropIndex = row.getIndex();
                    }

                    queueTable.getItems().add(dropIndex, draggedPerson);

                    event.setDropCompleted(true);
                    queueTable.getSelectionModel().select(dropIndex);
                    application.connector.out.println("move " + draggedIndex + " " + dropIndex);
                    event.consume();
                }
            });

            return row ;
        });
    }

    public void updateTable(JSONObject data) {
        if (data.has("clear")) {
            queue.clear();
            queueTable.setItems(FXCollections.observableArrayList(queue));
        }
        if (data.has("insert")) {
            JSONObject obj = data.getJSONObject("insert");
            for (String s : obj.keySet()) {
                JSONObject insObj = obj.getJSONObject(s);
                queue.add(Integer.parseInt(s), new Song(insObj.getString("title"), insObj.getString("author"), insObj.getString("duration")));
                queueTable.setItems(FXCollections.observableArrayList(queue));
                insObj.clear();
            }
            obj.clear();
        }
        if (data.has("queue")) {
            JSONArray add = data.optJSONArray("queue");
            for (Object o : add) {
                queue.add(new Song(((JSONObject) o).getString("title"), ((JSONObject) o).getString("author"), ((JSONObject) o).getString("duration")));
                queueTable.setItems(FXCollections.observableArrayList(queue));
            }
            add.clear();
        }
        if (data.has("next")) {
            System.out.println(data);
            for (int i = 0; i < data.getInt("next"); i++) {
                queue.remove(0);
            }
            queueTable.setItems(FXCollections.observableArrayList(queue));
        }
        queueTable.refresh();
        data.clear();
    }
}