package util;









import model.User;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;


import javax.sql.DataSource;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.*;

public class DatabaseUtility {

    private static Connection connection;
    private static final String Driver = "com.mysql.jdbc.Driver";
    private static final String ConnectionString = "jdbc:mysql://den1.mysql6.gear.host?autoReconnect=true&useSSL=false";
    private static final String user = "g7musicappdb";
    private static final String pwd = "Nx7h__Wn32hK";
    private static GenericObjectPool gPool = null;
    public static DataSource dataSource = null;
    public DatabaseUtility() {

        System.out.println("\nthis would happen everytime a DBUtility object is created\n");

        try {
            dataSource = setUpPool();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }




    /**
     * to load the database base driver
     *
     * @return a database connection
     * @throws SQLException throws an exception if an error occurs
     */

    public static Connection connectDB() throws SQLException {


        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
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
     * to run an update query such as update, delete
     *
     * @param query custom query
     * @throws SQLException throws an exception if an error occurs
     */
    public static void runQuery(String query) throws SQLException {

        System.out.println("DatabaseUtility.runQuery");

        try(Connection con = getInstance().getConnection()) {
            printDbStatus();


            PreparedStatement statement = con.prepareStatement(query);

            statement.executeUpdate();

        }
    }

    public static void dbExecuteUpdate(String query) throws SQLException {
        PreparedStatement pstmt = null;
        try(Connection con = getInstance().getConnection()) {
            printDbStatus();
            pstmt = con.prepareStatement(query);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Problem occurred at executeUpdate operation : " + e);
            throw e;
        } finally {
            if (pstmt != null) {
                pstmt.close();
            }

        }

    }



    public static DataSource getInstance() {
        System.out.println("DatabaseUtility.getInstance " +System.currentTimeMillis());
        if (dataSource == null) {
            dataSource = new PoolingDataSource();
            printDbStatus();
        }
        return dataSource;
    }

    @SuppressWarnings("unused")
    public static DataSource setUpPool() throws Exception {
        System.out.println("DatabaseUtility.setUpPool");
        System.out.println("driver initializing");
        Class.forName(Driver);
        System.out.println("driver done");

        System.out.println("creating instance of genericpoolobj");
        // Creates an Instance of GenericObjectPool That Holds Our Pool of Connections Object!
        gPool = new GenericObjectPool();
        gPool.setMaxActive(5);

        System.out.println("creating instance of connection factory");
        // Creates a ConnectionFactory Object Which Will Be Use by the Pool to Create the Connection Object!
        ConnectionFactory cf = new DriverManagerConnectionFactory(ConnectionString, user, pwd);

        System.out.println("creating instance of poolable connection");
        // Creates a PoolableConnectionFactory That Will Wraps the Connection Object Created by the ConnectionFactory to Add Object Pooling Functionality!
        PoolableConnectionFactory pcf = new PoolableConnectionFactory(cf, gPool, null, null, false, true); //cf,gPool, null, null, false, true
        return new PoolingDataSource(gPool);

        /*OracleDataSource ods = new OracleDataSource();
java.util.Properties prop = new java.util.Properties();
prop.setProperty("MinLimit", "2");
prop.setProperty("MaxLimit", "10");
String url = "jdbc:oracle:oci8:@//xxx.xxx.xxx.xxx:1521/orcl";
ods.setURL(url);
ods.setUser("USER");
ods.setPassword("PWD");
ods.setConnectionCachingEnabled(true);
ods.setConnectionCacheProperties (prop);
ods.setConnectionCacheName("ImplicitCache01");*/
    }


    public static GenericObjectPool getConnectionPool() {
        return gPool;
    }

    // This Method Is Used To Print The Connection Pool Status
    private static void printDbStatus() {
        System.out.println("\nMax.: " + getConnectionPool().getMaxActive() + "; Active: " + getConnectionPool().getNumActive() + "; Idle: " + getConnectionPool().getNumIdle() + " - " + System.currentTimeMillis());
    }

    /**public static void main(String[] args) {
        ResultSet rsObj = null;
        Connection connObj = null;
        PreparedStatement pstmtObj = null;
        ConnectionPool jdbcObj = new ConnectionPool();
        try {
            DataSource dataSource = jdbcObj.setUpPool();
            jdbcObj.printDbStatus();

            // Performing Database Operation!
            System.out.println("\n=====Making A New Connection Object For Db Transaction=====\n");
            connObj = dataSource.getConnection();
            jdbcObj.printDbStatus();

            pstmtObj = connObj.prepareStatement("SELECT * FROM technical_editors");
            rsObj = pstmtObj.executeQuery();
            while (rsObj.next()) {
                System.out.println("Username: " + rsObj.getString("tech_username"));
            }
            System.out.println("\n=====Releasing Connection Object To Pool=====\n");
        } catch(Exception sqlException) {
            sqlException.printStackTrace();
        } finally {
            try {
                // Closing ResultSet Object
                if(rsObj != null) {
                    rsObj.close();
                }
                // Closing PreparedStatement Object
                if(pstmtObj != null) {
                    pstmtObj.close();
                }
                // Closing Connection Object
                if(connObj != null) {
                    connObj.close();
                }
            } catch(Exception sqlException) {
                sqlException.printStackTrace();
            }
        }
        jdbcObj.printDbStatus();

     ResultSet rsObj = null;
     Connection connObj = null;
     PreparedStatement pstmtObj = null;
     CachedRowSet cachedRows = RowSetProvider.newFactory().createCachedRowSet();
     try {
     DataSource dataSource = setUpPool();
     printDbStatus();

     // Performing Database Operation!
     System.out.println("\n=====Making A New Connection Object For Db Transaction=====\n");
     connObj = dataSource.getConnection();
     printDbStatus();

     pstmtObj = connObj.prepareStatement(query);
     rsObj = pstmtObj.executeQuery();
     cachedRows.populate(rsObj);

     System.out.println("\n=====Releasing Connection Object To Pool=====\n");
     }
     catch (Exception sqlException) {
     sqlException.printStackTrace();
     } finally {
     try {
     // Closing ResultSet Object
     if (rsObj != null) {
     rsObj.close();
     }
     // Closing PreparedStatement Object
     if (pstmtObj != null) {
     pstmtObj.close();
     }
     // Closing Connection Object
     if (connObj != null) {
     connObj.close();
     }
     }
     catch (Exception sqlException) {
     sqlException.printStackTrace();
     }
     }
     printDbStatus();*/
    public static ResultSet dbExecuteQuery(String query) throws SQLException {
        System.out.println("DatabaseUtility.dbExecuteQuery");

        ResultSet results = null;

        PreparedStatement pstmt = null;

        CachedRowSet cachedRows = RowSetProvider.newFactory().createCachedRowSet();
        System.out.println(System.currentTimeMillis());
        Connection con = null;

        try  {
             con = getInstance().getConnection();
            System.out.println(System.currentTimeMillis());

            printDbStatus();


            pstmt = con.prepareStatement(query);
            results = pstmt.executeQuery();
            cachedRows.populate(results);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Problem occurred at executeUpdate operation : " + e);
        } finally {

            try {

                // Closing ResultSet Object

                if(results != null) {

                    results.close();

                }

                // Closing PreparedStatement Object

                if(pstmt != null) {
                    pstmt.close();

                }

                // Closing Connection Object

                if(con != null) {

                    con.close();

                }

            } catch(Exception sqlException) {

                sqlException.printStackTrace();

            }

    }

        return cachedRows;

    }




}
