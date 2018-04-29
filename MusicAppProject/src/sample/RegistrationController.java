package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RegistrationController {

    public void press_btn_back(ActionEvent event) throws Exception {

        Parent loginParent = FXMLLoader.load(getClass().getResource("logInScene.fxml"));
        Scene loginScene = new Scene(loginParent);

        //this line gets the stage info
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(loginScene);
        window.show();

    }

    public void press_btn_registerNew(ActionEvent event) throws Exception{



        System.out.print("new user registered");
    }
}
