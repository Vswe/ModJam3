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
        EnumSet<ConnectionBlockType> types = EnumSet.noneOf(ConnectionBlockType.class);
        ComponentMenuContainerTypes typeMenu = (ComponentMenuContainerTypes)getParent().getMenus().get(1);
        for (int i = 0; i < typeMenu.getTypes().size(); i++) {
            if (typeMenu.getChecked()[i]) {
                types.add(typeMenu.getTypes().get(i));
            }
        }
        return types;
    }
}
