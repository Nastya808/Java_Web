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
    private AccessTokenDao accessTokenDao;
    private CategoryDao categoryDao;
    private ProductDao productDao;
    private CartDao cartDao;



    @Inject
    public DataContext(ProductDao productDao,CartDao cartDao, CategoryDao categoryDao, Injector injector, AccessTokenDao accessTokenDao)
            throws SQLException {
        this.userDao = injector.getInstance(UserDao.class);
        this.accessTokenDao = accessTokenDao;
        this.categoryDao = categoryDao;
        this.productDao = productDao;
        this.cartDao=cartDao;


    }

    public UserDao getUserDao() {
        return userDao;
    }

    public AccessTokenDao getAccessTokenDao() {
        return accessTokenDao;
    }

    public CategoryDao getCategoryDao() {
        return categoryDao;
    }

    public ProductDao getProductDao() {
        return productDao;
    }

    public CartDao getCartDao() {
        return cartDao;
    }

}