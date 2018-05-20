package controllers;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import model.Playlist;
import model.PlaylistDAO;
import model.Song;
import model.SongDAO;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import util.HttpGet;

import java.sql.SQLException;
import java.util.Optional;

import static controllers.LogInController.activeUser;


public class HomeController extends MainController{

    public static Playlist selectedPlaylist;
    public static boolean okToDelete;
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

    public TableColumn<Song,String> col_searchTitle= new TableColumn<>("First Name");
    public TableColumn<Song,String> col_searchArtist;
    public TableColumn<Song,String> col_searchAlbum;
    public Label lbl_playlistName;

    ContextMenu cm = new ContextMenu();
    MenuItem mi_delete = new MenuItem("Delete");

    @FXML
    void initialize() throws SQLException, ClassNotFoundException{

        String fullName = activeUser.getFirstName() + " " + activeUser.getLastName();

        lbl_user.setText(fullName);
        lbl_currentTrack.setText("");
        update_tbl_userPlaylists();



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

        if (plTitle.equals("empty")) {



        }else{

            String strActiveUserId = Integer.toString(activeUser.getUserId());
            System.out.println(plTitle);
            PlaylistDAO.insertPlaylist(plTitle, strActiveUserId);

            update_tbl_userPlaylists();
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

    public void update_tbl_userPlaylists() throws SQLException, ClassNotFoundException{





        String strActiveUserId = Integer.toString(activeUser.getUserId());
        ObservableList<Playlist> userPlaylists = PlaylistDAO.buildPlaylistData(strActiveUserId);
        col_userPlaylistTitle.setCellValueFactory(cellData -> cellData.getValue().plTitleProperty());
        tbl_userPlaylists.setItems(userPlaylists);



    }

    public void click_tbl_playlistTracks(MouseEvent event) throws SQLException, ClassNotFoundException{


            if (event.getButton() == MouseButton.PRIMARY  && event.getClickCount() == 2) {

                update_tbl_playlistTracks();

            }else if(event.getButton() == MouseButton.SECONDARY) {


                cm.getItems().add(mi_delete);


                cm.show(tbl_userPlaylists, event.getScreenX(), event.getScreenY());

                mi_delete.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {

                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

                        alert.setContentText("Are you ok with this?");

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

    public void update_tbl_playlistTracks() throws SQLException, ClassNotFoundException {


        selectedPlaylist = tbl_userPlaylists.getSelectionModel().getSelectedItem();
        lbl_playlistName.setText(selectedPlaylist.getPlTitle());

        ObservableList<Song> playlistSongs = SongDAO.buildSongDataFromPlaylist(selectedPlaylist.getPlaylistId());

        col_title.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        col_artist.setCellValueFactory(cellData -> cellData.getValue().artistProperty());
        col_album.setCellValueFactory(cellData -> cellData.getValue().albumProperty());
        tbl_playlistTracks.setItems(playlistSongs);


    }


    public void press_btn_addSong() throws Exception{

        selectedPlaylist = tbl_userPlaylists.getSelectionModel().getSelectedItem();
        selectedSong = tbl_searchResults.getSelectionModel().getSelectedItem();


        PlaylistDAO.insertSonginPlaylist(Integer.toString(selectedSong.getSongId()),Integer.toString(selectedPlaylist.getPlaylistId()));

        update_tbl_userPlaylists();
        update_tbl_playlistTracks();


    }

    public void delete_Playlist() throws SQLException, ClassNotFoundException{


        selectedPlaylist = tbl_userPlaylists.getSelectionModel().getSelectedItem();

        System.out.println(selectedPlaylist.getPlaylistId() + " " + selectedPlaylist.getPlTitle()+" gets deleted");

        PlaylistDAO.deletePlaylist(Integer.toString(selectedPlaylist.getPlaylistId()));

        update_tbl_playlistTracks();
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

        System.out.println(HttpGet.getDownload(txt_test.getText()));

    }

    public void press_btn_ytSearch(ActionEvent event) throws Exception {
        change_Scene_to(event,"../scenes/youtube-search.fxml");
    }



}
