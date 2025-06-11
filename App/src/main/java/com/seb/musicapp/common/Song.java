package com.seb.musicapp.common;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * <p>Song class.</p>
 *
 * @author xXTheSebXx
 * @version 1.0-SNAPSHOT
 */
public class Song {
    private StringProperty songName, artist, duration, url;

    /**
     * <p>Getter for the field <code>songName</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getSongName() {
        return songName.get();
    }

    /**
     * <p>songNameProperty.</p>
     *
     * @return a {@link javafx.beans.property.StringProperty} object
     */
    public StringProperty songNameProperty() {
        return songName;
    }

    /**
     * <p>Setter for the field <code>songName</code>.</p>
     *
     * @param songName a {@link java.lang.String} object
     */
    public void setSongName(String songName) {
        this.songName.set(songName);
    }

    /**
     * <p>Getter for the field <code>artist</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getArtist() {
        return artist.get();
    }

    /**
     * <p>artistProperty.</p>
     *
     * @return a {@link javafx.beans.property.StringProperty} object
     */
    public StringProperty artistProperty() {
        return artist;
    }

    /**
     * <p>Setter for the field <code>artist</code>.</p>
     *
     * @param artist a {@link java.lang.String} object
     */
    public void setArtist(String artist) {
        this.artist.set(artist);
    }

    /**
     * <p>Getter for the field <code>duration</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getDuration() {
        return duration.get();
    }

    /**
     * <p>durationProperty.</p>
     *
     * @return a {@link javafx.beans.property.StringProperty} object
     */
    public StringProperty durationProperty() {
        return duration;
    }

    /**
     * <p>Setter for the field <code>duration</code>.</p>
     *
     * @param duration a {@link java.lang.String} object
     */
    public void setDuration(String duration) {
        this.duration.set(duration);
    }

    /**
     * <p>Getter for the field <code>url</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getUrl() {
        return url.get();
    }

    /**
     * <p>urlProperty.</p>
     *
     * @return a {@link javafx.beans.property.StringProperty} object
     */
    public StringProperty urlProperty() {
        return url;
    }

    /**
     * <p>Setter for the field <code>url</code>.</p>
     *
     * @param url a {@link java.lang.String} object
     */
    public void setUrl(String url) {
        this.url.set(url);
    }

    /**
     * <p>Constructor for Song.</p>
     *
     * @param songName a {@link java.lang.String} object
     * @param artist a {@link java.lang.String} object
     * @param duration a {@link java.lang.String} object
     * @param url a {@link java.lang.String} object
     */
    public Song(String songName, String artist, String duration, String url) {
        this.songName = new SimpleStringProperty(this, "title", songName);
        this.artist = new SimpleStringProperty(this, "artist", artist);
        this.duration = new SimpleStringProperty(this, "duration", duration);
        this.url = new SimpleStringProperty(this, "url", url);
    }
}
