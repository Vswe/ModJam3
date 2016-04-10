package vswe.stevesfactory.blocks;


import java.util.UUID;

public class UserPermission {
    private UUID userId;
    private String name;
    private boolean op;
    private boolean active;

    public UserPermission(UUID userId, String name) {
        this.userId = userId;
        if (name == null) {
            this.name = "Unknown";
        } else{
            this.name = name;
        }
    }

    public UUID getUserId() {
        return userId;
    }

    public String getUserName() {
        return name;
    }

    public boolean isOp() {
        return op;
    }

    public void setOp(boolean op) {
        this.op = op;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public UserPermission copy() {
        UserPermission temp = new UserPermission(getUserId(), getUserName());
        temp.setOp(isOp());
        temp.setActive(isActive());
        return temp;
    }
}
