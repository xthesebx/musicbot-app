module com.seb.musicapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.logging;
    requires org.json;
    requires jkeymaster;
    requires discord.game.sdk;
    requires Logger;


    opens com.seb.musicapp to javafx.fxml;
    exports com.seb.musicapp;
    exports Discord;
    opens Discord to javafx.fxml;
    exports Discord.OS;
    opens Discord.OS to javafx.fxml;
}