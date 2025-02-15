package itstep.learning.dal.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.dal.dto.User;
import itstep.learning.models.UserSignUpFormModel;
import itstep.learning.services.db.DbService;
import itstep.learning.services.kdf.KdfService;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;


@Singleton
public class UserDao {
    private final Connection connection;
    private final Logger logger;
    private final KdfService kdfService;
    private final DbService dbService;

    @Inject
    public UserDao(KdfService kdfService, DbService dbService, Logger logger) throws SQLException {
        this.dbService = dbService;
        this.connection = dbService.getConnection();
        this.logger = logger;
        this.kdfService = kdfService;
    }

    public boolean installTables() {
        return this.installUsers() && this.installUsersAccess() && this.installRole();
    }

    public User addUser(UserSignUpFormModel userModel) {
        User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setEmil(userModel.getEmail());
        user.setName(userModel.getName());
        user.setPhone(userModel.getPhone());
        String sql = "INSERT INTO users (userId, name, email, phone)VALUES(?, ?, ?, ?)";

        try {
            PreparedStatement prep = this.connection.prepareStatement(sql);

            try {
                this.connection.setAutoCommit(false);
                prep.setString(1, user.getUserId().toString());
                prep.setString(2, user.getName());
                prep.setString(3, user.getEmil());
                prep.setString(4, user.getPhone());
                prep.executeUpdate();
            } catch (Throwable var13) {
                if (prep != null) {
                    try {
                        prep.close();
                    } catch (Throwable var10) {
                        var13.addSuppressed(var10);
                    }
                }

                throw var13;
            }

            if (prep != null) {
                prep.close();
            }
        } catch (SQLException ex) {
            this.logger.warning("UserDao::addUser 1 " + ex.getMessage());

            try {
                this.connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }

        sql = "INSERT INTO user_access (user_access_id, user_id, role_id, login, salt, dk )VALUES(UUID(),?,'guest', ?, ?, ?)";

        try {
            PreparedStatement prep = this.dbService.getConnection().prepareStatement(sql);

            try {
                prep.setString(1, user.getUserId().toString());
                prep.setString(2, user.getEmil());
                String salt = UUID.randomUUID().toString().substring(0, 16);
                prep.setString(3, salt);
                prep.setString(4, this.kdfService.dk(userModel.getPassword(), salt));
                prep.executeUpdate();
                this.dbService.getConnection().commit();
            } catch (Throwable var11) {
                if (prep != null) {
                    try {
                        prep.close();
                    } catch (Throwable var8) {
                        var11.addSuppressed(var8);
                    }
                }

                throw var11;
            }

            if (prep != null) {
                prep.close();
            }

            return user;
        } catch (SQLException ex) {
            this.logger.warning("UserDao::addUser 2 " + ex.getMessage());

            try {
                this.connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public User autorize(String login, String pass) {
        String sql = "select * from user_access ua join users u on ua.user_id=u.userId where ua.login = ? ";

        try {
            PreparedStatement prep = this.dbService.getConnection().prepareStatement(sql);

            User var7;
            label56: {
                try {
                    prep.setString(1, login);
                    ResultSet rs = prep.executeQuery();
                    if (rs.next()) {
                        String dk = this.kdfService.dk(pass, rs.getString("salt"));
                        if (Objects.equals(dk, rs.getString("dk"))) {
                            var7 = User.froResulSet(rs);
                            break label56;
                        }
                    }
                } catch (Throwable var9) {
                    if (prep != null) {
                        try {
                            prep.close();
                        } catch (Throwable var8) {
                            var9.addSuppressed(var8);
                        }
                    }

                    throw var9;
                }

                if (prep != null) {
                    prep.close();
                }

                return null;
            }

            if (prep != null) {
                prep.close();
            }

            return var7;
        } catch (SQLException ex) {
            this.logger.log(Level.WARNING, "UserDao::autorize {0}", ex.getMessage());
            return null;
        }
    }

    private boolean installRole() {
        String sql = "CREATE TABLE IF NOT EXISTS users_roles(rolesId  CHAR(36)     PRIMARY KEY DEFAULT( UUID() ),user_id  CHAR(36)       NOT NULL,role   VARCHAR(16)      NOT NULL,canCreate INT DEFAULT(false),canRead   INT DEFAULT(false),canUpdate INT DEFAULT(false),canDelete INT DEFAULT(false),description   VARCHAR(256)     NULL) Engine = InnoDB, DEFAULT CHARSET = utf8mb4";

        try {
            Statement statement = this.connection.createStatement();

            boolean var3;
            try {
                statement.executeUpdate(sql);
                this.logger.info("installRole Ok");
                var3 = true;
            } catch (Throwable var6) {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (Throwable var5) {
                        var6.addSuppressed(var5);
                    }
                }

                throw var6;
            }

            if (statement != null) {
                statement.close();
            }

            return var3;
        } catch (SQLException ex) {
            this.logger.warning("UserDao::installRole " + ex.getCause());
            return false;
        }
    }

    private boolean installUsersAccess() {
        String sql = "CREATE TABLE IF NOT EXISTS user_access(user_access_id  CHAR(36)     PRIMARY KEY DEFAULT( UUID() ),user_id  CHAR(36)       NOT NULL,login    VARCHAR(128)   NOT NULL,salt   VARCHAR(16)      NOT  NULL,role_id   VARCHAR(16)   NOT  NULL,dk   VARCHAR(20)        NOT NULL,UNIQUE(login)) Engine = InnoDB, DEFAULT CHARSET = utf8mb4";

        try {
            Statement statement = this.connection.createStatement();

            boolean var3;
            try {
                statement.executeUpdate(sql);
                this.logger.info("installUsersAccess Ok");
                var3 = true;
            } catch (Throwable var6) {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (Throwable var5) {
                        var6.addSuppressed(var5);
                    }
                }

                throw var6;
            }

            if (statement != null) {
                statement.close();
            }

            return var3;
        } catch (SQLException ex) {
            this.logger.warning("UserDao::installUsersAccess " + ex.getCause());
            return false;
        }
    }

    private boolean installUsers() {
        String sql = "CREATE TABLE IF NOT EXISTS users(userId  CHAR(36)     PRIMARY KEY DEFAULT( UUID() ),name    VARCHAR(128) NOT NULL,email   VARCHAR(256)     NULL,phone   VARCHAR(32)      NULL) Engine = InnoDB, DEFAULT CHARSET = utf8mb4";

        try {
            Statement statement = this.connection.createStatement();

            boolean var3;
            try {
                statement.executeUpdate(sql);
                this.logger.info("installUsers Ok");
                var3 = true;
            } catch (Throwable var6) {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (Throwable var5) {
                        var6.addSuppressed(var5);
                    }
                }

                throw var6;
            }

            if (statement != null) {
                statement.close();
            }

            return var3;
        } catch (SQLException ex) {
            this.logger.warning("UserDao::installUsers " + ex.getCause());
            return false;
        }
    }
}
