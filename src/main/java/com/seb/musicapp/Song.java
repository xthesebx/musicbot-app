package com.seb.musicapp;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Song {
    private StringProperty songName, artist, duration;

    public String getSongName() {
        return songName.get();
    }

    public StringProperty songNameProperty() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName.set(songName);
    }

    public String getArtist() {
        return artist.get();
    }

    public StringProperty artistProperty() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist.set(artist);
    }

    public String getDuration() {
        return duration.get();
    }

    public StringProperty durationProperty() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration.set(duration);
    }

    public Song(String songName, String artist, String duration) {
        this.songName = new SimpleStringProperty(this, "title", songName);
        this.artist = new SimpleStringProperty(this, "artist", artist);
        this.duration = new SimpleStringProperty(this, "duration", duration);
    }
}
