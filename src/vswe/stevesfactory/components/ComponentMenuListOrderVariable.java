package vswe.stevesfactory.components;


public class ComponentMenuListOrderVariable extends ComponentMenuListOrder {
    public ComponentMenuListOrderVariable(FlowComponent parent) {
        super(parent);
    }

    @Override
    public boolean isVisible() {
        return getParent().getConnectionSet() == ConnectionSet.STANDARD;
    }

    @Override
    public String getName() {
        return "Value Order";
    }
}
