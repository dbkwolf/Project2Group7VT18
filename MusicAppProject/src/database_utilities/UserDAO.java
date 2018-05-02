package database_utilities;

import controllers.User;

import database_utilities.DatabaseUtility;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    //*************************************
    //INSERT a user
    //*************************************
    public static void insertUser (String username, String firstName, String lastName,String password,String email) throws SQLException, ClassNotFoundException {
        //Declare a DELETE statement


        String insertQuery ="INSERT INTO g7musicappdb.users (user_id, username, first_name, last_name, password, email) VALUES ('0','" + username+ "', '"+firstName+"', '"+lastName+"', '"+password+"','"+email+"'); ";

        //Execute DELETE operation
        try {
            DatabaseUtility.runQuery(insertQuery);
        } catch (SQLException e) {
            System.out.print("Error occurred while DELETE Operation: " + e);
            throw e;
        }
    }

    public static User findUser(String username) throws SQLException, ClassNotFoundException {

        String searchQuery = "SELECT * FROM g7musicappdb.users WHERE username like '" + username+"';";

        try {
            ResultSet rsUser = DatabaseUtility.dbExecuteQuery(searchQuery);

            User user = getUserFromResultSet(rsUser);
            return user;
        } catch (SQLException e) {
            System.out.println("error while seraching user");
            throw e;
        }
    }

        public static User getUserFromResultSet(ResultSet rs) throws  SQLException{
            User user = null;
            if (rs.next()){
                user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
            }
            return user;
    }


}

