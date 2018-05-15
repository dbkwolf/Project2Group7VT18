package controllers;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class adminhomecontroller {
    public JFXButton btn_addsongs;
    public JFXButton btn_Removesongs;
    public JFXButton btn_editsongs;
    public JFXButton btn_cancel;

    public void press_btn_addsongs(ActionEvent event) throws Exception {

        Parent registerParent = FXMLLoader.load(getClass().getResource("../scenes/adminaddsongs.fxml"));
        Scene registerScene = new Scene(registerParent);

        //this line gets the stage info
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(registerScene);
        window.show();


    }

    public void press_btn_Removesongs(ActionEvent event) throws Exception {

        Parent registerParent = FXMLLoader.load(getClass().getResource("../scenes/adminremovesongs.fxml"));
        Scene registerScene = new Scene(registerParent);

        //this line gets the stage info
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(registerScene);
        window.show();
    }

    public void press_btn_editsongs(ActionEvent event) throws Exception {

        Parent registerParent = FXMLLoader.load(getClass().getResource("../scenes/admineditsongs.fxml"));
        Scene registerScene = new Scene(registerParent);

        //this line gets the stage info
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(registerScene);
        window.show();
    }

    public void press_btn_cancel(ActionEvent event) throws Exception {

        Parent registerParent = FXMLLoader.load(getClass().getResource("../scenes/login.fxml"));
        Scene registerScene = new Scene(registerParent);

        //this line gets the stage info
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(registerScene);
        window.show();
    }
}