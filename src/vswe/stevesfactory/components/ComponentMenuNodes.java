package vswe.stevesfactory.components;


import vswe.stevesfactory.Localization;
import vswe.stevesfactory.blocks.ConnectionBlockType;

import java.util.List;

public class ComponentMenuNodes extends ComponentMenuContainer {
    public ComponentMenuNodes(FlowComponent parent) {
        super(parent, ConnectionBlockType.NODE);

        radioButtonsMulti.setSelectedOption(2);
    }

    @Override
    public String getName() {
        return Localization.REDSTONE_NODE_MENU.toString();
    }

    @Override
    public void addErrors(List<String> errors) {
        if (selectedInventories.isEmpty()) {
            errors.add(Localization.NO_NODE_ERROR.toString());
        }
    }

    @Override
    protected void initRadioButtons() {
        radioButtonsMulti.add(new RadioButtonInventory(0, Localization.RUN_SHARED_ONCE));
        radioButtonsMulti.add(new RadioButtonInventory(1, Localization.REQUIRE_ALL_TARGETS));
        radioButtonsMulti.add(new RadioButtonInventory(2, Localization.REQUIRE_ONE_TARGET));
    }
}
