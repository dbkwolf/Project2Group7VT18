package controllers;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.SongLink;
import util.Auth;
import util.Search;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class YouTubeSearchController extends MainController{

    //if import classes not here, download.

    public JFXTextField txt_search;

    private static final String PROPERTIES_FILENAME = "youtube.properties";
    private static YouTube youtube;
    private static final long NUMBER_OF_VIDEOS_RETURNED = 25;
    public TableView<SongLink> tbl_results;
    public TableColumn<SongLink, String> col_titles;
    private SongLink selectedSongLink;

    public void press_btn_search() throws Exception{

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
                populateList(searchResultList.iterator(), queryTerm);
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

    public void populateList(Iterator<SearchResult> iteratorSearchResults, String query) {

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

    public void press_btn_back(ActionEvent event) throws Exception{
        change_Scene_to(event, "../scenes/home.fxml");
    }




}
