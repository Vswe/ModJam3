package vswe.stevesfactory.components;


import vswe.stevesfactory.Localization;

import java.util.List;

public class ComponentMenuRedstoneSidesTrigger extends ComponentMenuRedstoneSides {
    public ComponentMenuRedstoneSidesTrigger(FlowComponent parent) {
        super(parent);
    }

    @Override
    protected void initRadioButtons() {
        radioButtonList.add(new RadioButton(RADIO_BUTTON_X_LEFT, RADIO_BUTTON_Y, Localization.REQUIRES_ALL));
        radioButtonList.add(new RadioButton(RADIO_BUTTON_X_RIGHT, RADIO_BUTTON_Y, Localization.IF_ANY));
    }

    @Override
    protected String getMessage() {
        if (isBUD()) {
            return "Select which block sides updates should be detected at";
        }else{
            return "Select which block sides the redstone should be detected at";
        }
    }

    @Override
    public boolean isVisible() {
        return getParent().getConnectionSet() == ConnectionSet.REDSTONE || isBUD();
    }

    public boolean requireAll() {
        return useFirstOption();
    }

    @Override
    public String getName() {
        return isBUD() ? "Update Sides" : "Redstone Sides";
    }

    private boolean isBUD() {
        return getParent().getConnectionSet() == ConnectionSet.BUD;
    }

    @Override
    public void addErrors(List<String> errors) {
        if (isVisible() && selection == 0) {
            errors.add("No sides selected");
        }
    }
}
