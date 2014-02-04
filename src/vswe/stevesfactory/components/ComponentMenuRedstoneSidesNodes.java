package vswe.stevesfactory.components;


import vswe.stevesfactory.Localization;

public class ComponentMenuRedstoneSidesNodes extends ComponentMenuRedstoneSidesTrigger{

    public ComponentMenuRedstoneSidesNodes(FlowComponent parent) {
        super(parent);
    }

    @Override
    public String getName() {
        return Localization.REDSTONE_SIDES_MENU.toString();
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
