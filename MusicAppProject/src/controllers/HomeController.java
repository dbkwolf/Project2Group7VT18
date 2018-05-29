package controllers;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
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
import java.util.*;

import static controllers.LogInController.activeUser;


public class HomeController extends MainController {


    public Label lbl_user;
    // public ObservableList<Playlist> userPlaylists = activeUser.getUserPlaylists();

    public static Playlist selectedPlaylist;

    public TableView <Playlist> tbl_userPlaylists;
    public TableColumn <Playlist, String> col_userPlaylistTitle;

    public TableView <Song> tbl_playlistTracks;
    public TableColumn <Song, String> col_title;
    public TableColumn <Song, String> col_artist;
    public TableColumn <Song, String> col_album;

    public JFXTextField txt_search;
    public JFXTextField txt_test;
    public TableView <Song> tbl_searchResults;

    public TableColumn <Song, String> col_searchTitle;
    public TableColumn <Song, String> col_searchArtist;
    public TableColumn <Song, String> col_searchAlbum;
    public Label lbl_playlistName;
    public JFXButton btn_manageDB;
    public AnchorPane apn_middleHomeAnchorpane;
    public Label lbl_showUrl;
    private String url;


    public Song selectedSong;
    public Media track;
    public MediaPlayer player;
    public JFXButton btn_play;
    public JFXButton btn_pause;
    public Label lbl_currentTrack;



    private ContextMenu playlist_cm = new ContextMenu();
    private MenuItem delete_mi = new MenuItem("Delete");





