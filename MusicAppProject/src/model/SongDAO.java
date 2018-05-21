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

            String query = "SELECT g7musicappdb.songs.song_id, g7musicappdb.songs.song_title, g7musicappdb.artists.artist_name, g7musicappdb.albums.album_title, g7musicappdb.songs.file_location" +
                    " FROM ((g7musicappdb.songs INNER JOIN g7musicappdb.artists ON g7musicappdb.songs.artist_id = g7musicappdb.artists.artist_id)" +
                    " INNER JOIN g7musicappdb.albums on g7musicappdb.songs.album_id = g7musicappdb.albums.album_id)WHERE Match(song_title) Against('" + title + "');";
            ResultSet rs = DatabaseUtility.dbExecuteQuery(query);

            while (rs.next()) {

                Song currentSong = new Song(rs.getInt("song_id"), rs.getString("song_title"), rs.getString("artist_name"), rs.getString("album_title"), rs.getString("file_location"));
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

            String query = "SELECT songs.song_id, songs.song_title, artists.artist_name, albums.album_title, songs.file_location\n" +
                    "FROM (((g7musicappdb.songs INNER JOIN g7musicappdb.artists ON songs.artist_id = artists.artist_id)\n" +
                    "INNER JOIN g7musicappdb.albums on songs.album_id = albums.album_id)\n" +
                    "INNER JOIN g7musicappdb.song_playlist_references on songs.song_id = song_playlist_references.song_id)\n" +
                    "INNER JOIN g7musicappdb.playlists on song_playlist_references.playlist_id = playlists.playlist_id\n" +
                    "WHERE playlists.playlist_id = " + playlistId + ";";
            ResultSet rs = DatabaseUtility.dbExecuteQuery(query);

            while (rs.next()) {

                Song currentSong = new Song(rs.getInt("song_id"), rs.getString("song_title"), rs.getString("artist_name"), rs.getString("album_title"), rs.getString("file_location"));

                songData.add(currentSong);


            }

        } catch (SQLException e) {
            System.out.print("Error occurred while DELETE Operation: " + e);
            throw e;
        }

        return songData;

    }



}