package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConection {

    private static DBConection dbConnection;
    private static Connection connection;

    public DBConection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/thogakade","root","Kavija@");

    }

    public static DBConection getInstance() throws ClassNotFoundException, SQLException {
        return dbConnection!=null ? dbConnection : (dbConnection = new DBConection());
    }
    public Connection getConnection(){
        return connection;
    }
}
