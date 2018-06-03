package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import util.DatabaseUtility;

import java.sql.ResultSet;
import java.sql.SQLException;



public class PlaylistDAO {

    public static Playlist newInsertedPlaylist(String plTitle, int owner) throws SQLException {

        //declare insert op
        String insertQuery = "INSERT INTO g7musicappdb.playlists (pl_title, owner_id) VALUES ('" + plTitle + "', " + owner + "); ";
        Playlist newPlaylist = null;
        //Execute INSERT operation and retrieve newly inserted playlist's ID
        try {
            DatabaseUtility.dbExecuteUpdate(insertQuery);

            ResultSet rs = DatabaseUtility.dbExecuteQuery("SELECT LAST_INSERT_ID();");
            while(rs.next()) {
                newPlaylist = new Playlist(rs.getInt("LAST_INSERT_ID()"), plTitle, owner);
            }

        }
        catch (SQLException e) {
            System.out.print("Error occurred while INSERT Operation: " + e);
            throw e;
        }

        return newPlaylist;

    }

    public static ObservableList<Playlist> buildPlaylistData(int owner_id) throws SQLException {

        ObservableList<Playlist> plData;

        try {
            plData = FXCollections.observableArrayList();

            String query = "SELECT g7musicappdb.playlists.playlist_id, g7musicappdb.playlists.pl_title, g7musicappdb.playlists.owner_id" +
                    " FROM (g7musicappdb.playlists INNER JOIN g7musicappdb.users ON g7musicappdb.playlists.owner_id = g7musicappdb.users.user_id)" +
                    " WHERE g7musicappdb.playlists.owner_id = " + owner_id + ";";
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



    public static void deletePlaylist(int playlistId) throws SQLException {
        //Declare a DELETE statement
        String updateStmt = "DELETE FROM g7musicappdb.playlists WHERE playlist_id = " + playlistId + ";";

        //Execute DELETE operation
        try {
            DatabaseUtility.dbExecuteUpdate(updateStmt);
        }
        catch (SQLException e) {
            System.out.print("Error occurred while DELETE Operation: " + e);
            throw e;
        }
    }






}
