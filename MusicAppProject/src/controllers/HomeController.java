package controllers;


import com.jfoenix.controls.*;
import javafx.animation.TranslateTransition;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.Duration;
import model.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import util.HttpGet;

import javax.swing.*;
import java.io.IOException;
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
    public StackPane stpn_settings;
    public AnchorPane pn_search;
    public JFXButton btn_searchDB;
    public AnchorPane pn_searchyt;
    public JFXButton btn_ytSearch;
    public Slider slider;

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

       try {
            load_searchYTpane();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        load_animation_controls();



        //pn_searchyt.setStyle("-fx-background-color: rgba(255, 255, 255, 0);");


    }

    private void sliderSetUp(){
        // Listen to the slider. When it changes, seek with the player.
// MediaPlayer.seek does nothing when the player is stopped, so the play/pause button's handler
// always sets the start time to the slider's current value, and then resets it to 0 after starting the player.
        InvalidationListener sliderChangeListener = o-> {
            Duration seekTo = Duration.seconds(slider.getValue());
            player.seek(seekTo);
        };
        slider.valueProperty().addListener(sliderChangeListener);

// Link the player's time to the slider
        player.currentTimeProperty().addListener(l-> {
            // Temporarily remove the listener on the slider, so it doesn't respond to the change in playback time
            // I thought timeSlider.isValueChanging() would be useful for this, but it seems to get stuck at true
            // if the user slides the slider instead of just clicking a position on it.
            slider.valueProperty().removeListener(sliderChangeListener);

            // Keep timeText's text up to date with the slider position.
            Duration currentTime = player.getCurrentTime();
            int value = (int) currentTime.toSeconds();
            slider.setValue(value);

            // Re-add the slider listener
            slider.valueProperty().addListener(sliderChangeListener);
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

            player.setOnReady(new Runnable() {

                @Override
                public void run() {

                    System.out.println("Duration: "+track.getDuration().toSeconds());

                    // display media's metadata
                    for (Map.Entry<String, Object> entry : track.getMetadata().entrySet()){
                        System.out.println(entry.getKey() + ": " + entry.getValue());
                    }

                    // play if you want
                    player.play();
                }
            });



        }
        catch (Exception e) {
            System.out.println("no media found");
        }

        sliderSetUp();

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

        JFXDialogLayout content = new JFXDialogLayout();
        //content.setHeading(new Text("User Profile Settings"));



        JFXDialog dialog = new JFXDialog(stpn_settings, content, JFXDialog.DialogTransition.TOP);


        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));

        grid.getColumnConstraints().addAll(  new ColumnConstraints( 150 ), new ColumnConstraints( 150 ));
        //grid.setStyle("-fx-background-color: greenyellow;");


        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("New Password");
        PasswordField repeatPasswordField = new PasswordField();
        repeatPasswordField.setPromptText("Repeat new Password");

        TextField txt_changeEmail = new TextField();
        txt_changeEmail.setPromptText(activeUser.getEmail());

        Label lbl_newPsw = new Label("New Password");
        Label lbl_repNewPsw = new Label("Repeat new Password");
        Label lbl_changeEmail = new Label("E-mail");
        Label lbl_pswdError = new Label("!");
        lbl_pswdError.setVisible(false);
        lbl_newPsw.setStyle("-fx-text-fill: #fffafa ");
        lbl_repNewPsw.setStyle("-fx-text-fill: #fffafa");
        lbl_pswdError.setStyle("-fx-text-fill: red");
        lbl_changeEmail.setStyle("-fx-text-fill: #fffafa");


        grid.add(lbl_newPsw, 0, 0);
        grid.add(passwordField, 1, 0);

        grid.add(lbl_repNewPsw, 0, 1);
        grid.add(repeatPasswordField, 1, 1);
        grid.add(lbl_pswdError, 2,1);

        grid.add(lbl_changeEmail, 0, 2);
        grid.add(txt_changeEmail, 1, 2);
