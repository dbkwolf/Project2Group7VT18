package controllers;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import database_utilities.UserDAO;
import javafx.event.ActionEvent;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.SQLException;

public class SignUpController extends MainController{

    public JFXTextField txt_usernameSignUp;
    public JFXTextField txt_firstName;
    public JFXTextField txt_lastName;
    
    public JFXTextField txt_repeatPasswordRegister;
    public JFXTextField txt_email;
    public JFXTextField txt_repeatEmail;
    public JFXPasswordField txt_passwordSignUp;
    public JFXTextField txt_authorizationCode;


    public void press_btn_back(ActionEvent event) throws Exception {

        change_Scene_to(event, "../scenes/login.fxml");

    }

    public void register()throws Exception{
        String username = txt_usernameSignUp.getText();
        String firstName= txt_firstName.getText();
        String lastName = txt_lastName.getText();
        String password = txt_passwordSignUp.getText();
        String email = txt_email.getText();

        UserDAO.insertUser(username, firstName, lastName, password, email);

    }
    public void press_btn_registerNew(ActionEvent event) throws Exception{

        try {
            register();
        } catch (SQLException e) {
            System.out.print("Problem occurred while inserting user " + e);
            throw e;
        }


        System.out.print("new user registered");
        change_Scene_to(event, "../scenes/login.fxml");
    }

    private boolean isNotEmpty(JFXTextField field, String input){
        //needs to be done
        return true;
    }


    public void press_btn_registerNewAdmin(ActionEvent event) throws Exception{

        String code = txt_authorizationCode.getText();

    }
}
