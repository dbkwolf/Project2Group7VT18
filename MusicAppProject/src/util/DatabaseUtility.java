package util;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.*;

public class DatabaseUtility {

    private static Connection connection;
    private static final String Driver = "com.mysql.jdbc.Driver";
    private static final String ConnectionString = "jdbc:mysql://den1.mysql6.gear.host?autoReconnect=true&useSSL=false";
    private static final String user = "g7musicappdb";
    private static final String pwd = "Nx7h__Wn32hK";


    public DatabaseUtility() {

    }

    /**
     * to load the database base driver
     *
     * @return a database connection
     * @throws SQLException throws an exception if an error occurs
     */

    public static Connection connectDB() throws SQLException {

        try {
            Class.forName(Driver);
            System.out.println("Driver loaded");
        } catch (ClassNotFoundException ex) {
            System.out.println(ex.getMessage());
        }

        try {
            connection = DriverManager.getConnection(ConnectionString, user, pwd);
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console" + e);
            e.printStackTrace();
            throw e;
        }

        return connection;

    }

    /**
     * to close database connection
     *
     * @throws SQLException throws an exception if an error occurs
     */
    public static void disconnectDB() throws SQLException {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * to get a result set of a query
     *
     * @param query custom query
     * @return a result set of custom query
     * @throws SQLException throws an exception if an error occurs
     */
    public static ResultSet executeQuery(String query) throws SQLException {
        Connection connection = connectDB();
        ResultSet results;
        PreparedStatement st = connection.prepareStatement(query);
        results = st.executeQuery();

        return results;
    }

    /**
     * to run an update query such as update, delete
     *
     * @param query custom query
     * @throws SQLException throws an exception if an error occurs
     */
    public static void runQuery(String query) throws SQLException {
        Connection con = connectDB();

        PreparedStatement statement = con.prepareStatement(query);

        statement.executeUpdate();
        disconnectDB();

    }

    public static void dbExecuteUpdate(String query) throws SQLException {
        Statement statement = null;
        try {
            connectDB();
            statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println("Problem occurred at executeUpdate operation : " + e);
            throw e;
        } finally {
            if (statement != null) {
                statement.close();
            }
            disconnectDB();
        }

    }

    public static ResultSet dbExecuteQuery(String query) throws SQLException {
        Statement statement = null;
        ResultSet results = null;
        CachedRowSet cachedRows = RowSetProvider.newFactory().createCachedRowSet();

        try {
            connectDB();
            System.out.println("Select statement: " + query + "\n");
            statement = connection.createStatement();
            results = statement.executeQuery(query);
            cachedRows.populate(results);
        } catch (SQLException e) {
            System.out.println("Problem occurred at executeQuery: " + e);
            throw e;
        } finally {
            if (results != null) {
                results.close();
            }
            if (statement != null) {
                statement.close();
            }
            disconnectDB();
        }
        return cachedRows;
    }
}