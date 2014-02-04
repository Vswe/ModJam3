package vswe.stevesfactory.components;


import vswe.stevesfactory.Localization;

import java.util.List;

public class ComponentMenuLiquidCondition extends ComponentMenuLiquid implements IConditionStuffMenu {
    public ComponentMenuLiquidCondition(FlowComponent parent) {
        super(parent);
    }


    @Override
    protected void initRadioButtons() {
        radioButtons.add(new RadioButton(RADIO_BUTTON_X_LEFT, RADIO_BUTTON_Y, Localization.REQUIRES_ALL));
        radioButtons.add(new RadioButton(RADIO_BUTTON_X_RIGHT, RADIO_BUTTON_Y, Localization.IF_ANY));
    }

    public boolean requiresAll() {
        return isFirstRadioButtonSelected();
    }

    @Override
    public void addErrors(List<String> errors) {
        for (Setting setting : getSettings()) {
            if (setting.isValid()) {
                return;
            }
        }

        errors.add(Localization.NO_CONDITION_ERROR.toString());
    }
}
