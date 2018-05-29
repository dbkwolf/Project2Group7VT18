package controllers;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import model.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import util.HttpGet;

import java.sql.SQLException;
import java.sql.SQLOutput;
import java.util.*;

import static controllers.LogInController.activeUser;
import static model.UserDAO.dbutil;


public class HomeController extends MainController{

    public static Playlist selectedPlaylist;
    public  Label lbl_user;
    public Song selectedSong;
    public Media track;
    public MediaPlayer player;
    public JFXButton btn_play;
    public JFXButton btn_pause;
    public Label lbl_currentTrack;
    public TableView<Playlist> tbl_userPlaylists;
    public TableColumn<Playlist, String> col_userPlaylistTitle;
    public TableView<Song> tbl_playlistTracks;
    public TableColumn<Song,String> col_title;
    public TableColumn<Song,String> col_artist;
    public TableColumn<Song,String> col_album;
    public JFXTextField txt_search;
    public JFXTextField txt_test;
    public  TableView<Song> tbl_searchResults;

    public TableColumn<Song,String> col_searchTitle;
    public TableColumn<Song,String> col_searchArtist;
    public TableColumn<Song,String> col_searchAlbum;
    public Label lbl_playlistName;
    public JFXButton btn_manageDB;
    public AnchorPane apn_middleHomeAnchorpane;
    public Label lbl_showUrl;
    private String url;

    private List<Integer> removedPlaylists = new ArrayList<>();
    private ContextMenu cm = new ContextMenu();
    private MenuItem mi_delete = new MenuItem("Delete");

    @FXML
    void initialize() throws SQLException, ClassNotFoundException{

        String fullName = activeUser.getFirstName() + " " + activeUser.getLastName();

        lbl_user.setText(fullName);
        lbl_currentTrack.setText("");
        load_all();

        update_tbl_userPlaylists();
        cm.getItems().add(mi_delete);
        adminVisibility(activeUser.isAdminLevel());

    }


    public void press_btn_search(javafx.event.ActionEvent event)throws SQLException, ClassNotFoundException{

        ObservableList<Song> songsAvailable = SongDAO.searchSong(txt_search.getText());
        col_searchTitle.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        col_searchArtist.setCellValueFactory(cellData -> cellData.getValue().artistProperty());
        col_searchAlbum.setCellValueFactory(cellData -> cellData.getValue().albumProperty());
        tbl_searchResults.setItems(songsAvailable);



    }


    public void press_btn_play(javafx.event.ActionEvent event)throws Exception{
        btn_play.setVisible(false);
        btn_pause.setVisible(true);
        try{
            selectedSong = tbl_searchResults.getSelectionModel().getSelectedItem();
            String selectedSongInfo = selectedSong.getSongTitle() + " - " + selectedSong.getSongArtist() + " - " + selectedSong.getAlbum();
            lbl_currentTrack.setText(selectedSongInfo);
            System.out.println(selectedSong.getSongLocation());
            track = new Media(selectedSong.getSongLocation());
            player = new MediaPlayer(track);
            player.play();
        }
        catch (Exception e){
            System.out.println("no media found");
        }

    }

    public void press_btn_pause(javafx.event.ActionEvent event)throws Exception{

        btn_pause.setVisible(false);
        btn_play.setVisible(true);
        try{
            player.pause();
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("no media was playing");
        }
    }


    public void press_btn_newPlaylist (javafx.event.ActionEvent event)throws Exception {


        String plTitle = namePlaylistPrompt();

        if (!plTitle.equals("empty")) {

            System.out.println("before: " + activeUser.getUserPlaylists().size());
            activeUser.getUserPlaylists().add(new Playlist(0, plTitle, activeUser.getUserId()));


            update_tbl_userPlaylists();


            System.out.println("after. "+activeUser.getUserPlaylists().size());

            try {
                UserDAO.updateUser("INSERT INTO g7musicappdb.playlists (playlist_id, pl_title, owner_id) VALUES (0, '"+ plTitle + "', " + activeUser.getUserId()+");");
            }
            catch (SQLException e) {
                e.printStackTrace();
            }

            activeUser.getUserPlaylists().get(activeUser.getUserPlaylists().size()-1).setPlaylistId(PlaylistDAO.getPlaylistIdFromDB(plTitle,Integer.toString(activeUser.getUserId())));

         }



    }

    public String namePlaylistPrompt(){
        String playlistName = "empty";

        TextInputDialog dialog = new TextInputDialog(activeUser.getFirstName()+"Playlist1"); // no apostrophes since it fs up SQL
        dialog.setTitle("Create Playlist");
        dialog.setHeaderText("");
        dialog.setContentText("Name:");

        Optional<String> result = dialog.showAndWait();

        //result.ifPresent(name -> {  this.label.setText(name); });
        if (result.isPresent()) {
            playlistName = result.get();
        }
        return playlistName;
    }

    public void update_tbl_userPlaylists() {

        col_userPlaylistTitle.setCellValueFactory(cellData -> cellData.getValue().plTitleProperty());
        tbl_userPlaylists.setItems(activeUser.getUserPlaylists());

    }

