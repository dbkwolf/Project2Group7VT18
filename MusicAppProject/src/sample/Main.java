package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Music App");

        /*------------- GRID PANE-----------------*/
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        /*------------- BUTTONS-----------------*/
        Button btnRegister = new Button("Register");
        HBox hbBtnRegister = new HBox(10);
        hbBtnRegister.setAlignment(Pos.BOTTOM_LEFT);
        hbBtnRegister.getChildren().add(btnRegister);
        grid.add(hbBtnRegister, 1, 4);

        Button btnSignIn = new Button("Sign in");
        HBox hbBtnSignIn = new HBox(10);
        hbBtnSignIn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtnSignIn.getChildren().add(btnSignIn);
        grid.add(hbBtnSignIn, 1, 4);


        /*-------------SCENE-----------------*/
        Text scenetitle = new Text("Welcome");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));


        grid.add(scenetitle, 0, 0, 2, 1);

        /*------------- LABEL & TEXT Fields for USER NAME-----------------*/
        Label userName = new Label("User Name:");
        grid.add(userName, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        /*------------- LABEL & TEXT Fields for PASSWORD-----------------*/
        Label pw = new Label("Password:");
        grid.add(pw, 0, 2);

        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);
        Scene scene = new Scene(grid, 300, 275);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
