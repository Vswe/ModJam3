package vswe.stevesfactory.components;

import vswe.stevesfactory.blocks.ConnectionBlockType;

import java.util.EnumSet;


public class ComponentMenuVariableContainers extends ComponentMenuContainer {
    public ComponentMenuVariableContainers(FlowComponent parent) {
        super(parent, null);
    }

    @Override
    protected void initRadioButtons() {
        //nothing
    }

    @Override
    public String getName() {
        return "Containers";
    }

    @Override
    protected EnumSet<ConnectionBlockType> getValidTypes() {
        return ((ComponentMenuContainerTypes)getParent().getMenus().get(1)).getValidTypes();
    }

    @Override
    public boolean isVariableAllowed(int i) {
        return i != ((ComponentMenuVariable)getParent().getMenus().get(0)).getSelectedVariable();
    }
}
