package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import database_utilities.UserDAO;
import javafx.concurrent.Service;
import javafx.util.Duration;

import javax.swing.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class LogInController {


    public JFXPasswordField txt_passwordLogIn;
    private User activeUser;
    public JFXTextField txt_usernameLogIn;
    public Label lbl_error;
    public JFXButton btn_help;

    public void press_btn_register(ActionEvent event) throws Exception {

        Parent registerParent = FXMLLoader.load(getClass().getResource("../scenes/signup.fxml"));
        Scene registerScene = new Scene(registerParent);

        //this line gets the stage info
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(registerScene);
        window.show();
    }
    public void press_btn_help(ActionEvent event) throws Exception {

        Parent registerParent = FXMLLoader.load(getClass().getResource("../scenes/Help.fxml"));
        Scene registerScene = new Scene(registerParent);

        //this line gets the stage info
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(registerScene);
        window.show();
    }




    public void press_btn_login (ActionEvent event) throws Exception {
        System.out.print("trying to log in");




        try {
            activeUser = UserDAO.findUser(txt_usernameLogIn.getText());
        }catch(SQLException e){
            throw e;
        }

         if (activeUser == null){
             lbl_error.setVisible(true);

        }else{
             if(Objects.equals(activeUser.getPassword(), txt_passwordLogIn.getText())){
                 System.out.print("login successful");
                 lbl_error.setVisible(false);
                 Parent registerParent = FXMLLoader.load(getClass().getResource("../scenes/home.fxml"));
                 Scene registerScene = new Scene(registerParent);

                 //this line gets the stage info
                 Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                 window.setScene(registerScene);
                 window.show();
             }else{
                 lbl_error.setVisible(true);
             }

         }

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
