package itstep.learning.dal.dto;

import java.util.UUID;

public class UserRoles {

    private UUID rolesId;
    private UUID userId;
    private String description;
    private String role;
    private int canCreate;
    private int canRead;
    private int canUpdate;
    private int canDelete;

    public UUID getRolesId() {
        return rolesId;
    }

    public void setRolesId(UUID rolesId) {
        this.rolesId = rolesId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getCanCreate() {
        return canCreate;
    }

    public void setCanCreate(int canCreate) {
        this.canCreate = canCreate;
    }

    public int getCanRead() {
        return canRead;
    }

    public void setCanRead(int canRead) {
        this.canRead = canRead;
    }

    public int getCanUpdate() {
        return canUpdate;
    }

    public void setCanUpdate(int canUpdate) {
        this.canUpdate = canUpdate;
    }

    public int getCanDelete() {
        return canDelete;
    }

    public void setCanDelete(int canDelete) {
        this.canDelete = canDelete;
    }

}