    public void click_tbl_userPlaylists(MouseEvent event) {


            if (event.getButton() == MouseButton.PRIMARY  && event.getClickCount() == 2) {

                update_tbl_playlistTracks();

            }else if(event.getButton() == MouseButton.SECONDARY) {


                cm.show(tbl_userPlaylists, event.getScreenX(), event.getScreenY());

                mi_delete.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {

                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

                        alert.setContentText("Are you sure you want to delete this playlist?");

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.get() == ButtonType.OK) {

                            try {
                                 delete_Playlist();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {

                            alert.close();

                        }
                    }
                });


            }
    }
    public void click_tbl_playlistTracks(MouseEvent event) {

        if (event.getButton() == MouseButton.PRIMARY  && event.getClickCount() == 2) {

            update_tbl_playlistTracks();

        }else if(event.getButton() == MouseButton.SECONDARY) {


            cm.show(tbl_userPlaylists, event.getScreenX(), event.getScreenY());

            mi_delete.setOnAction(event1 -> delete_songFromPlaylist());


        }

    }

    private void delete_songFromPlaylist() {

        int plPosition = findPlaylistListPosition(lbl_playlistName.getText());
        selectedSong = tbl_playlistTracks.getSelectionModel().getSelectedItem();
        activeUser.getUserPlaylists().get(plPosition).getSongsInPlaylist().remove(selectedSong);



        if(selectedPlaylist.getPlaylistId()!=0){

            StringBuilder qry = new StringBuilder();

            qry.append("DELETE FROM g7musicappdb.song_playlist_references WHERE ref_id = ").append(selectedSong.getRefId());
            System.out.println(qry.toString());
            try {
                UserDAO.updateUser(qry.toString());
            }
            catch (SQLException e) {
                e.printStackTrace();
            }

        }

    }

    public void update_tbl_playlistTracks() {


        selectedPlaylist = tbl_userPlaylists.getSelectionModel().getSelectedItem();
        if(!selectedPlaylist.getPlTitle().isEmpty()) {
            lbl_playlistName.setText(selectedPlaylist.getPlTitle());

            col_title.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
            col_artist.setCellValueFactory(cellData -> cellData.getValue().artistProperty());
            col_album.setCellValueFactory(cellData -> cellData.getValue().albumProperty());
            tbl_playlistTracks.setItems(selectedPlaylist.getSongsInPlaylist());
        }

    }

    public void press_btn_addSong() throws Exception{

        selectedPlaylist = tbl_userPlaylists.getSelectionModel().getSelectedItem();
        selectedSong = tbl_searchResults.getSelectionModel().getSelectedItem();
        int plPosition = findPlaylistListPosition(selectedPlaylist.getPlTitle());

        activeUser.getUserPlaylists().get(plPosition).getSongsInPlaylist().add(selectedSong);
        update_tbl_userPlaylists();
        update_tbl_playlistTracks();




            StringBuilder qry = new StringBuilder();

            qry.append("INSERT INTO g7musicappdb.song_playlist_references (ref_id, song_id, playlist_id) VALUES (0,").append(selectedSong.getSongId()).append(", ").append( selectedPlaylist.getPlaylistId()).append( "); ");

            try {
                UserDAO.updateUser(qry.toString());
            }
            catch (SQLException e) {
                e.printStackTrace();
            }


        activeUser.getUserPlaylists().get(plPosition).setSongsInPlaylist(SongDAO.buildSongDataFromPlaylist( activeUser.getUserPlaylists().get(plPosition).getPlaylistId()));

        update_tbl_userPlaylists();
        update_tbl_playlistTracks();
    }

    public int findPlaylistListPosition(String playlistTitle){


        int  i= 0;
       while(!activeUser.getUserPlaylists().get(i).getPlTitle().equals(playlistTitle)){
           System.out.println(i);
           i++;
       }

        return i;
    }

    public void delete_Playlist() throws SQLException, ClassNotFoundException {


        selectedPlaylist = tbl_userPlaylists.getSelectionModel().getSelectedItem();


        if (selectedPlaylist.getPlaylistId() != 0) {
            removedPlaylists.add(selectedPlaylist.getPlaylistId());
        }

        activeUser.getUserPlaylists().remove(selectedPlaylist);


        System.out.println(selectedPlaylist.getPlaylistId() + " " + selectedPlaylist.getPlTitle() + " gets deleted");


        update_tbl_playlistTracks();
        update_tbl_userPlaylists();



        try {
            UserDAO.updateUser("DELETE FROM g7musicappdb.playlists WHERE playlist_id =" + selectedPlaylist.getPlaylistId());
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        update_tbl_userPlaylists();

    }

    public void press_btn_profile_settings(javafx.event.ActionEvent event) throws Exception {




    // Create the custom dialog.
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Settings");
            dialog.setHeaderText("User Profile Settings");


    // Set the icon (must be included in the project).

    // Set the button types.
            ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

    // Create the username and password labels and fields.
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));
            //grid.setStyle("-fx-background-color: greenyellow;");

            TextField username = new TextField();
            username.setPromptText("Username");
            PasswordField password = new PasswordField();
            password.setPromptText("Password");
            TextField email = new TextField();
            email.setPromptText("Email");

            grid.add(new Label("Username:"), 0, 0);
            grid.add(username, 1, 0);
            grid.add(new Label("Password:"), 0, 1);
            grid.add(password, 1, 1);
            grid.add(new Label("Email:"), 0, 2);
            grid.add(email, 1, 2);

    // Enable/Disable login button depending on whether a username was entered.
            Node updateButton = dialog.getDialogPane().lookupButton(updateButtonType);
            updateButton.setDisable(false);

    // Do some validation.
            username.textProperty().addListener((observable, oldValue, newValue) -> {
                updateButton.setDisable(newValue.trim().isEmpty());
            });

            dialog.getDialogPane().setContent(grid);

            dialog.showAndWait();



    }

    public void testHttpGet() throws Exception{



        //url = HttpGet.getDownload(txt_test.getText());
        //lbl_showUrl.setText(url);

        update_all_user_changes();


    }

    public void press_btn_ytSearch(ActionEvent event) throws Exception {
        change_Scene_to(event,"../scenes/youtube-search.fxml");
    }

    public void loadSongsToUserPlaylistsLocal(ObservableList<Playlist> userPlaylists){



        userPlaylists.forEach((p) -> {
            try {
                if(p!=null){
                    p.setSongsInPlaylist(SongDAO.buildSongDataFromPlaylist(p.getPlaylistId()));}
                else{
                    System.out.println("no songs in " + p.getPlTitle());
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });




    }

    public void adminVisibility(boolean activeUserIsAdmin){
        if(activeUserIsAdmin){
            btn_manageDB.setVisible(true);
        }
    }

    public  void press_btn_manageDB (javafx.event.ActionEvent event)throws Exception{

        change_Scene_to(event,"../scenes/admin.fxml");

    }

    public void press_btn_playUrl (javafx.event.ActionEvent event)throws Exception {

        track = new Media(lbl_showUrl.getText());
        player = new MediaPlayer(track);
        player.play();

    }

    public void update_all_user_changes () {

        List<Playlist> addedPlaylists = new ArrayList<>();


        for (Playlist playlist : activeUser.getUserPlaylists()) {
            if (playlist.getPlaylistId() == 0) {
                addedPlaylists.add(playlist);
            }
        }

        StringBuilder query = new StringBuilder();

        if (addedPlaylists.size() != 0) {
            query.append("INSERT INTO g7musicappdb.playlists (playlist_id, pl_title, owner_id) VALUES ");

            int i = 0;
            for (Playlist p : addedPlaylists) {
                query.append("(0, '").append(p.getPlTitle()).append("', ").append(p.getPlOwner()).append(")");
                if (i++ == addedPlaylists.size() - 1) {
                    query.append(";\n");


                } else {
                    query.append(",");
                }
            }

            //System.out.println(query);
            try {
                UserDAO.updateUser(query.toString());
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }

        StringBuilder delQ = new StringBuilder();

        if (removedPlaylists.size() > 0) {
            delQ.append("DELETE FROM g7musicappdb.playlists WHERE playlist_id IN ( ");

            int j = 0;

            for (int id : removedPlaylists) {
                delQ.append(id);

                if (j++ == removedPlaylists.size() - 1) {
                    delQ.append(");\n");
                } else {
                    delQ.append(",");
                }
            }

           // System.out.println(query);
            try {
                UserDAO.updateUser(delQ.toString());
            }
            catch (SQLException e) {
                e.printStackTrace();
            }

        }


      /*  StringBuilder songq = new StringBuilder();

            for (Playlist playlist : addedPlaylists) {


                    if (!playlist.getSongsInPlaylist().isEmpty()) {


                        songq.append("INSERT INTO g7musicappdb.song_playlist_references (ref_id, song_id, playlist_id) VALUES");

                        int z = 0;

                        for (Song song : playlist.getSongsInPlaylist()) {

                            songq.append("(0, " + song.getSongId() + ", (SELECT playlists.playlist_id FROM g7musicappdb.playlists " +
                                                 "WHERE pl_title like '" + playlist.getPlTitle() + "' and owner_id = " + activeUser.getUserId() + " ))");

                            if (z++ == playlist.getSongsInPlaylist().size() - 1) {
                                songq.append(";");
                            } else {
                                songq.append(",");
                            }

                        }

                        try {
                            UserDAO.updateUser(songq.toString());
                        }
                        catch (SQLException e) {
                            e.printStackTrace();
                        }
                        }
                    }*/








    }

    public void load_all(){
        String strActiveUserId = Integer.toString(activeUser.getUserId());
        ObservableList<Playlist> userPlaylists = null;

        try {
            userPlaylists = PlaylistDAO.buildPlaylistData(strActiveUserId);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        activeUser.setUserPlaylists(userPlaylists);//saves all playlists of active user locally
        loadSongsToUserPlaylistsLocal(userPlaylists);//saves all songs of every playlist locally
    }


}
