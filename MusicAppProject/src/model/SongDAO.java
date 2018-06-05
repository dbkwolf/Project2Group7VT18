package model;

import util.DatabaseUtility;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import java.sql.ResultSet;
import java.sql.SQLException;


public class SongDAO {

    private ObservableList<Song> data;
    private TableView table;


    public static ObservableList<Song> searchSong(String title) throws SQLException, ClassNotFoundException {

        ObservableList<Song> songData;

        try {
            songData = FXCollections.observableArrayList();

            String query = "SELECT g7musicappdb.songs.song_id, g7musicappdb.songs.song_title, g7musicappdb.artists.artist_name, g7musicappdb.albums.album_title, g7musicappdb.songs.file_location, g7musicappdb.songs.duration" +
                    " FROM ((g7musicappdb.songs INNER JOIN g7musicappdb.artists ON g7musicappdb.songs.artist_id = g7musicappdb.artists.artist_id)" +
                    " INNER JOIN g7musicappdb.albums on g7musicappdb.songs.album_id = g7musicappdb.albums.album_id)WHERE Match(song_title) Against('" + title + "');";
            ResultSet rs = DatabaseUtility.dbExecuteQuery(query);

            while (rs.next()) {

                Song currentSong = new Song(rs.getInt("song_id"), rs.getString("song_title"), rs.getString("artist_name"), rs.getString("album_title"), rs.getString("file_location"), rs.getInt("duration"));
                songData.add(currentSong);

            }

        } catch (SQLException e) {
            System.out.print("Error occurred while DELETE Operation: " + e);
            throw e;
        }

        return songData;

    }

    public static ObservableList<Song> buildSongDataFromPlaylist(int playlistId) throws SQLException, ClassNotFoundException {

        ObservableList<Song> songData;

        try {
            songData = FXCollections.observableArrayList();

            String query = "SELECT song_playlist_references.ref_id, songs.song_id, songs.song_title, artists.artist_name, albums.album_title, songs.file_location, songs.duration\n" +
                    "FROM (((g7musicappdb.songs INNER JOIN g7musicappdb.artists ON songs.artist_id = artists.artist_id)\n" +
                    "INNER JOIN g7musicappdb.albums on songs.album_id = albums.album_id)\n" +
                    "INNER JOIN g7musicappdb.song_playlist_references on songs.song_id = song_playlist_references.song_id)\n" +
                    "INNER JOIN g7musicappdb.playlists on song_playlist_references.playlist_id = playlists.playlist_id\n" +
                    "WHERE playlists.playlist_id = " + playlistId + ";";
            ResultSet rs = DatabaseUtility.dbExecuteQuery(query);

            while (rs.next()) {

                Song currentSong = new Song(rs.getInt("song_id"), rs.getString("song_title"), rs.getString("artist_name"), rs.getString("album_title"), rs.getString("file_location"), rs.getInt("duration"));
                        currentSong.setRefId(rs.getInt("ref_id"));
                songData.add(currentSong);


            }

        } catch (SQLException e) {
            System.out.print("Error occurred while DELETE Operation: " + e);
            throw e;
        }

        return songData;

    }

    public static Song insertSonginPlaylist(int songId, int playlistId, Song song) throws SQLException {
        //Declare a INSERT statement


        String insertQuery = "INSERT INTO g7musicappdb.song_playlist_references ( song_id, playlist_id) VALUES (" + songId + ", " + playlistId + "); ";

        //Execute INSERT operation
        try {
            DatabaseUtility.dbExecuteUpdate(insertQuery);

            ResultSet rs = DatabaseUtility.dbExecuteQuery("SELECT LAST_INSERT_ID();");

            while(rs.next()){
                song.setRefId(rs.getInt("LAST_INSERT_ID()"));


            }

        }
        catch (SQLException e) {
            System.out.print("Error occurred while INSERT Operation: " + e);
            throw e;
        }

        return song;
    }

    public static void deleteSongfromPlaylist(int ref_id) throws SQLException{

        String qr = "DELETE FROM g7musicappdb.song_playlist_references WHERE ref_id = " + ref_id;

        //Execute UPDATE operation
        try {
            DatabaseUtility.dbExecuteUpdate(qr);
        }
        catch (SQLException e) {
            System.out.print("Error occurred while UPDATE Operation: " + e);
            throw e;
        }

    }

    public static Artist searchArtist(String artistName) throws SQLException{

        Artist artist = null;

        String qr = "SELECT * from g7musicappdb.artists where artist_name like '" + artistName + "';";


        try {
            ResultSet rs = DatabaseUtility.dbExecuteQuery(qr);
            while(rs.next()) {
                artist = new Artist(rs.getInt("artist_id"), rs.getString("artist_name"));
            }
        }
        catch (SQLException e) {
            System.out.print("Error occurred while searching for artist: " + e);
            throw e;
        }
         return artist;
    }

    public static Album searchAlbum(String albumTitle, int artist_id) throws SQLException{
        Album album = null;

        String qr = "SELECT * from g7musicappdb.albums where album_title like '" + albumTitle + "' AND artist_id =" + artist_id +";";


        try {
            ResultSet rs = DatabaseUtility.dbExecuteQuery(qr);
            while(rs.next()) {
                album = new Album(rs.getInt("album_id"), rs.getString("album_title"), artist_id);
            }
        }
        catch (SQLException e) {
            System.out.print("Error occurred while searching for artist: " + e);
            throw e;
        }
        return album;
    }

    public static void addSongToDB(String title, int artist_id, int album_id, String location, int duration)throws SQLException{
        String qr = "INSERT INTO g7musicappdb.songs (song_title, artist_id, album_id, file_location, duration) VALUES ('"+title+"', "+artist_id+", "+album_id+", '"+location+"', "+duration+");";

        //Execute UPDATE operation
        try {
            DatabaseUtility.dbExecuteUpdate(qr);
        }
        catch (SQLException e) {
            System.out.print("Error occurred while UPDATE Operation: " + e);
            throw e;
        }
    }

    public static void addSongToDB(String title, int artist_id, String albumTitle, String location, int duration)throws SQLException{

        String qr1 = "INSERT INTO g7musicappdb.albums (album_title, artist_id) VALUES ('"+albumTitle+"', "+artist_id+");";


        //Execute UPDATE operation
        try {

            DatabaseUtility.dbExecuteUpdate(qr1);

            ResultSet rs = DatabaseUtility.dbExecuteQuery("SELECT LAST_INSERT_ID();");
            int albumId = 0;
            while(rs.next()) {
                albumId = rs.getInt("LAST_INSERT_ID()");
            }


            addSongToDB(title, artist_id, albumId, location, duration);



        }
        catch (SQLException e) {
            System.out.print("Error occurred while UPDATE Operation: " + e);
            throw e;
        }
    }

    public static void addSongToDB(String title, String artistName, String albumTitle, String location, int duration) throws SQLException{

        String qr1 = "INSERT INTO g7musicappdb.artists (artist_name) VALUES ('"+artistName+"')";


        //Execute UPDATE operation
        try {

            DatabaseUtility.dbExecuteUpdate(qr1);

            ResultSet rs = DatabaseUtility.dbExecuteQuery("SELECT LAST_INSERT_ID();");
            int artistId = 0;
            while(rs.next()) {
                artistId = rs.getInt("LAST_INSERT_ID()");
            }

            addSongToDB(title, artistId, albumTitle, location, duration);




        }
        catch (SQLException e) {
            System.out.print("Error occurred while UPDATE Operation: " + e);
            throw e;
        }

    }
}