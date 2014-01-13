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
        return "Select which block sides the redstone should be detected at";
    }

    @Override
    public boolean isVisible() {
        return getParent().getConnectionSet() == ConnectionSet.REDSTONE;
    }

    public boolean requireAll() {
        return useFirstOption();
    }

    @Override
    public String getName() {
        return "Redstone Sides";
    }
}
