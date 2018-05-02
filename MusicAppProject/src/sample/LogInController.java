package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class LogInController {



    public void press_btn_register(ActionEvent event) throws Exception {

        Parent registerParent = FXMLLoader.load(getClass().getResource("signup.fxml"));
        Scene registerScene = new Scene(registerParent);

        //this line gets the stage info
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(registerScene);
        window.show();
    }

    public void signUpPressedButton(ActionEvent event) throws IOException {

//        Stage stage = new Stage();
//        Parent root = FXMLLoader.load(getClass().getResource("viewapp.signup.fxml"));
//        stage.setTitle("New User");
//        stage.setScene(new Scene(root));
//        stage.show();

        //to open in the same stage
        Node node = (Node)event.getSource();
        Stage stage = (Stage)node.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/musicapp/viewapp/signup.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("New User");


    }

    public void closedPressedButton(){
        System.exit(1);

    }



    @FXML
    public void loginPressedButton(){

    }

    @FXML
    public void minScreen(MouseEvent event){
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }
}
