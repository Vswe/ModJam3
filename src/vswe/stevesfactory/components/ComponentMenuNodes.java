package vswe.stevesfactory.components;


import vswe.stevesfactory.blocks.ConnectionBlockType;

import java.util.List;

public class ComponentMenuNodes extends ComponentMenuContainer {
    public ComponentMenuNodes(FlowComponent parent) {
        super(parent, ConnectionBlockType.NODE);

        radioButtons.setSelectedOption(2);
    }

    @Override
    public String getName() {
        return "Redstone Nodes";
    }

    @Override
    public void addErrors(List<String> errors) {
        if (selectedInventories.isEmpty()) {
            errors.add("No nodes selected");
        }
    }

    @Override
    protected void initRadioButtons() {
        radioButtons.add(new RadioButtonInventory(0, "Run a shared command once"));
        radioButtons.add(new RadioButtonInventory(1, "Require all targets"));
        radioButtons.add(new RadioButtonInventory(2, "Require one target"));
    }
}
