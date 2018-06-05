package controllers;


import com.jfoenix.controls.*;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Duration;
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


    public Label lbl_activeUser;

    //user profile info update
    public JFXButton btn_profile_settings;
    public StackPane stpn_settings;

    //currently selected playlist
    private static Playlist selectedPlaylist;
    public Label lbl_selectedPlaylistTitle;

    //Table of user's playlists
    public TableView <Playlist> tbl_userPlaylists;
    public TableColumn <Playlist, String> col_userPlaylistTitle;

    //Table of playlists's songs
    public TableView <Song> tbl_playlistSongs;
    public TableColumn <Song, String> col_title;
    public TableColumn <Song, String> col_artist;
    public TableColumn <Song, String> col_album;
    public TableColumn <Song, String> col_duration;


    //Button used for toggling between YT and DB search
    public JFXButton btn_toggleSearch;
    public ImageView img_btnToggleSearch;
    private Image imgDBlogo = new Image("/images/dblogo.png");
    private Image imgYTlogo = new Image("/images/ytlogo.png");

    //Contents of DB Search Pane, incl. table of search results
    public AnchorPane pn_search;
    public JFXTextField txt_search;
    public TableView <Song> tbl_searchResults;
    public TableColumn <Song, String> col_searchTitle;
    public TableColumn <Song, String> col_searchArtist;
    public TableColumn <Song, String> col_searchAlbum;

    //YT Search pane, used for loading our searchyt.fxml
    public AnchorPane pn_searchyt;


    //Media Player and its button controls
    public Media songTrack;
    public MediaPlayer player;
    public JFXButton btn_play;
    public JFXButton btn_pause;
    public JFXButton btn_next;
    public JFXButton btn_previous;

    //Media slider
    public Slider slider;
    //auxiliary double to aid slider functionality
    private static final double MIN_CHANGE = 0.5;

    //currently selected song and it's information
    private static Song selectedSong;
    public Label lbl_selectedSongInfo;

    private Song currentPlaying;

    //For DNS Server testing purposes
    public JFXTextField txt_test;

    //Button allowing admin functionality
    public JFXButton btn_manageDB;

    //Menus for right click on playlists and songs table
    private ContextMenu playlist_cm = new ContextMenu();
    private MenuItem delete_mi = new MenuItem("Delete");

    //effect
    BoxBlur bb = new BoxBlur();


    @FXML
    void initialize() {


        //get current active user's full name and display it
        String fullName = activeUser.getFirstName() + " " + activeUser.getLastName();
        lbl_activeUser.setText(fullName);

        //checks if active user is admin
        loadAdminPrivileges(activeUser.isAdminLevel());


        //retrieve active user's playlists and song information
        load_user_music();


        //check that the user has playlists
        if (!activeUser.getUserPlaylists().isEmpty()){

            //load the first one
            selectedPlaylist = activeUser.getUserPlaylists().get(0);
            lbl_selectedPlaylistTitle.setText(selectedPlaylist.getPlTitle());
            tbl_playlistSongs.setItems(selectedPlaylist.getSongsInPlaylist());

        } else {
            System.out.println("why you have no playlist?");
        }

        populate_tbl_userPlaylists();


        tbl_userPlaylists_bindings();

        selectedSong_bindings();


        playlist_cm.getItems().add(delete_mi);


        //make song info label empty
        lbl_selectedSongInfo.setText("");


        populate_tbl_playlistTracks();


        try {
            load_searchYTpane();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        load_animation_controls();

        btn_play.setText("");
        btn_pause.setText("");
        btn_next.setText("");
        btn_previous.setText("");


       press_enter_for_search();

       txt_search.setStyle("-fx-text-fill: white");


    }

    /**
     * This happens every time the user selects a playlist
     */
    private void tbl_userPlaylists_bindings() {


        tbl_userPlaylists.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {

            //if a playlist has actually been selected
            if (newSelection != null){

                selectedPlaylist = newSelection;

                //change label to respective playlist title
                lbl_selectedPlaylistTitle.setText(newSelection.getPlTitle());

                //populate songs table
                tbl_playlistSongs.setItems(selectedPlaylist.getSongsInPlaylist());

                System.out.println(selectedPlaylist.getPlTitle());

            }
        });


    }


    /**
     * allows to search from DB with just hitting enter
     */
    private void press_enter_for_search(){

        txt_search.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)){
                try {
                    enter_search();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
                catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    /**
     * Set up for the media player
     */
    private void sliderSetUp() {


        //to jump to a specific time in song
        //(for when seeking in song using drag / drop )
        slider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging){
                player.seek(Duration.seconds(slider.getValue()));
            }
        });

        //to jump to a specific time in song
        //(when clicking on specific value on slider (no drag))
        slider.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!slider.isValueChanging()){
                double currentTime = player.getCurrentTime().toSeconds();

                //to ignore small changes made from the synchronization to player
                if (Math.abs(currentTime - newValue.doubleValue()) > MIN_CHANGE){
                    player.seek(Duration.seconds(newValue.doubleValue()));
                }
            }
        });

        //Synchronizes the slider's value to the current time in song
        player.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            if (!slider.isValueChanging()){
                slider.setValue(newTime.toSeconds());
            }
        });


    }

    /**
     * When pressing the new Playlist button
     */
    public void press_btn_newPlaylist() {

        String plTitle = namePlaylistPrompt();

        //if the name of new playlist is valid
        if (!plTitle.equals("empty")){

            try {

                //add new playlist to database
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


    /**
     * Prompts the user to input a name for their new playlist
     *
     * @return the name of the new playlist
     */
    private String namePlaylistPrompt() {
        String playlistName = "empty";

        TextInputDialog dialog = new TextInputDialog(activeUser.getFirstName() + "Playlist1"); // no apostrophes since it fs up SQL
        dialog.setTitle("Create Playlist");
        dialog.setHeaderText("");
        dialog.setContentText("Name:");

        Optional <String> result = dialog.showAndWait();

        //result.ifPresent(name -> {  this.label.setText(name); });
        if (result.isPresent()){
            playlistName = result.get();
        }
        return playlistName;
    }


    /**
     * Listens to see if a right click has been made on the table
     * shows the context menu for deleting a playlist
     * shows an alert for confirmation
     *
     * @param event onClick
     */
    public void click_tbl_userPlaylists(MouseEvent event) {


        if (event.getButton() == MouseButton.SECONDARY){

            if (!activeUser.getUserPlaylists().isEmpty()){
                playlist_cm.show(tbl_userPlaylists, event.getScreenX(), event.getScreenY());
            }
            delete_mi.setOnAction(event1 -> {

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

                alert.setContentText("Are you sure you want to delete this playlist?");

                Optional <ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){

                    delete_Playlist();

                } else {

                    alert.close();

                }
            });


        }

    }

    /**
     * Deletes Playlist from DB and updates local info
     */
    private void delete_Playlist() {

        //delete from DB
        try {
            PlaylistDAO.deletePlaylist(selectedPlaylist.getPlaylistId());
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        //delete from local playlist
        activeUser.getUserPlaylists().remove(findPlaylistListPosition(selectedPlaylist.getPlaylistId()));

    }

    /**
     * Updates current selected song after selecting either from the search results or the playlist table.
     */
    private void selectedSong_bindings() {

        tbl_searchResults.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {

            selectedSong = newSelection;

            }

        );

        tbl_playlistSongs.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {

            selectedSong = newSelection;

            }
        );
    }


    /**
     * Add song from search table to the currently selected playlist
     */
    public void press_btn_addSong() {


        //validate that a song has been selected
        if (!tbl_searchResults.getSelectionModel().isEmpty()&&!tbl_userPlaylists.getSelectionModel().isEmpty()){


            System.out.println(selectedPlaylist.getPlaylistId() + selectedPlaylist.getPlTitle());
            System.out.println(selectedSong.getRefId());

            try {
                //Song goes in without and comes out with ref id
                Song addedSong = SongDAO.insertSonginPlaylist(selectedSong.getSongId(), selectedPlaylist.getPlaylistId(), selectedSong);
                System.out.println("after inserting the song into the playlist, we get this new instance of the song with ref id:" + addedSong.getRefId());

                //song gets added locally
                activeUser.getUserPlaylists().get(findPlaylistListPosition(selectedPlaylist.getPlaylistId())).getSongsInPlaylist().add(addedSong);

            }
            catch (SQLException e) {
                e.printStackTrace();
            }

        }else{
            System.out.println("why u no select a song/playlist first?");

            // TODO: alert the user of no song or playlist selected

        }
    }


    /**
     * Next song button function
     * TODO: More testing!!!
     */
    public void press_next() {

        if (player != null){
            int id = selectedSong.getSongId();

            int i = 0;
            while (selectedPlaylist.getSongsInPlaylist().get(i).getSongId() != id) {
                System.out.println(i);
                i++;
            }

            if (i < selectedPlaylist.getSongsInPlaylist().size() - 1){

                selectedSong = selectedPlaylist.getSongsInPlaylist().get(i++);

                play();
            } else {
                player.stop();
            }

        }

    }

    /**
     * Detect a right click to show a context menu with the option to delete a song
     * @param event OnMouseClick
     */
    public void click_tbl_playlistTracks(MouseEvent event) {

        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2){

            if (player != null){
                if (player.getStatus().equals(MediaPlayer.Status.PLAYING)){
                    player.stop();
                }
            }
            selectedSong = tbl_playlistSongs.getSelectionModel().getSelectedItem();
            play();


        } else if (event.getButton() == MouseButton.SECONDARY){

            if (selectedPlaylist != null && !selectedPlaylist.getSongsInPlaylist().isEmpty())

                playlist_cm.show(tbl_userPlaylists, event.getScreenX(), event.getScreenY());

            delete_mi.setOnAction(event1 -> delete_songFromPlaylist());


        }


    }

    /**
     * deletes song from both DB and updates local
     */
    private void delete_songFromPlaylist() {

        int plPosition = findPlaylistListPosition(selectedPlaylist.getPlaylistId());
        selectedSong = tbl_playlistSongs.getSelectionModel().getSelectedItem();


        try {
            SongDAO.deleteSongfromPlaylist(selectedSong.getRefId());
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        activeUser.getUserPlaylists().get(plPosition).getSongsInPlaylist().remove(selectedSong);


    }

    /**
     * Populates the search-results-table after searching DB
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void enter_search() throws SQLException, ClassNotFoundException {

        ObservableList <Song> songsAvailable = SongDAO.searchSong(txt_search.getText());
        col_searchTitle.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        col_searchArtist.setCellValueFactory(cellData -> cellData.getValue().artistProperty());
        col_searchAlbum.setCellValueFactory(cellData -> cellData.getValue().albumProperty());
        tbl_searchResults.setItems(songsAvailable);


    }


    /**
     * plays media player
     */
    public void play() {



        try {
            //if a song has been selected
            if (selectedSong != null){
                //display song info
                String selectedSongInfo = selectedSong.getSongTitle() + " - " + selectedSong.getSongArtist() + " - " + selectedSong.getAlbum();
                lbl_selectedSongInfo.setText(selectedSongInfo);

                //get location of song and set as media
                songTrack = new Media(selectedSong.getSongLocation());

                //load player with media
                player = new MediaPlayer(songTrack);

                //to obtain the length of the track in seconds
                player.setOnReady(() -> {

                    System.out.println("Duration: " + songTrack.getDuration().toSeconds());

                    //set the bounds of the slider to match the length of the track
                    slider.maxProperty().bind(Bindings.createDoubleBinding(
                            () -> player.getTotalDuration().toSeconds(),
                            player.totalDurationProperty()));


                    // display media's metadata
                    for (Map.Entry <String, Object> entry : songTrack.getMetadata().entrySet()) {
                        System.out.println(entry.getKey() + ": " + entry.getValue());
                    }

                    player.play();

                });

                //visibility control
                btn_play.setVisible(false);
                btn_pause.setVisible(true);

            }

        }
        catch (Exception e) {
            System.out.println("no media found");
        }

        sliderSetUp();

    }


    public void pause() {

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

    /**
     * Generates a dialog for the  user to update their profile settings
     * co-Authors: Daniel, Imasha
     * @param event onClick
     * @throws Exception
     */
    public void press_btn_profile_settings(javafx.event.ActionEvent event) throws Exception {

        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("User Profile Settings"));


        JFXDialog dialog = new JFXDialog(stpn_settings, content, JFXDialog.DialogTransition.CENTER);


        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));

        grid.getColumnConstraints().addAll(new ColumnConstraints(150), new ColumnConstraints(150));


        //user input fields
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


        //buttons
        JFXButton btn_close = new JFXButton("close");
        JFXButton btn_updateInfo = new JFXButton("Save Changes");
        btn_close.setOnAction(event1 -> close(dialog));
        btn_updateInfo.setOnAction(event2 -> updateUserInfo(passwordField.getText(), txt_changeEmail.getText(), dialog));


        btn_close.setStyle("-fx-background-color: #000080; -fx-text-fill: #fffafa; -fx-background-radius: 100");
        btn_close.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue){
                btn_close.setStyle("-fx-background-color: #3733cb; -fx-text-fill: #fffafa; -fx-background-radius: 100");
            } else {
                btn_close.setStyle("-fx-background-color: #000080; -fx-text-fill: #fffafa; -fx-background-radius: 100");
            }

        });

        btn_updateInfo.setStyle("-fx-background-color: #027232; -fx-text-fill: #fffafa; -fx-background-radius: 100");
        btn_updateInfo.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue){
                btn_updateInfo.setStyle("-fx-background-color: #02a149;-fx-text-fill: #fffafa; -fx-background-radius: 100");
            } else {
                btn_updateInfo.setStyle("-fx-background-color: #027232; -fx-text-fill: #fffafa; -fx-background-radius: 100");
            }

        });

        grid.add(lbl_newPsw, 0, 0);
        grid.add(passwordField, 1, 0);

        grid.add(lbl_repNewPsw, 0, 1);
        grid.add(repeatPasswordField, 1, 1);
        grid.add(lbl_pswdError, 2, 1);

        grid.add(lbl_changeEmail, 0, 2);
        grid.add(txt_changeEmail, 1, 2);
        grid.add(btn_updateInfo, 1, 4);
        grid.add(btn_close, 0, 4);

        grid.setStyle("-fx-background-color: #1d1d1d");

        dialog.setContent(grid);


      /*------------------------------<password validation>----------------------------------*/
        repeatPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(passwordField.getText())){
                lbl_pswdError.setText("no match");
                lbl_pswdError.setVisible(true);
                btn_updateInfo.setDisable(true);
            } else {
                lbl_pswdError.setVisible(false);
                btn_updateInfo.setDisable(false);
            }

        });

        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(repeatPasswordField.getText())){
                lbl_pswdError.setText("no match");
                lbl_pswdError.setVisible(true);
                btn_updateInfo.setDisable(true);
            } else {
                lbl_pswdError.setVisible(false);
                btn_updateInfo.setDisable(false);
            }

            if(newValue.trim().isEmpty() || newValue.trim().length()<5){

                lbl_pswdError.setText("too short!");
                lbl_pswdError.setVisible(true);
            }

        });
        /*------------------------------</password validation>----------------------------------*/



       //some effects

        tbl_playlistSongs.setEffect(bb);
        lbl_selectedPlaylistTitle.setEffect(bb);
        btn_profile_settings.setDisable(true);

        dialog.show();





    }



    /**
     * Updates the DB with the new info
     * @param password : dialog input
     * @param email :dialog input
     * @param dialog : parent dialog
     */
    public void updateUserInfo(String password, String email, JFXDialog dialog) {


        try {
            UserDAO.updateUserProfileInfo(activeUser.getUserId(), password, email);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }


        close(dialog);

    }

    /**
     * revert effects before closing
     * @param dialog :parent dialog
     */
    private void close(JFXDialog dialog) {
        tbl_playlistSongs.setEffect(null);
        lbl_selectedPlaylistTitle.setEffect(null);
        btn_profile_settings.setDisable(false);
        dialog.close();
    }


    /**
     * loads the youtube scene into an anchorpane, child of right border anchorpane
     * @throws Exception
     */
    private void load_searchYTpane() throws Exception {

        pn_searchyt.getChildren().clear();
        pn_searchyt.getChildren().add(FXMLLoader.load(getClass().getResource("../scenes/searchyt.fxml")));
        pn_searchyt.setLayoutX(0);
        pn_searchyt.setLayoutY(0);

    }

    /**
     * changes scene to Admin Scene
     * @param event
     * @throws Exception
     */
    public void press_btn_manageDB(javafx.event.ActionEvent event) throws Exception {

        change_Scene_to(event, "../scenes/admin.fxml");

    }

    /**
     * Right Border Pane Animation
     * Author: Daniel
     */
    public void load_animation_controls() {

        TranslateTransition closeYTsearchAction = new TranslateTransition(new Duration(350), pn_search);
        TranslateTransition openYTsearchAction = new TranslateTransition(new Duration(350), pn_search);

        //serve para voltar quando aperto o botão de menu


        btn_toggleSearch.setOnAction((ActionEvent event) -> {
            if (pn_search.getTranslateX() != 0){
                openYTsearchAction.setToX(0);
                openYTsearchAction.play();
                img_btnToggleSearch.setImage(imgYTlogo);
                btn_toggleSearch.setText("Search YouTube");


            } else {
                closeYTsearchAction.setToX(+(pn_search.getWidth()));
                closeYTsearchAction.play();
                img_btnToggleSearch.setImage(imgDBlogo);
                btn_toggleSearch.setText("Search Database");


            }
        });


    }

    /**
     * Auxiliary method: to find the position of a playlist in a list
     * @param playlistId : the searched playlist Database ID
     * @return List ID
     */
    public int findPlaylistListPosition(int playlistId) {


        int i = 0;
        while (activeUser.getUserPlaylists().get(i).getPlaylistId() != playlistId) {
            System.out.println(i);
            i++;
        }

        return i;
    }

    /**
     * sets columns in the playlists table for population
     */
    private void populate_tbl_userPlaylists() {

        col_userPlaylistTitle.setCellValueFactory(cellData -> cellData.getValue().plTitleProperty());
        tbl_userPlaylists.setItems(activeUser.getUserPlaylists());
    }

    /**
     * sets columns in the playlist songs table for population
     */
    private void populate_tbl_playlistTracks() {

        col_title.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        col_artist.setCellValueFactory(cellData -> cellData.getValue().artistProperty());
        col_album.setCellValueFactory(cellData -> cellData.getValue().albumProperty());
        col_duration.setCellValueFactory(cellData -> cellData.getValue().strSongDurationProperty());
    }

    /**
     * Store active user's playlists and song information locally
     * Aids in minimizing the ratio (MySql queries : onMouseClick)
     */
    private void load_user_music() {

        try {
            activeUser.setUserPlaylists(PlaylistDAO.buildPlaylistData(activeUser.getUserId()));
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        if (activeUser.getUserPlaylists().isEmpty()){
            System.out.println("user has no playlists yet");
        }

        for (Playlist p : activeUser.getUserPlaylists()) {
            System.out.println(p.getPlaylistId() + " " + p.getPlTitle());
        }

        loadSongsToUserPlaylistsLocal(activeUser.getUserPlaylists());//saves all songs of every playlist locally
    }

    /**
     * sets visibility of admin featueres
     * @param activeUserIsAdmin
     */
    private void loadAdminPrivileges(boolean activeUserIsAdmin) {
        if (activeUserIsAdmin){
            btn_manageDB.setVisible(true);
        }
    }

    /**
     * Once playlists have been fetched, this method fetches the songs in each playlist.
     * @param userPlaylists
     */
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


    //For testing DNS Server Connection purposes
    public void testHttpGet(ActionEvent even) throws Exception {

        HttpGet.getDownload(txt_test.getText());
    }


}
