package itstep.learning.dal.dto;

import java.util.UUID;


public class UserAccess {
    private UUID userAccessId;
    private UUID userId;
    private String login;
    private String salt;
    private String dk;
    private String roleId;

    public UserAccess() {
    }

    public UUID getUserAccessId() {
        return this.userAccessId;
    }

    public void setUserAccessId(UUID userAccessId) {
        this.userAccessId = userAccessId;
    }

    public UUID getUserId() {
        return this.userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getLogin() {
        return this.login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSalt() {
        return this.salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getDk() {
        return this.dk;
    }

    public void setDk(String dk) {
        this.dk = dk;
    }

    public String getRoleId() {
        return this.roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
}
