package vswe.stevesfactory.components;


public enum ConnectionOption {
    STANDARD_INPUT("Input", ConnectionType.INPUT),
    STANDARD_OUTPUT("Output", ConnectionType.OUTPUT),
    INTERVAL("Interval", ConnectionType.OUTPUT),
    REDSTONE_PULSE_HIGH("On high pulse", ConnectionType.OUTPUT),
    REDSTONE_PULSE_LOW("On low pulse", ConnectionType.OUTPUT),
    REDSTONE_HIGH("While high signal", ConnectionType.OUTPUT),
    REDSTONE_LOW("While low signal", ConnectionType.OUTPUT),
    CONDITION_TRUE("True", ConnectionType.OUTPUT),
    CONDITION_FALSE("False", ConnectionType.OUTPUT),
    FOR_EACH("For each", ConnectionType.SIDE),
    BUD("On update", ConnectionType.OUTPUT),
    BUD_PULSE("On update pulse", ConnectionType.OUTPUT),
    BUD_MATCH("While matching", ConnectionType.OUTPUT);

    private String name;
    private ConnectionType type;


    private ConnectionOption(String name, ConnectionType type) {
        this.name = name;
        this.type = type;
    }

    public boolean isInput() {
        return type == ConnectionType.INPUT;
    }


    public ConnectionType getType() {
        return type;
    }

    @Override
    public String toString() {
        return name;
    }

    public enum ConnectionType {
        INPUT,
        OUTPUT,
        SIDE
    }
}
