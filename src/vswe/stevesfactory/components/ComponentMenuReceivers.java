package vswe.stevesfactory.components;

import vswe.stevesfactory.blocks.ConnectionBlockType;

import java.util.List;

public class ComponentMenuReceivers extends ComponentMenuContainer {

    public ComponentMenuReceivers(FlowComponent parent) {
        super(parent, ConnectionBlockType.RECEIVER);

        radioButtons.setSelectedOption(2);
    }

    @Override
    public String getName() {
        return "Redstone Receivers";
    }

    @Override
    public void addErrors(List<String> errors) {
        if (selectedInventories.isEmpty() && isVisible()) {
            errors.add("No receivers selected");
        }
    }

    @Override
    protected void initRadioButtons() {
        radioButtons.add(new ComponentMenuContainer.RadioButtonInventory(0, "Run a shared command once"));
        radioButtons.add(new ComponentMenuContainer.RadioButtonInventory(1, "Require all targets"));
        radioButtons.add(new ComponentMenuContainer.RadioButtonInventory(2, "Require one target"));
    }

    @Override
    public boolean isVisible() {
        return getParent().getConnectionSet() == ConnectionSet.REDSTONE;
    }
}
