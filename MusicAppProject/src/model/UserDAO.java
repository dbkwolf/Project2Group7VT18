package model;

import util.DatabaseUtility;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    //*************************************
    //INSERT a user
    //*************************************
    public static void insertUser (String username, String firstName, String lastName,String password,String email, String adminLevel) throws SQLException, ClassNotFoundException {
        //Declare a INSERT statement


        String insertQuery ="INSERT INTO g7musicappdb.users (user_id, username, first_name, last_name, password, email, admin_level) VALUES ('0','" + username+ "', '"+firstName+"', '"+lastName+"', '"+password+"','"+email+"','"+adminLevel+"'); ";

        //Execute INSERT operation
        try {
            DatabaseUtility.runQuery(insertQuery);
        } catch (SQLException e) {
            System.out.print("Error occurred while INSERT Operation: " + e);
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
            System.out.println("error while searching user");
            throw e;
        }
    }

        public static User getUserFromResultSet(ResultSet rs) throws  SQLException{
            User user = null;
            if (rs.next()){
                user = new User(rs.getInt("user_id"), rs.getString("username"),
                        rs.getString("first_name"), rs.getString("last_name"),
                        rs.getString("password"),rs.getString("email"), rs.getBoolean("admin_level"));

            }
            return user;
    }

    public static String findAuCode(String input) throws SQLException, ClassNotFoundException {

        String searchQuery = "SELECT * FROM g7musicappdb.authorization_codes WHERE code_sequence like '" + input +"';";
        String auCode=null;
        try {
            ResultSet rsCode = DatabaseUtility.dbExecuteQuery(searchQuery);
            if (rsCode.next()) {

                auCode = rsCode.getString("code_sequence");

            }
            return auCode;
        } catch (SQLException e) {
            System.out.println("error while searching user");
            throw e;
        }
    }

}

