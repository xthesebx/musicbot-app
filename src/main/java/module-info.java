module com.seb.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.logging;
    requires org.json;
    requires jkeymaster;


    opens com.seb.musicapp to javafx.fxml;
    exports com.seb.musicapp;
}