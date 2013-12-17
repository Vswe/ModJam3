package vswe.stevesfactory.components;


public class Connection {
    private int componentId;
    private int connectionId;

    public Connection(int componentId, int connectionId) {
        this.componentId = componentId;
        this.connectionId = connectionId;
    }

    public int getComponentId() {
        return componentId;
    }

    public void setComponentId(int componentId) {
        this.componentId = componentId;
    }

    public int getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(int connectionId) {
        this.connectionId = connectionId;
    }
}
