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
import itstep.learning.services.config.ConfigService;


@Singleton
public class AccessTokenDao {

    private final Logger logger;
    private final DbService dbService;
    private final ConfigService configService;

    private int tokenLifeTime;

    @Inject
    public AccessTokenDao(ConfigService configService, Logger logger, DbService dbService) {
        this.logger = logger;
        this.dbService = dbService;
        this.configService = configService;
        this.tokenLifeTime = 0;

    }

    public boolean installTables() {

        String sql = "CREATE TABLE IF NOT EXISTS access_tokens("
                + "access_token_id  CHAR(36)     PRIMARY KEY DEFAULT( UUID() ),"
                + "user_access_id  CHAR(36)       NOT NULL,"
                + "issued_at    DATETIME          NOT NULL,"
                + "expires_at   DATETIME           NULL"
                + ") Engine = InnoDB, DEFAULT CHARSET = utf8mb4";

        try (Statement statement = dbService.getConnection().createStatement()) {

            statement.executeUpdate(sql);
            dbService.getConnection().commit();
            logger.info("AccessTokenDao: installTables Ok");
            return true;

        } catch (SQLException ex) {

            logger.log(Level.WARNING, "AccessTokenDao::installTables {0} sql: {1}",
                    new Object[] { ex.getMessage(), sql });
            return false;

        }

    }

    public AccessToken create(UserAccess userAccess) {

        if (userAccess == null)
            return null;
        if (tokenLifeTime == 0) {
            tokenLifeTime = configService.getValue("token.lifetime").getAsInt() * 1000;// miliseconds

        }

        AccessToken token = new AccessToken();
        token.setAccessTokenId(UUID.randomUUID());
        token.setUserAccessId(userAccess.getUserAccessId());
        Date date = new Date();
        token.setIssuedAt(date);
        token.setExpiresAt(new Date(date.getTime() + tokenLifeTime));

        String sql = "INSERT INTO access_tokens(access_token_id, user_access_id, "
                + "issued_at, expires_at) VALUES(?,?,?,?)";

        try (PreparedStatement prep = dbService.getConnection().prepareStatement(sql)) {

            prep.setString(1, token.getAccessTokenId().toString());
            prep.setString(2, token.getUserAccessId().toString());
            prep.setTimestamp(3, new Timestamp(token.getIssuedAt().getTime()));
            prep.setTimestamp(4, new Timestamp(token.getExpiresAt().getTime()));
            prep.executeUpdate();
            dbService.getConnection().commit();

        } catch (Exception e) {

            logger.log(Level.WARNING, "AccessTokenDao::Create {0} sql :'{1}'", new Object[] { e.getMessage(), sql });

            return null;
        }
        return token;
    }

    public UserAccess getUserAccess(String bearereCredentials) {
        UUID accessTokenId;
        try {
            accessTokenId = UUID.fromString(bearereCredentials);
        } catch (Exception ignore) {
            return null;
        }

        String sql = String.format("SELECT * FROM access_tokens a "
                + " JOIN user_access ua ON a.user_access_id = ua.user_access_id "
                + " WHERE a.access_token_id='%s' "
                + " AND a.expires_at > CURRENT_TIMESTAMP", accessTokenId.toString());

        try (Statement stmt = dbService.getConnection().createStatement()) {

            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {

                return UserAccess.fromResultSet(rs);
            }

        } catch (Exception e) {

            logger.log(Level.WARNING, "AccessTokenDao::getUserAccess {0} sql :'{1}'",
                    new Object[] { e.getMessage(), sql });

        }
        return null;
    }

    public boolean cencel(AccessToken token) {

        return true;
    }

    public AccessToken getTAccessToken(String userId) {

        String sql = String.format("SELECT * FROM access_tokens "
                + " WHERE user_access_id='%s' "
                + " AND expires_at > CURRENT_TIMESTAMP", userId);

        try (Statement stmt = dbService.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                return AccessToken.FromResultSet(rs);
            }

        } catch (Exception e) {

            logger.log(Level.WARNING, "AccessTokenDao::getTAccessToken {0} sql :'{1}'",
                    new Object[] { e.getMessage(), sql });

        }

        return null;
    }

    public boolean prolonge(String userId) {

        java.util.Date currentdate = new Date();
        java.util.Date expires = new Date();

        String sql = String.format("SELECT expires_at FROM access_tokens "
                + " WHERE user_access_id='%s' "
                + " AND expires_at > CURRENT_TIMESTAMP", userId);
        try (Statement stmt = dbService.getConnection().createStatement()) {

            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                expires = rs.getTimestamp(1);
            }

            if (expires.getTime() > currentdate.getTime()) {

                sql = String.format("UPDATE access_tokens SET expires_at= ? "
                        +" WHERE user_access_id='%s' ORDER BY expires_at DESC LIMIT 1", userId);

                try (PreparedStatement prep = dbService.getConnection().prepareStatement(sql)) {
                    prep.setTimestamp(1, new Timestamp(new Date(currentdate.getTime()).getTime() + tokenLifeTime));
                    prep.executeUpdate();
                    dbService.getConnection().commit();
                } catch (SQLException ex) {

                    logger.log(Level.WARNING, "AccessTokenDao::prolonge {0}", ex.getMessage());

                }
                logger.log(Level.INFO,
                        "token is update, new expires :" + new Timestamp(currentdate.getTime() + tokenLifeTime));
                return true;
            } else {
                logger.log(Level.INFO, "User need new token");
                return false;

            }

        } catch (Exception e) {

            logger.log(Level.WARNING, "AccessTokenDao::prolonge {0} sql :'{1}'",
                    new Object[] { e.getMessage(), sql });

        }

        return false;
    }
}
