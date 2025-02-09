package itstep.learning.dal.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.dal.dto.User;
import itstep.learning.models.UserSignUpFormModel;
import itstep.learning.services.db.DbService;
import itstep.learning.services.kdf.KdfService;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.logging.Logger;



@Singleton
public class UserDao {

    private final Connection connection;
    private final Logger logger;

    @Inject
    public UserDao(DbService dbService, Logger logger) throws SQLException {
        this.connection = dbService.getConnection();
        this.logger=logger;
    }

    public User addUser ( UserSignUpFormModel userModel) {
        User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setName(userModel.getName());
        user.setEmail(userModel.getEmail());
       // user.setPhone( userModel.getPhones());

        String sql = "INSERT INTO users(user_id, name, email, phone)"
                + " VALUES (?, ?, ?, ?)";
        try (PreparedStatement prep = this.connection.prepareStatement( sql)) {
            prep.setString(1, user.getUserId().toString());
            prep.setString(2, user.getName());
            prep.setString(3, user.getEmail());
            prep.setString(4, user.getPhone());


        } catch (SQLException ex) {

            logger.warning("UserDao::addUsers " + ex.getMessage());
            return null;

        }
        sql = "INSERT INTO users_access (user_access_id, user_id, role_id, login, salt, dk)"
                + " VALUES (UUID(), ?, 'guest' ,?, ?, ?)";
        try (PreparedStatement prep = this.connection.prepareStatement( sql)) {
            prep.setString(1, user.getUserId().toString());
            prep.setString(2, user.getEmail());
            String salt = UUID.randomUUID().toString().substring(0, 16);
            prep.setString(3, salt);
            //prep.setString(4, kdfService.dk (userModel.getPassword(), salt));
            this.connection.setAutoCommit(false);
            prep.executeUpdate();
            this.connection.commit();

        } catch (SQLException ex) {

            logger.warning("UserDao::addUsers " + ex.getMessage());
            try {this.connection.rollback();} catch (SQLException ex1) {}
            return null;

        }
        return user;
    }



    public boolean installTables() {

        return installUsers()&&installUsersAccess();

    }


    private boolean installUsersAccess() {

        String sql = "CREATE TABLE IF NOT EXISTS users("
                + "user_access_id  CHAR(36)     PRIMARY KEY DEFAULT( UUID() ),"
                + "user_id  CHAR(36)       NOT NULL,"
                + "login    VARCHAR(128)   NOT NULL,"
                + "salt   VARCHAR(16)      NOT  NULL,"
                + "role_id   VARCHAR(16)   NOT  NULL,"
                + "dk   VARCHAR(20)        NOT NULL,"
                + "UNIQUE(login)"
                + ") Engine = InnoDB, DEFAULT CHARSET = utf8mb4";

        try (Statement statement = connection.createStatement()) {

            statement.executeUpdate(sql);
            logger.info("installUsersAccess Ok");
            return true;

        } catch (SQLException ex) {

            logger.warning("UserDao::installUsersAccess " + ex.getCause());
            return false;

        }

    }



    private boolean installUsers() {

        String sql = "CREATE TABLE IF NOT EXISTS users("
                + "userId  CHAR(36)     PRIMARY KEY DEFAULT( UUID() ),"
                + "name    VARCHAR(128) NOT NULL,"
                + "email   VARCHAR(256)     NULL,"
                + "phone   VARCHAR(32)      NULL"
                + ") Engine = InnoDB, DEFAULT CHARSET = utf8mb4";

        try (Statement statement = connection.createStatement()) {

            statement.executeUpdate(sql);
            logger.info("installUsers Ok");
            return true;

        } catch (SQLException ex) {

            logger.warning("UserDao::installUsers " + ex.getCause());
            return false;

        }

    }

}