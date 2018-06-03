package model;

public class Artist {



    private int artistId;
    private String artistName;

    public Artist(int id, String name){
        this.artistId = id;
        this.artistName = name;
    }

    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }


    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }
}
