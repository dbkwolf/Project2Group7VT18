package util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.*;


public class HttpGet {


    /**
     * Send request to a server that download audio from youtube-dl supported site and returns link to a mp3 file
     *
     * @param URL from a youtube-dl supported site like youtube.com (this is the youtube link)
     * @return url location from server (this is the link to be stored in the database)
     * @throws Exception throws an exception if an error occurs
     */
    public static String getDownload(String URL) throws Exception {

        String url = "http://project2.duckdns.org:1234/index.php?link=" + URL;

        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());
        return parseJSON(response.toString());
    }

    private  static String parseJSON(String jsonString){

        JSONObject obj = new JSONObject(jsonString);
        String url = obj.getString("file");
        System.out.println(url);

        return url;

    }

}
