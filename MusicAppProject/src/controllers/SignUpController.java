package controllers;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import database_utilities.UserDAO;
import javafx.event.ActionEvent;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.Optional;

public class SignUpController {

    public JFXTextField txt_usernameSignUp;
    public JFXTextField txt_firstName;
    public JFXTextField txt_lastName;
    
    public JFXTextField txt_repeatPasswordRegister;
    public JFXTextField txt_email;
    public JFXTextField txt_repeatEmail;
    public JFXPasswordField txt_passwordSignUp;

    public void press_btn_back(ActionEvent event) throws Exception {

        Parent loginParent = FXMLLoader.load(getClass().getResource("../scenes/login.fxml"));
        Scene loginScene = new Scene(loginParent);

        //this line gets the stage info
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(loginScene);
        window.show();


    }

    public void press_btn_registerNew(ActionEvent event) throws Exception{


        String username = txt_usernameSignUp.getText();
        String firstName= txt_firstName.getText();
        String lastName = txt_lastName.getText();
        String password = txt_passwordSignUp.getText();
        String email = txt_email.getText();


        try {
            UserDAO.insertUser(username, firstName, lastName, password, email);
            System.out.print("Employee inserted!");
        } catch (SQLException e) {
            System.out.print("Problem occurred while inserting employee " + e);
            throw e;
        }


        System.out.print("new user registered");


        Parent loginParent = FXMLLoader.load(getClass().getResource("../scenes/login.fxml"));
        Scene loginScene = new Scene(loginParent);

        //this line gets the stage info
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(loginScene);
        window.show();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText("New user registered! Please, log in.");

        alert.showAndWait();

    }

    private boolean isNotEmpty(JFXTextField field, String input){
        //needs to be done
        return true;
    }


}
