package itstep.learning.services.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.google.inject.Singleton;

@Singleton
public class MySqlDbService implements DbService {

    private Connection connection;

    @Override
    public Connection getConnection() throws SQLException {

        if (connection == null) {

            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            String connectionString = "jdbc:mysql://localhost:3306/javaDB";
            connection = DriverManager.getConnection(connectionString, "root", "");

        }
        return connection;
    }

}