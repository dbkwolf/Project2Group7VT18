package sample;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.DatabaseUtility;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    //*************************************
    //INSERT a user
    //*************************************
    public static void insertUser (String username, String firstName, String lastName,String password,String email) throws SQLException, ClassNotFoundException {
        //Declare a DELETE statement


        String newQuery ="INSERT INTO g7musicappdb.users (user_id, username, first_name, last_name, password, email) VALUES ('0','" + username+ "', '"+firstName+"', '"+lastName+"', '"+password+"','"+email+"'); ";

        //Execute DELETE operation
        try {
            DatabaseUtility.runQuery(newQuery);
        } catch (SQLException e) {
            System.out.print("Error occurred while DELETE Operation: " + e);
            throw e;
        }
    }
}
