package controllers;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Pair;
import model.Album;
import model.Artist;
import model.SongDAO;
import model.SongLink;
import util.Auth;
import util.HttpGet;
import util.Search;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import static controllers.LogInController.activeUser;

public class YouTubeSearchController extends MainController{

    //if import classes not here, download.

    public JFXTextField txt_search;

    private static final String PROPERTIES_FILENAME = "youtube.properties";
    private static YouTube youtube;
    private static final long NUMBER_OF_VIDEOS_RETURNED = 25;
    public TableView<SongLink> tbl_results;
    public TableColumn<SongLink, String> col_titles;
    public StackPane stpn_addtoDB;

    private SongLink selectedSongLink;



    public void press_btn_searchyt() throws Exception{

        Properties properties = new Properties();
        try {
            InputStream in = Search.class.getResourceAsStream("/util/" + PROPERTIES_FILENAME);
            properties.load(in);

        } catch (IOException e) {
            System.err.println("Error reading " + PROPERTIES_FILENAME + ": " + e.getCause()
                                       + " : " + e.getMessage());
            System.exit(1);
        }

        try {
            // This object is used to make YouTube Data API requests. The last
            // argument is required, but since we don't need anything
            // initialized when the HttpRequest is initialized, we override
            // the interface and provide a no-op function.
            youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("youtube-cmdline-search-sample").build();

            // Prompt the user to enter a query term.
            String queryTerm = read_search_field();

            // Define the API request for retrieving search results.
            YouTube.Search.List search = youtube.search().list("id,snippet");

            // Set your developer key from the {{ Google Cloud Console }} for
            // non-authenticated requests. See:
            // {{ https://cloud.google.com/console }}
            String apiKey = properties.getProperty("youtube.apikey");
            search.setKey(apiKey);
            search.setQ(queryTerm);

            // Restrict the search results to only include videos. See:
            // https://developers.google.com/youtube/v3/docs/search/list#type
            search.setType("video");

            // To increase efficiency, only retrieve the fields that the
            // application uses.
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

            // Call the API and print results.
            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();
            if (searchResultList != null) {
                populateList(searchResultList.iterator());
            }
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                                       + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }


    }

    public String read_search_field() throws Exception{

        String inputQuery = "";


        inputQuery = txt_search.getText();

        if (inputQuery.length() < 1) {
            // Use the string "YouTube Developers Live" as a default.
            inputQuery = "YouTube Developers Live";
        }
        return inputQuery;

    }

    public void populateList(Iterator<SearchResult> iteratorSearchResults) {

        ObservableList<SongLink> ytData = FXCollections.observableArrayList();


        if (!iteratorSearchResults.hasNext()) {
            System.out.println(" no match");
        }

        while (iteratorSearchResults.hasNext()) {

            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();

            // Confirm that the result represents a video. Otherwise, the
            // item will not contain a video ID.
            if (rId.getKind().equals("youtube#video")) {
                Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();

                SongLink newSongLink = new SongLink(singleVideo.getSnippet().getTitle(),rId.getVideoId());

                System.out.println(newSongLink.getTitle() + ", " + newSongLink.getUrl());

                ytData.add(newSongLink);

            }

        }

        col_titles.setCellValueFactory(cellData -> cellData.getValue().titleProperty());

        tbl_results.setItems(ytData);

    }

    public void getLinkFromList(){

        selectedSongLink = tbl_results.getSelectionModel().getSelectedItem();
        System.out.println(selectedSongLink.getUrl());



    }

    public void  press_btn_addtoDB(ActionEvent event) throws Exception{

            JFXDialogLayout content = new JFXDialogLayout();
            //content.setHeading(new Text("User Profile Settings"));





            JFXDialog dialog = new JFXDialog(stpn_addtoDB, content, JFXDialog.DialogTransition.BOTTOM );


            // Create the username and password labels and fields.
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(10, 10, 10, 10));

            grid.getColumnConstraints().addAll(new ColumnConstraints(110 ), new ColumnConstraints(110 ));
            //grid.setStyle("-fx-background-color: greenyellow;");


            TextField txt_Title = new TextField();
            txt_Title.setPromptText("Title");
            TextField txt_Artist = new TextField();
            txt_Artist.setPromptText("Artist");

            TextField txt_album = new TextField();
            txt_album.setPromptText("Album");

            Label lbl_title = new Label("Add Title");
            Label lbl_artist = new Label("Add Artist");
            Label lbl_album = new Label("Add Album");
            lbl_title.setStyle("-fx-text-fill: #fffafa ");
            lbl_artist.setStyle("-fx-text-fill: #fffafa");
            lbl_album.setStyle("-fx-text-fill: #fffafa");

