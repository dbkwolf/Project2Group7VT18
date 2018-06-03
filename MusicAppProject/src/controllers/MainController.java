package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MainController {

    /**
     * Changes stage from current to next
     * @param event: Action event, e.g. Click Button event
     * @param path: location of next scene
     * @throws Exception -
     */
    public void change_Scene_to(ActionEvent event, String path)throws Exception{
        Parent parent = FXMLLoader.load(getClass().getResource(path));
        Scene nextScene = new Scene(parent );


        //this line gets the stage info
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(nextScene);

       centerStage(window, window.getWidth(), window.getHeight());
        window.show();
    }

    /**
     * centers stage in middle of user's screen
     * @param stage: stage to be centered
     * @param width: width of stage
     * @param height: height of stage
     */
    private void centerStage(Stage stage, double width, double height) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - width) / 2);
        stage.setY((screenBounds.getHeight() - height) / 2);
    }
}
