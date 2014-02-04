package vswe.stevesfactory.components;


import vswe.stevesfactory.Localization;

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
        return Localization.VALUE_ORDER_MENU.toString();
    }
}
