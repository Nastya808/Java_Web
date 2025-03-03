package itstep.learning.dal.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.dal.dto.AccessToken;
import itstep.learning.dal.dto.UserAccess;
import itstep.learning.services.db.DbService;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class AccessTokenDao {
    private final DbService dbService;
    private final Logger logger;

    @Inject
    public AccessTokenDao(DbService dbService, Logger logger) {
        this.dbService = dbService;
        this.logger = logger;
    }

    public AccessToken create(UserAccess userAccess) {
        if (userAccess == null) return null;

        AccessToken token = new AccessToken();
        token.setAccessTokenId(UUID.randomUUID());
        token.setUserAccessId(userAccess.getUserAccessId());
        Date date = new Date();
        token.setIssuedAt(date);
        token.setExpiresAt(new Date(date.getTime() + 1000 * 60 * 60 * 24)); // 24 hours

        String sql = "INSERT INTO access_tokens(access_token_id, user_access_id, issued_at, expires_at) VALUES(?,?,?,?)";
        try (PreparedStatement prep = dbService.getConnection().prepareStatement(sql)) {
            prep.setString(1, token.getAccessTokenId().toString());
            prep.setString(2, token.getUserAccessId().toString());
            prep.setTimestamp(3, new Timestamp(token.getIssuedAt().getTime()));
            prep.setTimestamp(4, new Timestamp(token.getExpiresAt().getTime()));
            prep.executeUpdate();
            dbService.getConnection().commit();
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "AccessTokenDao::create {0} sql: '{1}'", new Object[]{ex.getMessage(), sql});
            return null;
        }
        return token;
    }

    public UserAccess getUserAccess(String bearerCredentials) {
        UUID accessTokenId;
        try {
            accessTokenId = UUID.fromString(bearerCredentials);
        } catch (Exception ignore) {
            return null;
        }

        String sql = String.format(
                "SELECT * FROM access_tokens a " +
                        "JOIN users_access ua ON a.user_access_id = ua.user_access_id " +
                        "WHERE a.access_token_id = '%s' " +
                        "AND a.expires_at > CURRENT_TIMESTAMP",
                accessTokenId.toString());

        try (Statement statement = dbService.getConnection().createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            if (rs.next()) {
                return UserAccess.fromResultSet(rs);
            }
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "AccessTokenDao::getUserAccess {0} sql: '{1}'", new Object[]{ex.getMessage(), sql});
        }
        return null;
    }

    public boolean cancel(AccessToken token) {
        return true;
    }

    public boolean prolong(AccessToken token) {
        return true;
    }

    public boolean installTables() {
        String sql = "CREATE TABLE IF NOT EXISTS access_tokens (" +
                "access_token_id  CHAR(36)   PRIMARY KEY DEFAULT( UUID() )," +
                "user_access_id   CHAR(36)   NOT NULL," +
                "issued_at        DATETIME   NOT NULL," +
                "expires_at       DATETIME       NULL" +
                ") Engine = InnoDB, DEFAULT CHARSET = utf8mb4";

        try (Statement statement = dbService.getConnection().createStatement()) {
            statement.executeUpdate(sql);
            dbService.getConnection().commit();
            logger.info("AccessTokenDao::installTables OK");
            return true;
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "AccessTokenDao::installTables {0} sql: '{1}'", new Object[]{ex.getMessage(), sql});
        }
        return false;
    }

    public boolean deleteUser(UUID userId) {
        String deleteAccessTokensSql = "DELETE FROM access_tokens WHERE user_access_id IN (SELECT user_access_id FROM users_access WHERE user_id = ?)";
        String deleteUserAccessSql = "DELETE FROM users_access WHERE user_id = ?";
        String deleteUserSql = "DELETE FROM users WHERE user_id = ?";

        try (PreparedStatement deleteAccessTokensStmt = dbService.getConnection().prepareStatement(deleteAccessTokensSql);
             PreparedStatement deleteUserAccessStmt = dbService.getConnection().prepareStatement(deleteUserAccessSql);
             PreparedStatement deleteUserStmt = dbService.getConnection().prepareStatement(deleteUserSql)) {

            deleteAccessTokensStmt.setString(1, userId.toString());
            deleteAccessTokensStmt.executeUpdate();

            deleteUserAccessStmt.setString(1, userId.toString());
            deleteUserAccessStmt.executeUpdate();

            deleteUserStmt.setString(1, userId.toString());
            deleteUserStmt.executeUpdate();

            dbService.getConnection().commit();
            logger.info("AccessTokenDao::deleteUser OK");
            return true;
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "AccessTokenDao::deleteUser {0}", new Object[]{ex.getMessage()});
            return false;
        }
    }
}