    @FXML
    void initialize() throws SQLException, ClassNotFoundException {

        String fullName = activeUser.getFirstName() + " " + activeUser.getLastName();
        lbl_user.setText(fullName);

        lbl_currentTrack.setText("");


        load_user_music();

        populate_tbl_userPlaylists();


        playlist_cm.getItems().add(delete_mi);



        loadAdminPrivileges(activeUser.isAdminLevel());


        lbl_playlistName.visibleProperty().bind(
                Bindings.isNotNull(tbl_userPlaylists.getSelectionModel().selectedItemProperty())
        );

        populate_tbl_playlistTracks();

        tbl_userPlaylists.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {

                lbl_playlistName.setText(newSelection.getPlTitle());

                tbl_playlistTracks.setItems(newSelection.getSongsInPlaylist());

                selectedPlaylist = newSelection;

            } else {

                lbl_playlistName.setText("");

            }
        });


    }


    public void press_btn_newPlaylist(javafx.event.ActionEvent event) throws Exception {

        String plTitle = namePlaylistPrompt();

        //if the name of new playlist is valid
        if (!plTitle.equals("empty")) {

            try {

                //addnew playlist to database
                Playlist newPlaylist = PlaylistDAO.newInsertedPlaylist(plTitle, activeUser.getUserId());

                //update user's local Playlist list with the new inserted Playlist
                activeUser.getUserPlaylists().add(newPlaylist);

            }

            catch (SQLException e) {

                e.printStackTrace();
            }
        }

        for (Playlist p : activeUser.getUserPlaylists()) {
            System.out.println(p.getPlaylistId() + " " + p.getPlTitle());
        }

    }

    private String namePlaylistPrompt() {
        String playlistName = "empty";

        TextInputDialog dialog = new TextInputDialog(activeUser.getFirstName() + "Playlist1"); // no apostrophes since it fs up SQL
        dialog.setTitle("Create Playlist");
        dialog.setHeaderText("");
        dialog.setContentText("Name:");

        Optional <String> result = dialog.showAndWait();

        //result.ifPresent(name -> {  this.label.setText(name); });
        if (result.isPresent()) {
            playlistName = result.get();
        }
        return playlistName;
    }



    public void click_tbl_userPlaylists(MouseEvent event) {


        if (event.getButton() == MouseButton.SECONDARY) {

            if (!activeUser.getUserPlaylists().isEmpty()) {
                playlist_cm.show(tbl_userPlaylists, event.getScreenX(), event.getScreenY());
            }
            delete_mi.setOnAction(event1 -> {

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

                alert.setContentText("Are you sure you want to delete this playlist?");

                Optional <ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {

                    try {
                        delete_Playlist();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {

                    alert.close();

                }
            });


        }

    }

    private void delete_Playlist() throws SQLException, ClassNotFoundException {


        selectedPlaylist = tbl_userPlaylists.getSelectionModel().getSelectedItem();

        //delete from local playlist
        activeUser.getUserPlaylists().remove(selectedPlaylist);

        //delete from DB
        try {
            PlaylistDAO.deletePlaylist(selectedPlaylist.getPlaylistId());
        }
        catch (SQLException e) {
            e.printStackTrace();
        }


        for (Playlist p : activeUser.getUserPlaylists()) {
            System.out.println(p.getPlaylistId() + " " + p.getPlTitle());
        }
    }




    public void press_btn_addSong() throws Exception {

        selectedPlaylist = tbl_userPlaylists.getSelectionModel().getSelectedItem();

        selectedSong = tbl_searchResults.getSelectionModel().getSelectedItem();

        System.out.println(selectedPlaylist.getPlaylistId() + selectedPlaylist.getPlTitle());
        System.out.println(selectedSong.getRefId());

        try {
            //Song goes in wihtout and comes out with ref id
            Song  addedSong = SongDAO.insertSonginPlaylist(selectedSong.getSongId(), selectedPlaylist.getPlaylistId(), selectedSong);
            System.out.println("after inserting the song into the playlist, we get this new instance of the song with ref id:" + addedSong.getRefId());

            //song gets added locally
            activeUser.getUserPlaylists().get(findPlaylistListPosition(selectedPlaylist.getPlaylistId())).getSongsInPlaylist().add(addedSong);

        }
        catch (SQLException e) {
            e.printStackTrace();
        }




    }

    public void click_tbl_playlistTracks(MouseEvent event) {

        //if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2)


     if (event.getButton() == MouseButton.SECONDARY) {

         if(selectedPlaylist!=null && !selectedPlaylist.getSongsInPlaylist().isEmpty())

            playlist_cm.show(tbl_userPlaylists, event.getScreenX(), event.getScreenY());

            delete_mi.setOnAction(event1 -> delete_songFromPlaylist());


        }

    }

    private void delete_songFromPlaylist() {

        int plPosition = findPlaylistListPosition(selectedPlaylist.getPlaylistId());
        selectedSong = tbl_playlistTracks.getSelectionModel().getSelectedItem();

        activeUser.getUserPlaylists().get(plPosition).getSongsInPlaylist().remove(selectedSong);

        try {
            SongDAO.deleteSongfromPlaylist(selectedSong.getRefId());
        }
        catch (SQLException e) {
            e.printStackTrace();
        }



    }





    public void press_btn_search(javafx.event.ActionEvent event) throws SQLException, ClassNotFoundException {

        ObservableList <Song> songsAvailable = SongDAO.searchSong(txt_search.getText());
        col_searchTitle.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        col_searchArtist.setCellValueFactory(cellData -> cellData.getValue().artistProperty());
        col_searchAlbum.setCellValueFactory(cellData -> cellData.getValue().albumProperty());
        tbl_searchResults.setItems(songsAvailable);


    }


    public void press_btn_play(javafx.event.ActionEvent event) throws Exception {
        btn_play.setVisible(false);
        btn_pause.setVisible(true);
        try {
            selectedSong = tbl_searchResults.getSelectionModel().getSelectedItem();
            String selectedSongInfo = selectedSong.getSongTitle() + " - " + selectedSong.getSongArtist() + " - " + selectedSong.getAlbum();
            lbl_currentTrack.setText(selectedSongInfo);
            System.out.println(selectedSong.getSongLocation());
            track = new Media(selectedSong.getSongLocation());
            player = new MediaPlayer(track);
            player.play();
        }
        catch (Exception e) {
            System.out.println("no media found");
        }

    }

    public void press_btn_pause(javafx.event.ActionEvent event) throws Exception {

        btn_pause.setVisible(false);
        btn_play.setVisible(true);
        try {
            player.pause();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("no media was playing");
        }
    }


    public void press_btn_profile_settings(javafx.event.ActionEvent event) throws Exception {


        // Create the custom dialog.
        Dialog <Pair <String, String>> dialog = new Dialog <>();
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

    public void testHttpGet() throws Exception {


        url = HttpGet.getDownload(txt_test.getText());
        lbl_showUrl.setText(url);




    }




    public void press_btn_ytSearch(ActionEvent event) throws Exception {
        change_Scene_to(event, "../scenes/youtube-search.fxml");
    }



    public void press_btn_manageDB(javafx.event.ActionEvent event) throws Exception {

        change_Scene_to(event, "../scenes/admin.fxml");

    }

    public void press_btn_playUrl(javafx.event.ActionEvent event) throws Exception {

        track = new Media(lbl_showUrl.getText());
        player = new MediaPlayer(track);
        player.play();

    }


    public int findPlaylistListPosition(int playlistId) {


        int i = 0;
        while (activeUser.getUserPlaylists().get(i).getPlaylistId() != playlistId) {
            System.out.println(i);
            i++;
        }

        return i;
    }


    private void populate_tbl_userPlaylists() {

        col_userPlaylistTitle.setCellValueFactory(cellData -> cellData.getValue().plTitleProperty());
        tbl_userPlaylists.setItems(activeUser.getUserPlaylists());
    }

    private void populate_tbl_playlistTracks() {

        col_title.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        col_artist.setCellValueFactory(cellData -> cellData.getValue().artistProperty());
        col_album.setCellValueFactory(cellData -> cellData.getValue().albumProperty());

    }


    private void load_user_music() {

        try {
            activeUser.setUserPlaylists(PlaylistDAO.buildPlaylistData(activeUser.getUserId()));
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        if (activeUser.getUserPlaylists().isEmpty()) {
            System.out.println("user has no playlists yet");
        }

        for (Playlist p : activeUser.getUserPlaylists()) {
            System.out.println(p.getPlaylistId() + " " + p.getPlTitle());
        }

        loadSongsToUserPlaylistsLocal(activeUser.getUserPlaylists());//saves all songs of every playlist locally
    }

    private void loadAdminPrivileges(boolean activeUserIsAdmin) {
        if (activeUserIsAdmin) {
            btn_manageDB.setVisible(true);
        }
    }

    public void loadSongsToUserPlaylistsLocal(ObservableList <Playlist> userPlaylists) {


        userPlaylists.forEach((p) -> {
            try {
                p.setSongsInPlaylist(SongDAO.buildSongDataFromPlaylist(p.getPlaylistId()));

            }
            catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        });


    }




}
