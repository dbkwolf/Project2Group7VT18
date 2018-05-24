package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import model.Playlist;
import model.PlaylistDAO;
import model.User;
import model.UserDAO;

import javax.swing.*;
import java.sql.SQLException;
import java.util.Optional;

import static controllers.LogInController.activeUser;

public class AdminController extends MainController{

    public JFXButton btn_adminBack;
    public AnchorPane apn_adminAnchorpane;
    public JFXTextField txt_searchEmail;
    public JFXTextField txt_searchUsername;
    public JFXTextField txt_searchUserId;
    public TableView<User> tbl_users;
    public TableColumn<User, Number> col_user_id;
    public TableColumn<User, String> col_username;
    public TableColumn<User, String> col_email;
    public TableColumn<User, String> col_first;
    public TableColumn<User, String> col_last;
    public TableColumn<User, String> col_isAdmin;
    public Label lbl_inputError;
    public JFXButton btn_deleteUser;
    public JFXButton btn_grantPermissions;
    private String allUsersQuery = "SELECT * FROM g7musicappdb.users" ;


    public void press_btn_adminBack (javafx.event.ActionEvent event)throws Exception{
        change_Scene_to(event,"../scenes/home.fxml");
    }

    @FXML
    void initialize() throws SQLException, ClassNotFoundException{

        update_tbl_users(allUsersQuery);
        control_user_input();

    }

    public void update_tbl_users(String query) throws SQLException {

        ObservableList<User> userData = UserDAO.buildUserData(query);


        col_user_id.setCellValueFactory(cellData -> cellData.getValue().userIdProperty());
        col_username.setCellValueFactory(cellData ->cellData.getValue().usernameProperty());
        col_first.setCellValueFactory(cellData ->cellData.getValue().firstNameProperty());
        col_last.setCellValueFactory(cellData ->cellData.getValue().lastNameProperty());
        col_email.setCellValueFactory(cellData ->cellData.getValue().emailProperty());
        col_isAdmin.setCellValueFactory(cellData ->cellData.getValue().strAdminLevelProperty());

        tbl_users.setItems(userData);





    }

    /**
     * Determines the appropriate query string for search in the DB
     * @param event: Action event = click button
     * @throws Exception
     */
    public void press_btn_searchUsers(javafx.event.ActionEvent event)throws Exception{

        String searchQuery;

        /*
         * IF-Statement for determining which text field is not empty
         * search query string gets updated accordingly
         */
        if (!txt_searchEmail.getText().isEmpty()){

            String email = txt_searchEmail.getText();
            searchQuery ="SELECT * FROM g7musicappdb.users WHERE users.email like '" + email +"'";

        }else if (!txt_searchUserId.getText().isEmpty()){

            String id = txt_searchUserId.getText();
            searchQuery ="SELECT * FROM g7musicappdb.users WHERE users.user_id = " + id;

        }else if (!txt_searchUsername.getText().isEmpty()){

            String username = txt_searchUsername.getText();
            searchQuery ="SELECT * FROM g7musicappdb.users WHERE users.username like '" + username +"'";

        }else{
            searchQuery = allUsersQuery; //in case all fields are empty
        }

        update_tbl_users(searchQuery);


    }

    /**
     * controls the user's search input
     */
    public void control_user_input() {

        /*
         * allows for only one textfield to be enabled at a time
         */
            txt_searchEmail.disableProperty().bind(
                    Bindings.isNotEmpty(txt_searchUserId.textProperty())
                            .or(Bindings.isNotEmpty(txt_searchUsername.textProperty()))
            );

            txt_searchUserId.disableProperty().bind(
                    Bindings.isNotEmpty(txt_searchEmail.textProperty())
                            .or(Bindings.isNotEmpty(txt_searchUsername.textProperty()))
            );

            txt_searchUsername.disableProperty().bind(
                    Bindings.isNotEmpty(txt_searchEmail.textProperty())
                            .or(Bindings.isNotEmpty(txt_searchUserId.textProperty()))
            );


        /*
         * allows only numeric values in the txt_searchUserId TextField
         * makes error label visible accordingly
         */
        txt_searchUserId.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.matches("[0-9]+") && !newValue.isEmpty()){
                lbl_inputError.setVisible(true);
            }else{
                lbl_inputError.setVisible(false);
            }

        });

        /*
         * binds the delete button to be disabled when selection in user table is null
         * i.e. : enables the delete button only when an item has been selected
         */
        btn_deleteUser.disableProperty().bind(
                Bindings.isNull(tbl_users.getSelectionModel().selectedItemProperty())
        );

        /*
         * binds the permissions button to be disabled when selection in user table is null
         * i.e. : enables the premissions button only when an item has been selected
         */
        btn_grantPermissions.disableProperty().bind(
                Bindings.isNull(tbl_users.getSelectionModel().selectedItemProperty())

        );

        /*
         *binds the button text according to whether the selected user is an admin or not
         */
        tbl_users.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
       if ( newSelection == null || !newSelection.adminLevelProperty().get() ){

                 btn_grantPermissions.setText("Grant Admin Permissions");

            }else  {
                btn_grantPermissions.setText("Revoke Admin Permissions");

            }
        });

    }

    public void deleteUserFromDB()throws SQLException{


        User selectedUser = tbl_users.getSelectionModel().getSelectedItem();
        System.out.println(selectedUser.getUserId());



        int selectedUserId = selectedUser.getUserId();

        if (selectedUserId == activeUser.getUserId()){
            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setContentText("Delete operation not possible: the selected user has an active session open.");

            alert.show();


            }else{

            UserDAO.deleteUser(selectedUserId);

        }

    }

    public void press_btn_deleteUser(javafx.event.ActionEvent event)throws Exception{

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setContentText("Are you sure you want to delete this user?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {

            try {
                deleteUserFromDB();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            alert.close();

        }

    update_tbl_users(allUsersQuery);


    }


    public void press_btn_grantPermissions(javafx.event.ActionEvent event)throws Exception{

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setContentText("Are you sure you want to update this user's permissions?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {

            try {
                updatePermissions();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            alert.close();

        }

        update_tbl_users(allUsersQuery);

    }

    public void updatePermissions()throws SQLException{
        User selectedUser = tbl_users.getSelectionModel().getSelectedItem();
        System.out.println(selectedUser.getUserId());

        String aux;

        if(selectedUser.adminLevelProperty().get()){
            aux = "0";
        }else{
            aux = "1";
        }

        int selectedUserId = selectedUser.getUserId();

        String query = " UPDATE g7musicappdb.users SET admin_level = "+ aux + " WHERE user_id = " + selectedUserId + ";";



        if (selectedUserId == activeUser.getUserId()){
            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setContentText("Update failed: the selected user has an active session open.");

            alert.show();


        }else{

            UserDAO.updateUser(selectedUserId, query);

        }

    }
}
