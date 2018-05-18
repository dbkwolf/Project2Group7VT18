package controllers;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;

public class Forgotpasswordcontroller  {
    public TextField txt_Email;
    public JFXButton btn_Verificationlink;
    public JFXButton btn_back;
    public void press_btn_back(ActionEvent event) throws Exception {

        Parent loginParent = FXMLLoader.load(getClass().getResource("../scenes/login.fxml"));
        Scene loginScene = new Scene(loginParent);

        //this line gets the stage info
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(loginScene);
        window.show();

    }

    public void press_btn_Verificationlink(ActionEvent event)throws Exception{

    }




}
