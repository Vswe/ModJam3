package vswe.stevesjam.components;


public enum ConnectionSet {
    STANDARD(0, "Standard", ConnectionOption.STANDARD_INPUT, ConnectionOption.STANDARD_OUTPUT),
    FAIL_CHECK(1, "Result Based", ConnectionOption.STANDARD_INPUT, ConnectionOption.SUCCESS, ConnectionOption.FAILURE);


    private ConnectionOption[] connections;
    private int outputCount;
    private int inputCount;
    private String name;
    private int id;

    private ConnectionSet(int id, String name, ConnectionOption... connections) {
        this.id = id;
        this.connections = connections;

        for (ConnectionOption connection : connections) {
            if (connection.isInput()) {
                inputCount++;
            }else{
                outputCount++;
            }
        }

        this.name = name;
    }


    public ConnectionOption[] getConnections() {
        return connections;
    }

    public int getOutputCount() {
        return outputCount;
    }

    public int getInputCount() {
        return inputCount;
    }

    public int getId() {
        return id;
    }

    public static ConnectionSet getTypeFromId(int id) {
        for (ConnectionSet connection : values()) {
            if (id == connection.id) {
                return connection;
            }
        }
        return  null;
    }

    @Override
    public String toString() {
        return name;
    }
}
