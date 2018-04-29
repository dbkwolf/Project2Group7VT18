package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LogInController {



    public void press_btn_register(ActionEvent event) throws Exception {

        Parent registerParent = FXMLLoader.load(getClass().getResource("registerScene.fxml"));
        Scene registerScene = new Scene(registerParent);

        //this line gets the stage info
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(registerScene);
        window.show();
    }


}
