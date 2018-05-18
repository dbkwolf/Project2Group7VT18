package controllers;


import com.jfoenix.controls.JFXButton;
import model.Playlist;
import model.PlaylistDAO;
import model.Song;
import model.SongDAO;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.sql.SQLException;
import java.util.Optional;

import static controllers.LogInController.activeUser;


public class HomeController extends MainController{

    public Song selectedSong;
    public Playlist selectedPlaylist;
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


    private  ObservableList<Song> data;

    public  TableView<Song> tbl_searchResults;
    public  Label lbl_user;
    public TableColumn<Song,String> col_searchTitle= new TableColumn<>("First Name");
    public TableColumn<Song,String> col_searchArtist;
    public TableColumn<Song,String> col_searchAlbum;

    @FXML
    void initialize() throws SQLException, ClassNotFoundException{

        String fullName = activeUser.getFirstName() + " " + activeUser.getLastName();

        lbl_user.setText(fullName);
        lbl_currentTrack.setText("");
        update_tbl_userPlaylists();

       /* tbl_searchResults.setOnMouseClicked((MouseEvent event) -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                System.out.println(tbl_searchResults.getSelectionModel().getSelectedItem().getSongId());
            }
        });*/

    }

    public void click_checkid(javafx.event.ActionEvent event)throws Exception{
        Song selectedSong = tbl_searchResults.getSelectionModel().getSelectedItem();
        System.out.println(selectedSong.getSongLocation());
       //playTrack(selectedSong);
    }

    public void press_btn_search(javafx.event.ActionEvent event)throws SQLException, ClassNotFoundException{

        ObservableList<Song> songsAvailable = SongDAO.buildSongData();
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

    public void press_btn_newPlaylist (javafx.event.ActionEvent event)throws Exception{

        String plTitle = namePlaylistPrompt();

        String strActiveUserId = Integer.toString(activeUser.getUserId());
        System.out.println(plTitle);
        PlaylistDAO.insertPlaylist(plTitle, strActiveUserId);

        update_tbl_userPlaylists();

    }

    public String namePlaylistPrompt(){
        String playlistName = "nothing to see";

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

    public void update_tbl_playlistTracks() throws SQLException, ClassNotFoundException{

        selectedPlaylist = tbl_userPlaylists.getSelectionModel().getSelectedItem();

        ObservableList<Song> playlistSongs = SongDAO.buildSongDataFromPlaylist(selectedPlaylist.getPlaylistId());

        col_title.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        col_artist.setCellValueFactory(cellData -> cellData.getValue().artistProperty());
        col_album.setCellValueFactory(cellData -> cellData.getValue().albumProperty());
        tbl_playlistTracks.setItems(playlistSongs);


    }

    public void press_btn_addSong() throws Exception{

    }



}
