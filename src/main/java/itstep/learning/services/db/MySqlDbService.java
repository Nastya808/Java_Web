package itstep.learning.services.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import itstep.learning.services.config.ConfigService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class MySqlDbService implements DbService {

    private Connection connection;
    private final ConfigService configService;
    private final Logger logger;

    @Inject
    public MySqlDbService(Logger logger, ConfigService configService) {

        this.configService = configService;
        this.logger = logger;

    }

    @Override
    public Connection getConnection() throws SQLException {

        if (connection == null) {

            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());

            try {
                String connectionString = String.format("jdbc:%s://%s:%d/%s?%s",
                        configService.getValue("db.MySql.dbms").getAsString(),
                        configService.getValue("db.MySql.host").getAsString(),
                        configService.getValue("db.MySql.port").getAsInt(),
                        configService.getValue("db.MySql.schema").getAsString(),
                        configService.getValue("db.MySql.params").getAsString());

                connection = DriverManager.getConnection(connectionString,
                        configService.getValue("db.MySql.user").getAsString(),
                        configService.getValue("db.MySql.password").getAsString());
                connection.setAutoCommit(configService.getValue("db.MySql.autocommit").getAsBoolean());

            } catch (Exception e) {
                logger.log(Level.WARNING, "MySqlDbService::getConnection, Error string connection", e);
                return null;
            }
        }
        return connection;
    }

}




//            String connectionString = "jdbc:mysql://localhost:3306/javaDB";
//            connection = DriverManager.getConnection(connectionString, "root", "");