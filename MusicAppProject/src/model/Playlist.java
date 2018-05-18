package model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Playlist {
    private SimpleIntegerProperty playlistId;
    private SimpleStringProperty plTitle;
    private SimpleIntegerProperty plOwner;

    public Playlist(int id, String title, int owner) {
        this.playlistId = new SimpleIntegerProperty(id);
        this.plTitle = new SimpleStringProperty(title);
        this.plOwner = new SimpleIntegerProperty(owner);
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
}

