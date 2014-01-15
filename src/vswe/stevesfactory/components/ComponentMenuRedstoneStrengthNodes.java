package vswe.stevesfactory.components;


public class ComponentMenuRedstoneStrengthNodes extends ComponentMenuRedstoneStrength {
    public ComponentMenuRedstoneStrengthNodes(FlowComponent parent) {
        super(parent);
    }

    @Override
    public String getName() {
        return "Strength";
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
