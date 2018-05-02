package sample;
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
        Connection con = connectDB();
        ResultSet rs;
        PreparedStatement st = con.prepareStatement(query);
        rs = st.executeQuery();

        return rs;
    }

    /**
     * to run an update query such as update, delete
     *
     * @param query custom query
     * @throws SQLException throws an exception if an error occurs
     */
    public static void runQuery(String query) throws SQLException {
        Connection con = connectDB();

        PreparedStatement st = con.prepareStatement(query);

        st.executeUpdate();

    }

    public static void dbExecuteUpdate(String sqlStmt) throws SQLException, ClassNotFoundException {
        //Declare statement as null
        Statement stmt = null;
        try {
            //Connect to DB (Establish Oracle Connection)
            connectDB();
            //Create Statement
            stmt = connection.createStatement();
            //Run executeUpdate operation with given sql statement
            stmt.executeUpdate(sqlStmt);
        } catch (SQLException e) {
            System.out.println("Problem occurred at executeUpdate operation : " + e);
            throw e;
        } finally {
            if (stmt != null) {
                //Close statement
                stmt.close();
            }
            //Close connection
            disconnectDB();
        }

    }
}