package itstep.learning.dal.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.RuntimeErrorException;

import itstep.learning.dal.dto.Category;
import itstep.learning.dal.dto.Product;
import itstep.learning.services.db.DbService;
import com.google.inject.Inject;

public class CategoryDao {

    private final DbService dbService;
    private final Logger logger;

    @Inject
    public CategoryDao(DbService dbService, Logger logger) {

        this.logger = logger;
        this.dbService = dbService;

    }

    public Category getCategoryBySlug(String slug) {

        if (slug == null) {
            return null;
        }
        Category category = null;
        String sql = "SELECT * FROM categoties c LEFT JOIN products p ON c.category_id=p.category_id WHERE c.category_slug=?";

        try (PreparedStatement prep = dbService.getConnection().prepareStatement(sql)) {

            prep.setString(1, slug);
            ResultSet rs = prep.executeQuery();

            if (rs.next()) {
                category = Category.fromResulSet(rs);
                List<Product> products = new ArrayList<>();
                do{
                    try {
                        products.add(Product.fromResulSet(rs));
                    } catch (Exception ignore) {
                    }
                }while(rs.next());
                category.setProducts(products);

            }

        } catch (SQLException ex) {

            logger.log(Level.WARNING, "CategoryDao::CategoryBySlug {0} sql : {1} ",
                    new Object[] { ex.getMessage(), sql });
            throw new RuntimeException(ex);

        }

        return category;

    }

    public List<Category> getList() {

        List<Category> res = new ArrayList<>();
        String sql = "SELECT * FROM categoties";

        try (Statement statement = dbService.getConnection().createStatement()) {

            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {

                res.add(Category.fromResulSet(rs));

            }

        } catch (SQLException ex) {

            logger.log(Level.WARNING, "CategoryDao::getList {0} sql : {1} ",
                    new Object[] { ex.getMessage(), sql });

        }

        return res;

    }

    public boolean seedData() {

        String sql = "INSERT INTO categoties VALUES"
                + "('19ca6d5e-fb75-11ef-9598-0250c751a137','products from glass','decoration glass and dishes','glass','glass.jpg',NULL),"
                + "('679ca390-fb76-11ef-9598-0250c751a137','products from office','decoration office and dishes','office','office.jpg',NULL),"
                + "('70fddafd-fb76-11ef-9598-0250c751a137','products from stone','decoration stone and dishes','stone','stone.jpg',NULL),"
                + "('7f3cb36a-fb76-11ef-9598-0250c751a137','products from wood','decoration wood and dishes','wood','wood.jpg',NULL)"

                ;

        try (Statement statement = dbService.getConnection().createStatement()) {

            statement.executeUpdate(sql);
            logger.info("CategoryDao::seedData Ok");
            return true;

        } catch (SQLException ex) {

            logger.warning("CategoryDao::seedData " + ex.getCause());
            return false;

        }

    }

    public boolean installTables() {

        String sql = "CREATE TABLE IF NOT EXISTS categoties("
                + "category_id  CHAR(36)     PRIMARY KEY DEFAULT( UUID() ),"
                + "category_title  CHAR(64)       NOT NULL,"
                + "category_description    VARCHAR(256)   NOT NULL,"
                + "category_slug   VARCHAR(64)      NOT  NULL,"
                + "category_image_id   VARCHAR(64)   NOT  NULL,"
                + "category_deleteMoment   DATETIME       NULL,"
                + "UNIQUE(category_slug)"
                + ") Engine = InnoDB, DEFAULT CHARSET = utf8mb4";

        try (Statement statement = dbService.getConnection().createStatement()) {

            statement.executeUpdate(sql);
            logger.info("installCategoryDao Ok");
            return true;

        } catch (SQLException ex) {

            logger.warning("CategoryDao::installUsersAccess " + ex.getMessage());
            return false;

        }

    }
}
