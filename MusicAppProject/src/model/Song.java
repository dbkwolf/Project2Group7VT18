package model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Song {

    private SimpleIntegerProperty songId;
    private SimpleStringProperty songTitle;
    private SimpleStringProperty songArtist;
    private SimpleStringProperty songAlbum;
    private SimpleStringProperty songLocation;
    private SimpleIntegerProperty songDuration;
    private SimpleStringProperty StrSongDuration;
    private int refId;


    public Song(int id, String title, String artist, String album, String location, int duration) {

        this.songId = new SimpleIntegerProperty(id);
        this.songTitle = new SimpleStringProperty(title);
        this.songArtist = new SimpleStringProperty(artist);
        this.songAlbum = new SimpleStringProperty(album);
        this.songLocation = new SimpleStringProperty("http://project2.duckdns.org:1234/files/" + location + ".mp3");
        this.songDuration = new SimpleIntegerProperty(duration);
        this.StrSongDuration = new SimpleStringProperty(secondsToString(duration));
    }


    public StringProperty titleProperty() {
        return songTitle;
    }

    public StringProperty artistProperty() {
        return songArtist;
    }

    public StringProperty albumProperty() {
        return songAlbum;
    }


    public String getSongTitle() {
        return songTitle.get();
    }

    public void setSongTitle(String songTitle) {
        this.songTitle.set(songTitle);
    }


    public String getAlbum() {
        return songAlbum.get();
    }

    public void setSongAlbum(String album) {
        this.songAlbum.set(album);
    }

    public String getSongArtist() {
        return songArtist.get();
    }

    public String songArtistProperty() {
        return songArtist.get();
    }

    public void setSongArtist(String songArtist) {
        this.songArtist.set(songArtist);
    }

    public int getSongId() {
        return songId.get();
    }

    public int songIdProperty() {
        return songId.get();
    }

    public void setSongId(int songId) {
        this.songId.set(songId);
    }


    public int getRefId() {
        return refId;
    }

    public void setRefId(int refId) {
        this.refId = refId;
    }

    public int getSongDuration() {
        return songDuration.get();
    }

    public SimpleIntegerProperty songDurationProperty() {

        return songDuration;
    }

    public void setSongDuration(int songDuration) {
        this.songDuration.set(songDuration);
    }

    public String getSongLocation() {
        return songLocation.get();
    }

    public SimpleStringProperty songLocationProperty() {
        return songLocation;
    }

    public void setSongLocation(String songLocation) {
        this.songLocation.set(songLocation);
    }

    private String secondsToString(int duration) {
        return String.format("%02d:%02d", duration / 60, duration % 60);
    }


    public String getStrSongDuration() {
        return StrSongDuration.get();
    }

    public SimpleStringProperty strSongDurationProperty() {
        return StrSongDuration;
    }

    public void setStrSongDuration(String strSongDuration) {
        this.StrSongDuration.set(strSongDuration);
    }
}
