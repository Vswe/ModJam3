package vswe.stevesfactory.components;


import vswe.stevesfactory.blocks.ConnectionBlockType;

import java.util.List;

public class ComponentMenuEmitters extends ComponentMenuContainer {
    public ComponentMenuEmitters(FlowComponent parent) {
        super(parent, ConnectionBlockType.EMITTER);
    }

    @Override
    public String getName() {
        return "Emitters";
    }

    @Override
    public void addErrors(List<String> errors) {
        if (selectedInventories.isEmpty()) {
            errors.add("No emitter selected");
        }
    }

    @Override
    protected void initRadioButtons() {

    }
}
