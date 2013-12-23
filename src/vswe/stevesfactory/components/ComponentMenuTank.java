package vswe.stevesfactory.components;

import vswe.stevesfactory.blocks.ConnectionBlockType;

import java.util.List;


public class ComponentMenuTank extends ComponentMenuContainer {
    public ComponentMenuTank(FlowComponent parent) {
        super(parent, ConnectionBlockType.TANK);
    }

    @Override
    public String getName() {
        return "Tanks";
    }

    @Override
    public void addErrors(List<String> errors) {
        if (selectedInventories.isEmpty()) {
            errors.add("No tank selected");
        }
    }
}
