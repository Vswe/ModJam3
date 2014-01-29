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

    @Override
    protected void initRadioButtons() {
        radioButtons.add(new ComponentMenuContainer.RadioButtonInventory(0, "Require all targets"));
        radioButtons.add(new ComponentMenuContainer.RadioButtonInventory(1, "Require one target"));
    }
}
