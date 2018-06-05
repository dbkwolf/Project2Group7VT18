package controllers;

import com.jfoenix.controls.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.SendEmail;
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


    public void press_btn_forgotPassword(ActionEvent event) throws Exception{

        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("Forgot Password? - Get a Recovery E-mail"));



        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.TOP);


        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));

        grid.getColumnConstraints().addAll(new ColumnConstraints(200 ), new ColumnConstraints(100 ));




        TextField txt_Email = new TextField();
        txt_Email.setPromptText("sample@email.com");


        Label lbl_Email = new Label("Please Enter your E-mail");

        lbl_Email.setStyle("-fx-text-fill: #fffafa");
        Label lbl_errorEmail = new Label("Couldn't find user in Database");
        lbl_errorEmail.setVisible(false);



        grid.add(lbl_Email, 0, 0);
        grid.add(lbl_errorEmail, 0,1);
        grid.add(txt_Email, 0, 2);



        JFXButton btn_cancel = new JFXButton("cancel");
        JFXButton btn_sendRecoveryEmail = new JFXButton("Send Recovery E-mail");
        btn_cancel.setOnAction(event1 -> dialog.close());
        btn_sendRecoveryEmail.setOnAction(event2-> sendRecoveryEmail(event, txt_Email.getText(), dialog, lbl_errorEmail));


        btn_cancel.setStyle("-fx-background-color: #000080; -fx-text-fill: #fffafa; -fx-background-radius: 100");
        btn_cancel.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                btn_cancel.setStyle("-fx-background-color: #3733cb; -fx-text-fill: #fffafa; -fx-background-radius: 100");
            }else{
                btn_cancel.setStyle("-fx-background-color: #000080; -fx-text-fill: #fffafa; -fx-background-radius: 100");
            }

        });

        btn_sendRecoveryEmail.setStyle("-fx-background-color: #027232; -fx-text-fill: #fffafa; -fx-background-radius: 100");
        btn_sendRecoveryEmail.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                btn_sendRecoveryEmail.setStyle("-fx-background-color: #02a149;-fx-text-fill: #fffafa; -fx-background-radius: 100");
            }else{
                btn_sendRecoveryEmail.setStyle("-fx-background-color: #027232; -fx-text-fill: #fffafa; -fx-background-radius: 100");
            }

        });

        grid.add(btn_sendRecoveryEmail, 0,4);
        grid.add(btn_cancel, 1, 4);

        grid.setStyle("-fx-background-color: #1d1d1d");

        dialog.setContent(grid);

        txt_Email.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.isEmpty()){

                btn_sendRecoveryEmail.setDisable(false);
            }else{

                btn_sendRecoveryEmail.setDisable(true);
            }

        });



        dialog.show();


    }

    private void sendRecoveryEmail(ActionEvent event, String recipientEmail, JFXDialog dialog, Label lbl) {


        try {
            if(UserDAO.emailExistsInDB(recipientEmail)){

                new SendEmail(recipientEmail);


                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(10, 10, 10, 10));

                grid.getColumnConstraints().addAll(new ColumnConstraints(150), new ColumnConstraints(200));

                Label lbl_confirmation = new Label("A recovery E-mail has been sent to " + recipientEmail);
                lbl_confirmation.setStyle("-fx-text-fill: white");

                grid.add(lbl_confirmation, 0, 0, 2, 1);

                JFXButton btn_close = new JFXButton("close");

                btn_close.setOnAction(event1 -> dialog.close());

                btn_close.setStyle("-fx-background-color: #000080; -fx-text-fill: #fffafa; -fx-background-radius: 100");
                btn_close.hoverProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue){
                        btn_close.setStyle("-fx-background-color: #3733cb; -fx-text-fill: #fffafa; -fx-background-radius: 100");
                    } else {
                        btn_close.setStyle("-fx-background-color: #000080; -fx-text-fill: #fffafa; -fx-background-radius: 100");
                    }

                });

                grid.add(btn_close, 0, 1);
                grid.setStyle("-fx-background-color: #1d1d1d");
                dialog.setContent(grid);

            }else{
                lbl.setStyle("-fx-text-fill: red");
                lbl.setVisible(true);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
