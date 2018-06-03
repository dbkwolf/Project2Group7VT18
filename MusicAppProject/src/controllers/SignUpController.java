package controllers;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import model.User;
import model.UserDAO;
import javafx.event.ActionEvent;

import java.sql.SQLException;
import java.util.Objects;

public class SignUpController extends MainController{

    public JFXTextField txt_usernameSignUp;
    public JFXTextField txt_firstName;
    public JFXTextField txt_lastName;
    
    public JFXTextField txt_repeatPasswordRegister;
    public JFXTextField txt_email;
    public JFXTextField txt_repeatEmail;
    public JFXPasswordField txt_passwordSignUp;
    public JFXTextField txt_authorizationCode;



    boolean adminLevel;


    public void press_btn_back(ActionEvent event) throws Exception {

        change_Scene_to(event, "../scenes/login.fxml");

    }

    public void register(boolean isAdmin)throws Exception{
        String username = txt_usernameSignUp.getText();
        String firstName = txt_firstName.getText();
        String lastName = txt_lastName.getText();
        String password = txt_passwordSignUp.getText();
        String email = txt_email.getText();
        String adminLevel;

        if (!isAdmin) {
            adminLevel ="0";
        }else{
            adminLevel ="1";

        }

        UserDAO.insertUser(username, firstName, lastName, password, email, adminLevel);

    }
    public void press_btn_registerNew(ActionEvent event) throws Exception{

        try {
            register(false);
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


    public void press_btn_registerAdmin(ActionEvent event) throws Exception{

        String inputAuCode = txt_authorizationCode.getText();

        if(UserDAO.findAuCode(inputAuCode)==null){
            System.out.println("wrong authorization code - registration failed.");
        }else{
            try {
                register(true);
            } catch (SQLException e) {
                System.out.print("Problem occurred while inserting user " + e);
                throw e;
            }
        }


        System.out.println("new admin user registered");

        change_Scene_to(event, "../scenes/login.fxml");

    }


}
