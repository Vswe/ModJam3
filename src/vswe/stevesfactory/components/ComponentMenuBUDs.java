package vswe.stevesfactory.components;


import vswe.stevesfactory.blocks.ConnectionBlockType;

public class ComponentMenuBUDs extends ComponentMenuContainer {
    public ComponentMenuBUDs(FlowComponent parent) {
        super(parent, ConnectionBlockType.BUD);
    }

    @Override
    public String getName() {
        return "Update Detectors";
    }

    @Override
    public boolean isVisible() {
        return getParent().getConnectionSet() == ConnectionSet.BUD;
    }
}
