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

    public UserRoles() {
    }

    public UUID getRolesId() {
        return this.rolesId;
    }

    public void setRolesId(UUID rolesId) {
        this.rolesId = rolesId;
    }

    public UUID getUserId() {
        return this.userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getCanCreate() {
        return this.canCreate;
    }

    public void setCanCreate(int canCreate) {
        this.canCreate = canCreate;
    }

    public int getCanRead() {
        return this.canRead;
    }

    public void setCanRead(int canRead) {
        this.canRead = canRead;
    }

    public int getCanUpdate() {
        return this.canUpdate;
    }

    public void setCanUpdate(int canUpdate) {
        this.canUpdate = canUpdate;
    }

    public int getCanDelete() {
        return this.canDelete;
    }

    public void setCanDelete(int canDelete) {
        this.canDelete = canDelete;
    }
}