            SongLink selectedTitle = tbl_results.getSelectionModel().getSelectedItem();
            System.out.println(selectedTitle);

            Label lbl_selection  = new Label("Nothing Selected");


                if (selectedTitle != null){
                    lbl_selection.setText(selectedTitle.getTitle());
                }

            lbl_selection.setStyle("-fx-text-fill: #fffafa");

            grid.add(lbl_selection, 0,0, 2,1);
            grid.add(lbl_title, 0, 1);
            grid.add(txt_Title, 1, 1);

            grid.add(lbl_artist, 0, 2);
            grid.add(txt_Artist, 1, 2);

            grid.add(lbl_album, 0, 3);
            grid.add(txt_album, 1, 3);


            JFXButton btn_cancel = new JFXButton("Cancel");
            JFXButton btn_add = new JFXButton("Add Song");
            btn_cancel.setOnAction(event1 -> dialog.close());
            btn_add.setOnAction(event2-> addsongtoDB(event, txt_Title.getText(), txt_Artist.getText(), txt_album.getText(), selectedTitle.getUrl()));


            btn_cancel.setStyle("-fx-background-color: #000080; -fx-text-fill: #fffafa; -fx-background-radius: 100");
            btn_cancel.hoverProperty().addListener((observable, oldValue, newValue) -> {
                if(newValue){
                    btn_cancel.setStyle("-fx-background-color: #3733cb; -fx-text-fill: #fffafa; -fx-background-radius: 100");
                }else{
                    btn_cancel.setStyle("-fx-background-color: #000080; -fx-text-fill: #fffafa; -fx-background-radius: 100");
                }

            });

            btn_add.setStyle("-fx-background-color: #027232; -fx-text-fill: #fffafa; -fx-background-radius: 100");
            btn_add.hoverProperty().addListener((observable, oldValue, newValue) -> {
                if(newValue){
                    btn_add.setStyle("-fx-background-color: #02a149;-fx-text-fill: #fffafa; -fx-background-radius: 100");
                }else{
                    btn_add.setStyle("-fx-background-color: #027232; -fx-text-fill: #fffafa; -fx-background-radius: 100");
                }

            });

            grid.add(btn_add, 1,4);
            grid.add(btn_cancel, 0, 4);

            grid.setStyle("-fx-background-color: #1d1d1d");

            dialog.setContent(grid);

            btn_add.disableProperty().bind(

                    Bindings.isEmpty(txt_Title.textProperty())
                    .or(Bindings.isEmpty(txt_Artist.textProperty())
                    .or(Bindings.isEmpty(txt_album.textProperty())))

            );



            dialog.show();


    }


    private void addsongtoDB(ActionEvent event, String title, String artistName, String albumTitle, String url)  {

        int duration=0;

        try {
            duration = HttpGet.getDownload(url).getDuration();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Artist artist = null;
        try {
            artist = SongDAO.searchArtist(artistName);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }



        if (artist!=null){


            Album album = null;

            try {
                album = SongDAO.searchAlbum(albumTitle,artist.getArtistId());
            }
            catch (SQLException e) {
                e.printStackTrace();
            }

            //String dialogBody = "Is this the correct artist?\n\n"+ artist.getArtistName();

            if (album != null){
                 //dialogBody = dialogBody + "\n\n And Is this the correct album?\n\n"+ album.getTitle();

                try {
                    SongDAO.addSongToDB(title, artist.getArtistId(), album.getAlbumId(), url, duration);
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }

            }else{
                try {
                    SongDAO.addSongToDB(title, artist.getArtistId(), albumTitle, url, duration);
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }



        }else{

            try {
                SongDAO.addSongToDB(title, artistName, albumTitle, url, duration);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }

        }






         JFXDialogLayout content = new JFXDialogLayout();
            content.setHeading(new Text("INSERT SUCCESFUL"));
            content.setBody(new Text(" The Song has been added to the DB"));

            content.setStyle("-fx-background-color: #3c3f41; -fx-text-fill: white");


            JFXDialog dialog = new JFXDialog(stpn_addtoDB,content,JFXDialog.DialogTransition.RIGHT);

            JFXButton btnYes = new JFXButton("Ok");


            btnYes.setOnAction(event1 -> dialog.close());


            btnYes.setStyle("-fx-background-color: #000080; -fx-text-fill: #fffafa; -fx-background-radius: 100");
            btnYes.setOnMouseEntered(event1 -> btnYes.setStyle("-fx-background-color: gray; -fx-background-radius: 100"));
            btnYes.setOnMouseExited(event1 -> btnYes.setStyle("-fx-background-color: #000080; -fx-text-fill: #fffafa; -fx-background-radius: 100"));



            content.setActions(btnYes);
            dialog.show();


    }



}
