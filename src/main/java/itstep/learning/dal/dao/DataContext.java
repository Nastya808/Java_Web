package itstep.learning.dal.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import java.sql.SQLException;

@Singleton
public class DataContext {
    private UserDao userDao;

    @Inject
    public DataContext(Injector injector) throws SQLException {
        this.userDao = (UserDao)injector.getInstance(UserDao.class);
    }

    public UserDao getUserDao() {
        return this.userDao;
    }
}
