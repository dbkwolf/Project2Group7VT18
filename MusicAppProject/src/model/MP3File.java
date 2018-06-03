package model;

public class MP3File {

    private String url;
    private int duration; //in seconds

    public MP3File(String url, int duration){

        this.url = url;
        this.duration = duration;
    }



    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
