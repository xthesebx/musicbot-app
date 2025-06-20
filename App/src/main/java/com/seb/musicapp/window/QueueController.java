package com.seb.musicapp.window;

import com.seb.musicapp.Main;
import com.seb.musicapp.common.RepeatState;
import com.seb.musicapp.common.Song;
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
    public List<Song> songList = new ArrayList<>();
    public int i = 0;

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
    public List<TableView<Song>> tableViews = new ArrayList<>();
    List<TableColumn<Song, String>> tableTitles = new ArrayList<>();
    List<TableColumn<Song, String>> tableArtists = new ArrayList<>();
    List<TableColumn<Song, String>> tableLenghts = new ArrayList<>();

    /**
     * <p>Constructor for QueueController.</p>
     */
    public QueueController() {
        super();
    }

    /**
     * <p>setApp.</p>
     *
     * @param application a {@link Main} object
     */
    public void setApp(Main application) {
        this.application = application;
        tableViews.add(queueTable);
        tableViews.add(application.mainWindowController.queueTable);
        tableTitles.add(queueTitle);
        tableTitles.add(application.mainWindowController.queueTitle);
        tableArtists.add(queueArtist);
        tableArtists.add(application.mainWindowController.queueArtist);
        tableLenghts.add(queueLength);
        tableLenghts.add(application.mainWindowController.queueLength);
        Main.dragHandler(outerBox);
    }

    /**
     * <p>setItems.</p>
     */
    public void setItems() {
        for (int i = 0; i < tableViews.size(); i++) {
            TableView<Song> tableView = tableViews.get(i);
            TableColumn<Song, String> tableTitle = tableTitles.get(i);
            TableColumn<Song, String> tableArtist = tableArtists.get(i);
            TableColumn<Song, String> tableLength = tableLenghts.get(i);
            while (tableView == null) System.out.println("null");
            tableView.setItems(FXCollections.observableArrayList(queue));
            tableTitle.setCellValueFactory(new PropertyValueFactory<>("songName"));
            tableArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
            tableLength.setCellValueFactory(new PropertyValueFactory<>("duration"));
            tableView.refresh();
            tableView.setItems(FXCollections.observableArrayList(queue));
            tableView.refresh();
            tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            tableView.setOnKeyPressed(e -> {
                if (e.getCode().getCode() == KeyEvent.VK_DELETE || e.getCode().getCode() == KeyEvent.VK_BACK_SPACE) {
                    JSONArray delete = new JSONArray();
                    tableView.getSelectionModel().getSelectedItems().forEach(o -> {
                        delete.put(queue.indexOf(o));
                        songList.remove(o);
                        queue.remove(o);
                    });
                    JSONObject deleteObj = new JSONObject();
                    deleteObj.put("delete", delete);
                    application.connector.out.println(deleteObj);
                    tableView.setItems(FXCollections.observableArrayList(queue));
                    tableView.refresh();
                }
            });
            tableView.setRowFactory(tv -> {
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

                    final double scrollAreaHeight = 30;
                    final double scrollSpeed = .02;

                    Pane header = (Pane) tableView.lookup("TableHeaderRow");
                    double yTable = tableView.localToScreen(tableView.getBoundsInLocal()).getMinY();
                    double yContentStart = yTable + header.getHeight();
                    double yContentEnd = yTable + tableView.getHeight();

                    double y = event.getScreenY();
                    boolean scrollUp = y < yContentStart + scrollAreaHeight;
                    boolean scrollDown = y > yContentEnd - scrollAreaHeight;

                    if (scrollUp || scrollDown) {
                        ScrollBar verticalScrollBar = (ScrollBar) tableView.lookup(".scroll-bar:vertical");
                        double offset = scrollUp ? -scrollSpeed : scrollSpeed;
                        if (verticalScrollBar.getValue() + offset < verticalScrollBar.getMin())
                            verticalScrollBar.setValue(verticalScrollBar.getMin());
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
                        Song draggedPerson = tableView.getItems().remove(draggedIndex);

                        int dropIndex;

                        if (row.isEmpty()) {
                            dropIndex = tableView.getItems().size();
                        } else {
                            dropIndex = row.getIndex();
                        }

                        tableView.getItems().add(dropIndex, draggedPerson);

                        event.setDropCompleted(true);
                        tableView.getSelectionModel().select(dropIndex);
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
        Instant start = Instant.now();
        if (data.has("pos")) {
            JSONObject pos = data.getJSONObject("pos");
            long timestamp = pos.getLong("timestamp");
            long position = pos.getLong("position");
            start = Instant.ofEpochMilli(timestamp - position);
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
                    if (application.discordActivity != null)
                        application.discordActivity.set(queue.getFirst().getSongName(), queue.getFirst().getArtist(), start, seconds, queue.getFirst().getUrl(), true);
                    Platform.runLater(() -> {
                        application.stage.setTitle(songName);
                        application.mainWindowController.getTitle().setText(songName);
                    });
                    queue.removeFirst();
                } else if (repeatState == RepeatState.NO_REPEAT) {
                    if (application.discordActivity != null)
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
        for (TableView<Song> tableView : tableViews) {
            tableView.setItems(FXCollections.observableArrayList(queue));
            tableView.refresh();
        }
        data.clear();
    }

    public void clearQueue() {
        songList.clear();
        queue.clear();
        this.i = 0;
        if (application.discordActivity != null)
            application.discordActivity.setIdlePresence();
        Platform.runLater(() -> {
            application.mainWindowController.getTitle().setText("Music Bot App");
            application.stage.setTitle("Music Bot App");
        });
        for (TableView<Song> tableView : tableViews) {
            tableView.setItems(FXCollections.observableArrayList(queue));
            tableView.refresh();
        }

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

    @FXML
    public void onPinButtonClick() {
        application.mainWindowController.onQueue();
    }
}
