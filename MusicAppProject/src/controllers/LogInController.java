package controllers;

import com.jfoenix.controls.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.User;
import model.UserDAO;
import util.DatabaseUtility;


import java.sql.SQLException;
import java.util.Objects;

public class LogInController extends MainController {


    public JFXPasswordField txt_passwordLogIn;
    public static User activeUser;
    public JFXTextField txt_usernameLogIn;
    public Label lbl_error;
    public JFXButton btn_help;
    public JFXButton btn_forgotPswd;


    @FXML private StackPane stackPane;

    public DatabaseUtility dbutil = new DatabaseUtility();

    public void press_btn_register(ActionEvent event) throws Exception {

        change_Scene_to(event,"../scenes/signup.fxml");
    }

    public void press_btn_help(ActionEvent event) throws Exception {

            JFXDialogLayout content = new JFXDialogLayout();
            content.setHeading(new Text("HELP TABLE - Click on DONE to close"));
            content.setBody(new Text("" +
                                             " • USERNAME\nType your UserName or create a new account to have access to the MusicApp. \n" +
                                             " • PASSWORD\nType the password of your account. \n" +
                                             " • LOGIN\nType your user credentials and press the Login Button to access your Home Screen. \n" +
                                             " • FORGOT PASSWORD\nForgot your password? All yu need to do is click on this button and recover your credentials. \n" +
                                             " • SIGN UP\nEnjoy the Music App functionalities creating a new account on Sign Up button. \n" +
                                             " • MINIMIZE/ CLOSE\nTo the right top view of the App you can find easily minimize/ close buttons."));
            content.setStyle("-fx-background-color: #6495ED");
            JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
//        Bounds bounds = dialog.localToScreen(dialog.getBoundsInLocal());
//        bounds.contains(200, 94);
            JFXButton done = new JFXButton("DONE");
            done.setOnAction(event1 -> dialog.close());
            //loginButton.setOnMouseEntered((event) -> loginButton.setStyle("-fx-background-color: gray; -fx-background-radius: 100"));
            done.setStyle("-fx-background-color: #000080; -fx-text-fill: #fffafa; -fx-background-radius: 100");
            done.setOnMouseEntered(event1 -> done.setStyle("-fx-background-color: gray; -fx-background-radius: 100"));
            done.setOnMouseExited(event1 -> done.setStyle("-fx-background-color: #000080; -fx-text-fill: #fffafa; -fx-background-radius: 100"));

            content.setActions(done);
            dialog.show();

    }

    public void press_btn_forgotpswd(ActionEvent event) throws Exception {
        change_Scene_to(event, "../scenes/Forgotpassword.fxml");
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
    public void minScreen(MouseEvent event){
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }
}
