package itstep.learning.dal.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.google.inject.Injector;
import itstep.learning.services.db.DbService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DataContext {
    private final Connection connection;
    private final UserDao userDao;
    private final Injector injector;

    @Inject
    public DataContext(DbService dbService, Logger logger, Injector injector) throws SQLException {
        this.connection = dbService.getConnection();
        userDao = injector.getInstance(UserDao.class);
        this.injector = injector;
    }

    public UserDao getUserDao() {
        return userDao;
    }

}