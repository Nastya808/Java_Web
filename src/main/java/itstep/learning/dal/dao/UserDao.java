package itstep.learning.dal.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.dal.dto.User;
import itstep.learning.dal.dto.UserAccess;
import itstep.learning.models.UserSignUpFormModel;
import itstep.learning.services.db.DbService;
import itstep.learning.services.kdf.KdfService;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;



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

    public User getUserById(String id) {

        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (Exception e) {
            logger.log(Level.WARNING, "UserDao::getUserByid Parse error {0}", id);
            return null;
        }
        return getUserById(uuid);

    }

    public CompletableFuture deleteAsync(User user) {

        String sql = String.format(
                "UPDATE users SET delete_moment=CURRENT_TIMESTAMP,"
                        + "name='', email='', city='', age=0, dateOfB=NULL, money=0, phone='' WHERE userId = '%s'",
                user.getUserId().toString());

        String sql1 = String.format("UPDATE user_access SET ua_delete_dt=CURRENT_TIMESTAMP,"
                        + " login=UUID() WHERE user_id = '%s'",
                user.getUserId().toString());

        CompletableFuture task1 = CompletableFuture.runAsync(() -> {

            try (Statement stmt = dbService.getConnection().createStatement()) {
                stmt.executeUpdate(sql);

            } catch (SQLException ex) {

                logger.log(Level.WARNING, "UserDao::Delete user {0},{1}",
                        new Object[] { ex.getMessage(), sql });
                try {
                    dbService.getConnection().rollback();
                } catch (SQLException ignore) {
                }
                ;
            }

        });

        CompletableFuture task2 = CompletableFuture.runAsync(() -> {

            try (Statement stmt = dbService.getConnection().createStatement()) {
                stmt.executeUpdate(sql1);

            } catch (SQLException ex) {

                logger.log(Level.WARNING, "UserDao::Delete access {0},{1}",
                        new Object[] { ex.getMessage(), sql1 });
                try {
                    dbService.getConnection().rollback();
                } catch (SQLException ignore) {
                }
                ;
            }

        });

        return CompletableFuture.allOf(task1, task2).thenRun(() -> {

            try {
                dbService.getConnection().commit();
            } catch (SQLException ignore) {

            }

        });

    }

    public boolean upDate(User user) {

        Map<String, Object> data = new HashMap<>();
        if (user.getName() != null) {
            data.put("name", user.getName());
        }
        if (user.getPhone() != null) {
            data.put("phone", user.getPhone());
        }
        if (user.getCity() != null) {
            data.put("city", user.getCity());
        }
        if (user.getDofb() != null) {
            data.put("dateOfB", user.getDofb());
        }
        if (user.getAge() != 0) {
            data.put("age", user.getAge());
        }
        if (user.getMoney() != 0) {
            data.put("money", user.getMoney());
        }

        if (!user.getEmail().isBlank() && user.getEmail() != null) {
            data.put("email", user.getEmail());
        }

        if (data.isEmpty())
            return true;// ?

        StringBuilder userUpdate = new StringBuilder("UPDATE users SET ");// sql ="UPDATE users SET " ;
        boolean isFirst = true;
        for (Map.Entry<String, Object> entry : data.entrySet()) {

            if (isFirst)
                isFirst = false;
            else
                userUpdate.append(", ");
            userUpdate.append(entry.getKey());
            userUpdate.append("= ?");
        }
        userUpdate.append(" WHERE userId = ?");

        if (!user.getEmail().isBlank()) {

            String userAccessUpdateEmail = String.format("UPDATE user_access SET login= ? WHERE user_id = '%s'",
                    user.getUserId().toString());

            Future<Boolean> task = CompletableFuture.supplyAsync(() -> {

                try (PreparedStatement prep = dbService.getConnection().prepareStatement(userAccessUpdateEmail)) {

                    prep.setString(1, user.getEmail());
                    prep.execute();
                    return true;
                } catch (SQLException ex) {

                    logger.log(Level.WARNING, "UserDao::User access  error {0},{1}",
                            new Object[] { ex.getMessage(), userAccessUpdateEmail });
                }
                return false;
            });

            Future<Boolean> task1 = CompletableFuture.supplyAsync(() -> {

                try (PreparedStatement prep = dbService.getConnection().prepareStatement(userUpdate.toString())) {
                    int param = 1;
                    for (Map.Entry<String, Object> entry : data.entrySet()) {
                        prep.setObject(param, entry.getValue());
                        param += 1;
                    }
                    prep.setString(param, user.getUserId().toString());
                    prep.execute();
                    return true;
                } catch (SQLException ex) {

                    logger.log(Level.WARNING, "UserDao::users error {0},{1}",
                            new Object[] { ex.getMessage(), userUpdate });
                }
                return false;
            });

            try {
                boolean res1 = task.get();
                boolean res2 = task1.get();
                try {
                    dbService.getConnection().commit();
                } catch (SQLException ignore) {
                }
                ;
                return res1 && res2;
            } catch (Exception e) {

                logger.log(Level.WARNING, "The super puper Async User update {0}", e.getMessage());
                return false;
            }
        } else {

            try (PreparedStatement prep = dbService.getConnection().prepareStatement(userUpdate.toString())) {
                int param = 1;
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    prep.setObject(param, entry.getValue());
                    param += 1;
                }
                prep.setString(param, user.getUserId().toString());
                prep.execute();
                dbService.getConnection().commit();
                return true;
            } catch (SQLException ex) {

                logger.log(Level.WARNING, "UserDao::getUserByid Parse error {0},{1}",
                        new Object[] { ex.getMessage(), userUpdate });
            }

        }
        return false;

    }

    public User getUserById(UUID uuid) {

        String sql = String.format("SELECT * FROM users u WHERE u.userId='%s'", uuid);
        try (Statement stmt = dbService.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {

                return User.froResulSet(rs);

            }
        } catch (SQLException ex) {

            logger.log(Level.WARNING, "UserDao::getUserByid Parse error {0},{1}",
                    new Object[] { ex.getMessage(), sql });
        }
        return null;

    }

    public boolean installTables() {

        Future<Boolean> task1 = CompletableFuture.supplyAsync(this::installUsersAccess);
        Future<Boolean> task2 = CompletableFuture.supplyAsync(this::installUsers);
        try {
            boolean res1 = task1.get();
            boolean res2 = task2.get();
            try {
                dbService.getConnection().commit();
            } catch (SQLException ignore) {
            }
            ;
            return res1 && res2;
        } catch (Exception e) {

            logger.log(Level.WARNING, "The super puper Async {0}", e.getMessage());
            return false;
        }

    }

    private boolean installUsers() {

        String sql = "CREATE TABLE IF NOT EXISTS users("
                + "userId  CHAR(36)     PRIMARY KEY DEFAULT( UUID() ),"
                + "name    VARCHAR(128) NOT NULL,"
                + "phone   VARCHAR(32)  NOT NULL,"
                + "city    VARCHAR(20)  NOT NULL,"
                + "dateOfB DATE             NULL,"
                + "age     INT              NULL,"
                + "money   DOUBLE (10,2)    NULL,"
                + "delete_moment   DATETIME NULL,"
                + "email   VARCHAR(256) NOT NULL"
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

    public User addUser(UserSignUpFormModel userModel) {

        User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setName(userModel.getName());
        user.setPhone(userModel.getPhone());
        user.setCity(userModel.getCity());
        user.setDofb(userModel.getDofb());
        user.setAge(userModel.getAge());
        user.setMoney(userModel.getMoney());
        user.setEmail(userModel.getEmail());

        String sql = "INSERT INTO users (userId,name,phone,city,dateOfB,age,money,email)" +

                "VALUES(?, ?, ?, ?,?,?,?,?)";

        try (PreparedStatement prep = this.connection.prepareStatement(sql)) {
            prep.setString(1, user.getUserId().toString());
            prep.setString(2, user.getName());
            prep.setString(3, user.getPhone());
            prep.setString(4, user.getCity());
            prep.setDate(5, new Date(user.getDofb().getTime()));
            prep.setInt(6, user.getAge());
            prep.setDouble(7, user.getMoney());
            prep.setString(8, user.getEmail());

            prep.executeUpdate();

        } catch (SQLException ex) {

            logger.warning("UserDao::addUser 1 " + ex.getMessage());
            try {
                this.connection.rollback();
            } catch (SQLException e) {

                e.printStackTrace();
            }
            return null;

        }

        sql = "INSERT INTO user_access (user_access_id, user_id, role_id, login, salt, dk )" +

                "VALUES(UUID(),?,'guest', ?, ?, ?)";
        try (PreparedStatement prep = this.dbService.getConnection().prepareStatement(sql)) {

            prep.setString(1, user.getUserId().toString());
            prep.setString(2, user.getEmail());
            String salt = UUID.randomUUID().toString().substring(0, 16);
            prep.setString(3, salt);
            prep.setString(4, kdfService.dk(userModel.getPassword(), salt));
            prep.executeUpdate();
            this.dbService.getConnection().commit();

        } catch (SQLException ex) {

            logger.warning("UserDao::addUser 2 " + ex.getMessage());
            try {
                this.connection.rollback();
            } catch (SQLException e) {

                e.printStackTrace();
            }
            return null;

        }

        return user;
    }

    public UserAccess authorize(String login, String pass) {

        String sql = "select * from user_access ua "
                // + "join users u on ua.user_id=u.userId "
                + "where ua.login = ? ";

        try (PreparedStatement prep = dbService.getConnection().prepareStatement(sql)) {

            prep.setString(1, login);
            ResultSet rs = prep.executeQuery();

            if (rs.next()) {

                String dk = kdfService.dk(pass, rs.getString("salt"));
                if (Objects.equals(dk, rs.getString("dk"))) {

                    return UserAccess.fromResultSet(rs);

                }

            }
        } catch (SQLException ex) {

            logger.log(Level.WARNING, "UserDao::autorize {0}", ex.getMessage());

        }
        return null;

    }

    private boolean installRole() {

        String sql = "CREATE TABLE IF NOT EXISTS users_roles("
                + "rolesId  CHAR(36)     PRIMARY KEY DEFAULT( UUID() ),"
                + "user_id  CHAR(36)       NOT NULL,"
                + "role   VARCHAR(16)      NOT NULL,"
                + "canCreate INT DEFAULT(false),"
                + "canRead   INT DEFAULT(false),"
                + "canUpdate INT DEFAULT(false),"
                + "canDelete INT DEFAULT(false),"
                + "description   VARCHAR(256)     NULL"
                + ") Engine = InnoDB, DEFAULT CHARSET = utf8mb4";
        try (Statement statement = connection.createStatement()) {

            statement.executeUpdate(sql);
            logger.info("installRole Ok");
            return true;

        } catch (SQLException ex) {

            logger.warning("UserDao::installRole " + ex.getCause());
            return false;

        }
    }

    private boolean installUsersAccess() {

        String sql = "CREATE TABLE IF NOT EXISTS user_access("
                + "user_access_id  CHAR(36)     PRIMARY KEY DEFAULT( UUID() ),"
                + "user_id  CHAR(36)       NOT NULL,"
                + "login    VARCHAR(128)   NOT NULL,"
                + "salt   VARCHAR(16)      NOT  NULL,"
                + "role_id   VARCHAR(16)   NOT  NULL,"
                + "dk   VARCHAR(20)        NOT NULL,"
                + "ua_delete_dt  DATETIME   NULL,"
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
}