//        Bounds bounds = dialog.localToScreen(dialog.getBoundsInLocal());
//        bounds.contains(200, 94);
        JFXButton btn_close = new JFXButton("close");
        JFXButton btn_updateInfo = new JFXButton("Save Changes");
        btn_close.setOnAction(event1 -> dialog.close());
        btn_updateInfo.setOnAction(event2-> updateUserInfo(event, passwordField.getText(),txt_changeEmail.getText()));


        btn_close.setStyle("-fx-background-color: #000080; -fx-text-fill: #fffafa; -fx-background-radius: 100");
        btn_close.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                btn_close.setStyle("-fx-background-color: #3733cb; -fx-text-fill: #fffafa; -fx-background-radius: 100");
            }else{
                btn_close.setStyle("-fx-background-color: #000080; -fx-text-fill: #fffafa; -fx-background-radius: 100");
            }

        });

        btn_updateInfo.setStyle("-fx-background-color: #027232; -fx-text-fill: #fffafa; -fx-background-radius: 100");
        btn_updateInfo.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                btn_updateInfo.setStyle("-fx-background-color: #02a149;-fx-text-fill: #fffafa; -fx-background-radius: 100");
            }else{
                btn_updateInfo.setStyle("-fx-background-color: #027232; -fx-text-fill: #fffafa; -fx-background-radius: 100");
            }

        });

        grid.add(btn_updateInfo, 1,4);
        grid.add(btn_close, 0, 4);

        grid.setStyle("-fx-background-color: #1d1d1d");

        dialog.setContent(grid);

        repeatPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.equals(passwordField.getText())){
                lbl_pswdError.setVisible(true);
                btn_updateInfo.setDisable(true);
            }else{
                lbl_pswdError.setVisible(false);
                btn_updateInfo.setDisable(false);
            }

        });

        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.equals(repeatPasswordField.getText())){
                lbl_pswdError.setVisible(true);
                btn_updateInfo.setDisable(true);
            }else{
                lbl_pswdError.setVisible(false);
                btn_updateInfo.setDisable(false);
            }

        });


        dialog.show();

    }

    public void updateUserInfo(ActionEvent event, String password, String email) {


        try {
            UserDAO.updateUserProfileInfo(activeUser.getUserId(), password, email);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }




    }



    private void load_searchYTpane()throws Exception{

        pn_searchyt.getChildren().clear();
        pn_searchyt.getChildren().add(FXMLLoader.load(getClass().getResource("../scenes/searchyt.fxml")));
        pn_searchyt.setLayoutX(0);
        pn_searchyt.setLayoutY(0);

    }


    public void press_btn_manageDB(javafx.event.ActionEvent event) throws Exception {

        change_Scene_to(event, "../scenes/admin.fxml");

    }

    public void load_animation_controls() {
        //AnchorPane pane =FXMLLoader.load(getClass().getResource("../scenes/searchdb.fxml"));
        //pn_search.getChildren().setAll(pane);

        TranslateTransition closeYTsearchAction = new TranslateTransition(new Duration(350), pn_searchyt);
        TranslateTransition openYTsearchAction = new TranslateTransition(new Duration(350), pn_searchyt );

        //serve para voltar quando aperto o botão de menu

        BoxBlur bb = new BoxBlur();

        btn_searchDB.setOnAction((ActionEvent event) -> {
            if (pn_searchyt.getTranslateX()!= 0){
                openYTsearchAction.setToX(0);
                openYTsearchAction.play();
                pn_search.setEffect(bb);
                btn_searchDB.setText("DB Search");
            }else{
                closeYTsearchAction.setToX(+(pn_searchyt.getWidth()));
                closeYTsearchAction.play();
                //btn_searchDB.setText("YT Search");
            }
        });




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


    public void testHttpGet(ActionEvent even)throws Exception{

        HttpGet.getDownload(txt_test.getText());
    }


}
