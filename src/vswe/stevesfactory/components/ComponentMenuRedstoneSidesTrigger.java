package vswe.stevesfactory.components;


public class ComponentMenuRedstoneSidesTrigger extends ComponentMenuRedstoneSides {
    public ComponentMenuRedstoneSidesTrigger(FlowComponent parent) {
        super(parent);
    }

    @Override
    protected void initRadioButtons() {
        radioButtonList.add(new RadioButton(RADIO_BUTTON_X_LEFT, RADIO_BUTTON_Y, "Requires all"));
        radioButtonList.add(new RadioButton(RADIO_BUTTON_X_RIGHT, RADIO_BUTTON_Y, "If any"));
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
}
