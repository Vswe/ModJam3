package vswe.stevesfactory.blocks;


public class UserPermission {
    private String name;
    private boolean op;
    private boolean active;

    public UserPermission(String name) {
        if (name == null) {
            this.name = "Unknown";
        } else{
            this.name = name;
        }
    }

    public String getName() {
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
        UserPermission temp = new UserPermission(getName());
        temp.setOp(isOp());
        temp.setActive(isActive());
        return temp;
    }
}
