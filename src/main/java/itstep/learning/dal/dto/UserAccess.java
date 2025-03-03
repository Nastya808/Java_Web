package itstep.learning.dal.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class UserAccess {
    private UUID userAccessId;
    private UUID userId;
    private String login;
    private String salt;
    private String dk;
    private String roleId;
    private Date deleteMoment;

    public static UserAccess fromResultSet(ResultSet rs) throws SQLException {
        UserAccess userAccess = new UserAccess();
        userAccess.setUserAccessId(UUID.fromString(rs.getString("user_access_id")));
        userAccess.setUserId(UUID.fromString(rs.getString("user_id")));
        userAccess.setLogin(rs.getString("login"));
        userAccess.setSalt(rs.getString("salt"));
        userAccess.setDk(rs.getString("dk"));
        userAccess.setRoleId(rs.getString("role_id"));

        Timestamp timestamp = rs.getTimestamp("delete_moment");
        if (timestamp != null) {
            userAccess.setDeleteMoment(new Date(timestamp.getTime()));
        }
        return userAccess;
    }

    public UUID getUserAccessId() {
        return userAccessId;
    }

    public void setUserAccessId(UUID userAccessId) {
        this.userAccessId = userAccessId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getDk() {
        return dk;
    }

    public void setDk(String dk) {
        this.dk = dk;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public Date getDeleteMoment() {
        return deleteMoment;
    }

    public void setDeleteMoment(Date deleteMoment) {
        this.deleteMoment = deleteMoment;
    }
}