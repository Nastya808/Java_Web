package itstep.learning.dal.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import itstep.learning.dal.dto.Product;
import itstep.learning.services.db.DbService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ProductDao {

    private final DbService dbService;
    private final Logger logger;

    @Inject
    public ProductDao(DbService dbService, Logger logger) throws SQLException {

        this.logger = logger;
        this.dbService = dbService;

    }

    public Product addNewProduct(Product product) {

        product.setProductId(UUID.randomUUID());

        String sql = "INSERT INTO products (product_id,category_id,product_title,"
                + "product_description,product_slug,product_image_id,"
                + "product_deleteMoment,product_price,product_stock)"
                + "VALUES(?,?,?,?,?,?,?,?,?)";

        try (PreparedStatement prep = dbService.getConnection().prepareStatement(sql)) {

            prep.setString(1, product.getProductId().toString());
            prep.setString(2, product.getCategoryId().toString());
            prep.setString(3, product.getProductTitle());
            prep.setString(4, product.getProductDescription());
            prep.setString(5, product.getProductSlug());
            prep.setString(6, product.getProductImageId());
            prep.setTimestamp(7,
                    product.getDeleteMoment() == null ? null : new Timestamp(product.getDeleteMoment().getTime()));
            prep.setDouble(8, product.getPrice());
            prep.setInt(9, product.getStrock());

            prep.executeUpdate();
            dbService.getConnection().commit();

            return product;

        } catch (SQLException ex) {

            logger.log(Level.WARNING, "ProductDao::addProduct {0} sql: {1}", new Object[] { ex.getMessage(), sql });

        }
        return null;
    }

    public boolean checkSlugCode(String slug) {

        String sql = "SELECT  product_slug FROM products p WHERE p.product_slug = ?";

        try (PreparedStatement prep = dbService.getConnection().prepareStatement(sql)) {

            prep.setString(1, slug);
            ResultSet rs = prep.executeQuery();

            if (rs.next()) {
                return true;

            } else {
                return false;
            }

        } catch (SQLException ex) {

            logger.log(Level.WARNING, "ProductDao::checkSlugCode {0}", ex.getMessage());

        }

        return false;
    }

    public boolean installTables() {

        String sql = "CREATE TABLE IF NOT EXISTS products("
                + "product_id  CHAR(36)     PRIMARY KEY DEFAULT( UUID() ),"
                + "category_id  CHAR(36)    ,"
                + "product_title          CHAR(64)       NOT NULL,"
                + "product_description    VARCHAR(256)   NOT NULL,"
                + "product_slug           VARCHAR(64)        NULL,"
                + "product_image_id       VARCHAR(64)       NULL,"
                + "product_deleteMoment   DATETIME           NULL,"
                + "product_price             FLOAT           NULL,"
                + "product_stock             INT             NULL,"
                + "UNIQUE(product_slug)"
                + ") Engine = InnoDB, DEFAULT CHARSET = utf8mb4";

        try (Statement statement = dbService.getConnection().createStatement()) {

            statement.executeUpdate(sql);
            logger.info("installProductDao Ok");
            return true;

        } catch (SQLException ex) {

            logger.warning("ProductDao::installProductDao " + ex.getMessage());
            return false;

        }

    }

}
