package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.User;
import model.UserDAO;


import java.sql.SQLException;
import java.util.Objects;

public class LogInController extends MainController {


    public JFXPasswordField txt_passwordLogIn;
    public static User activeUser;
    public JFXTextField txt_usernameLogIn;
    public Label lbl_error;
    public JFXButton btn_help;

    public void press_btn_register(ActionEvent event) throws Exception {

        change_Scene_to(event,"../scenes/signup.fxml");
    }

    public void press_btn_help(ActionEvent event) throws Exception {
        change_Scene_to(event,"../scenes/Help.fxml");
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
                 change_Scene_to(event,"../scenes/home.fxml");
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
