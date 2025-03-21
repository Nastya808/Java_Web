package itstep.learning.dal.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

public class CartItem {

    private UUID cardItemId;
    private UUID ucardId;
    private UUID productId;
    private UUID actionId;
    private double cardItemPrice;
    private short quantity;

    public static CartItem fromResulSet(ResultSet rs) throws SQLException {

        CartItem cartItem = new CartItem();

        cartItem.setCardItemId(UUID.fromString(rs.getString("card_item_id")));
        cartItem.setUcardId(UUID.fromString(rs.getString("ucard_id")));
        cartItem.setProductId(UUID.fromString(rs.getString("product_id")));
        String actionId=rs.getString("action_id");
        if (actionId != null) {
            cartItem.setActionId(UUID.fromString(actionId));

        }
        cartItem.setCardItemPrice(rs.getDouble("card_item_price"));
        cartItem.setQuantity(rs.getShort("quantity"));

        return cartItem;

    }

    public UUID getCardItemId() {
        return cardItemId;
    }

    public void setCardItemId(UUID cardItemId) {
        this.cardItemId = cardItemId;
    }

    public UUID getUcardId() {
        return ucardId;
    }

    public void setUcardId(UUID ucardId) {
        this.ucardId = ucardId;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public UUID getActionId() {
        return actionId;
    }

    public void setActionId(UUID actionId) {
        this.actionId = actionId;
    }

    public double getCardItemPrice() {
        return cardItemPrice;
    }

    public void setCardItemPrice(double cardItemPrice) {
        this.cardItemPrice = cardItemPrice;
    }

    public short getQuantity() {
        return quantity;
    }

    public void setQuantity(short quantity) {
        this.quantity = quantity;
    }
}