package model;

import util.DatabaseUtility;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlaylistDAO {

    public static void insertPlaylist (String plTitle, String owner) throws SQLException, ClassNotFoundException {

    //declare insert op
        String insertQuery ="INSERT INTO g7musicappdb.playlists (playlist_id, pl_title, owner_id) VALUES ('0','" + plTitle+ "', '"+owner+"'); ";

        //Execute INSERT operation
        try {
            DatabaseUtility.runQuery(insertQuery);
        } catch (SQLException e) {
            System.out.print("Error occurred while DELETE Operation: " + e);
            throw e;
        }
    }

    public static ObservableList<Playlist> buildPlaylistData(String owner) throws SQLException, ClassNotFoundException {

        ObservableList<Playlist> plData;

        try {
            plData = FXCollections.observableArrayList();

            String query = "SELECT g7musicappdb.playlists.playlist_id, g7musicappdb.playlists.pl_title, g7musicappdb.playlists.owner_id"+
            " FROM (g7musicappdb.playlists INNER JOIN g7musicappdb.users ON g7musicappdb.playlists.owner_id = g7musicappdb.users.user_id)" +
                    " WHERE g7musicappdb.playlists.owner_id like '" + owner + "';";
            ResultSet rs = DatabaseUtility.dbExecuteQuery(query);


            while (rs.next()) {

                Playlist crntPlaylist = new Playlist(rs.getInt("playlist_id"), rs.getString("pl_title"), rs.getInt("owner_id"));
                plData.add(crntPlaylist);

            }
        }
        catch (SQLException e) {
            System.out.print("Error occurred while DELETE Operation: " + e);
            throw e;
        }

        return plData;
    }

    public static void insertSonginPlaylist (String songId, String playlistId) throws SQLException, ClassNotFoundException {
        //Declare a INSERT statement


        String insertQuery ="INSERT INTO g7musicappdb.song_playlist_references (ref_id, song_id, playlist_id) VALUES ('0','" + songId + "', '"+playlistId+"); ";

        //Execute INSERT operation
        try {
            DatabaseUtility.runQuery(insertQuery);
        } catch (SQLException e) {
            System.out.print("Error occurred while INSERT Operation: " + e);
            throw e;
        }
    }



}
