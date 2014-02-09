package vswe.stevesfactory.components;

import vswe.stevesfactory.Localization;
import vswe.stevesfactory.blocks.ConnectionBlockType;

import java.util.List;

public class ComponentMenuReceivers extends ComponentMenuContainer {

    public ComponentMenuReceivers(FlowComponent parent) {
        super(parent, ConnectionBlockType.RECEIVER);

        radioButtonsMulti.setSelectedOption(2);
    }

    @Override
    public String getName() {
        return Localization.RECEIVERS_MENU.toString();
    }

    @Override
    public void addErrors(List<String> errors) {
        if (selectedInventories.isEmpty() && isVisible()) {
            errors.add(Localization.NO_RECEIVER_ERROR.toString());
        }
    }

    @Override
    protected void initRadioButtons() {
        radioButtonsMulti.add(new ComponentMenuContainer.RadioButtonInventory(0, Localization.RUN_SHARED_ONCE));
        radioButtonsMulti.add(new ComponentMenuContainer.RadioButtonInventory(1, Localization.REQUIRE_ALL_TARGETS));
        radioButtonsMulti.add(new ComponentMenuContainer.RadioButtonInventory(2, Localization.REQUIRE_ONE_TARGET));
    }

    @Override
    public boolean isVisible() {
        return getParent().getConnectionSet() == ConnectionSet.REDSTONE;
    }
}
