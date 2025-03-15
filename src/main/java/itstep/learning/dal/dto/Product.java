package itstep.learning.dal.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

public class Product {


    private UUID   categoryId;
    private UUID   ProductId;
    private String ProductSlug;
    private String ProductTitle;
    private String ProductDescription;
    private String ProductImageId;
    private double price;
    private int    strock;
    private Date   DeleteMoment;




    public static Product fromResulSet(ResultSet rs) throws SQLException {

        Product product = new Product();

        product.setCategoryId(UUID.fromString(rs.getString("category_id")));
        product.setProductId(UUID.fromString(rs.getString("product_id")));
        product.setProductSlug(rs.getString("product_slug"));
        product.setProductTitle(rs.getString("product_title"));
        product.setProductDescription(rs.getString("product_description"));
        product.setProductImageId(rs.getString("product_image_id"));
        product.setPrice(rs.getDouble("product_price"));
        product.setStrock(rs.getInt("product_stock"));

        java.sql.Timestamp timestamp=rs.getTimestamp("product_deleteMoment");
        product.setDeleteMoment(timestamp==null?null:new Date(timestamp.getTime()));

        return product;

    }



    public UUID getCategoryId() {
        return categoryId;
    }



    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }



    public UUID getProductId() {
        return ProductId;
    }



    public void setProductId(UUID productId) {
        ProductId = productId;
    }



    public String getProductSlug() {
        return ProductSlug;
    }



    public void setProductSlug(String productSlug) {
        ProductSlug = productSlug;
    }



    public String getProductTitle() {
        return ProductTitle;
    }



    public void setProductTitle(String productTitle) {
        ProductTitle = productTitle;
    }



    public String getProductDescription() {
        return ProductDescription;
    }



    public void setProductDescription(String productDescription) {
        ProductDescription = productDescription;
    }



    public String getProductImageId() {
        return ProductImageId;
    }



    public void setProductImageId(String productImageId) {
        ProductImageId = productImageId;
    }



    public double getPrice() {
        return price;
    }



    public void setPrice(double price) {
        this.price = price;
    }



    public int getStrock() {
        return strock;
    }



    public void setStrock(int strock) {
        this.strock = strock;
    }



    public Date getDeleteMoment() {
        return DeleteMoment;
    }



    public void setDeleteMoment(Date deleteMoment) {
        DeleteMoment = deleteMoment;
    }







}
