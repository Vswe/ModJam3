package vswe.stevesjam.components;


public enum ConnectionOption {
    STANDARD_INPUT("Input", true),
    STANDARD_OUTPUT("Output", false),
    SUCCESS("Success", false),
    FAILURE("Failure", false);

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
