package model;

import javafx.beans.property.SimpleStringProperty;

public class SongLink {

    private SimpleStringProperty title;
    private SimpleStringProperty url;

    public SongLink(String songTitle, String songUrl){
        this.title = new SimpleStringProperty(songTitle);
        this.url = new SimpleStringProperty(songUrl);
    }


    public String getTitle() {
        return title.get();
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getUrl() {
        return url.get();
    }

    public SimpleStringProperty urlProperty() {
        return url;
    }

    public void setUrl(String url) {
        this.url.set(url);
    }
}
