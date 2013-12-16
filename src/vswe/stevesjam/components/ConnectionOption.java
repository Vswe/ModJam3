package vswe.stevesjam.components;


public enum ConnectionOption {
    STANDARD_INPUT("Input", true),
    STANDARD_OUTPUT("Output", false),
    SUCCESS("Success", false),
    FAILURE("Failure", false),
    INTERVAL("Interval", false),
    REDSTONE_PULSE_HIGH("On high pulse", false),
    REDSTONE_PULSE_LOW("On low pulse", false),
    REDSTONE_HIGH("While high signal", false),
    REDSTONE_LOW("While low signal", false);

    private String name;
    private boolean isInput;


    private ConnectionOption(String name, boolean isInput) {
        this.name = name;
        this.isInput = isInput;
    }

    public boolean isInput() {
        return isInput;
    }


    @Override
    public String toString() {
        return name;
    }
}
