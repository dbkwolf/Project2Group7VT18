package model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private SimpleIntegerProperty playlistId;
    private SimpleStringProperty plTitle;
    private SimpleIntegerProperty plOwner;
    private ObservableList<Song> songsInPlaylist;

    public Playlist(int id, String title, int owner) {
        this.playlistId = new SimpleIntegerProperty(id);
        this.plTitle = new SimpleStringProperty(title);
        this.plOwner = new SimpleIntegerProperty(owner);
        this.songsInPlaylist = FXCollections.observableArrayList();
    }

    public int getPlaylistId() {
        return playlistId.get();
    }

    public SimpleIntegerProperty playlistIdProperty() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId.set(playlistId);
    }

    public String getPlTitle() {
        return plTitle.get();
    }

    public SimpleStringProperty plTitleProperty() {
        return plTitle;
    }

    public void setPlTitle(String plTitle) {
        this.plTitle.set(plTitle);
    }

    public int getPlOwner() {
        return plOwner.get();
    }

    public SimpleIntegerProperty plOwnerProperty() {
        return plOwner;
    }

    public void setPlOwner(int plOwner) {
        this.plOwner.set(plOwner);
    }

    public ObservableList<Song> getSongsInPlaylist() {
        return songsInPlaylist;
    }

    public void setSongsInPlaylist(ObservableList<Song> songsInPlaylist) {
        this.songsInPlaylist = songsInPlaylist;
    }
}