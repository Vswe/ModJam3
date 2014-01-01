package vswe.stevesfactory.components;


public class ComponentMenuRedstoneSidesEmitter extends ComponentMenuRedstoneSides {
    public ComponentMenuRedstoneSidesEmitter(FlowComponent parent) {
        super(parent);
    }

    @Override
    protected void initRadioButtons() {
        radioButtonList.add(new RadioButton(RADIO_BUTTON_X_LEFT, RADIO_BUTTON_Y, "Strong power"));
        radioButtonList.add(new RadioButton(RADIO_BUTTON_X_RIGHT, RADIO_BUTTON_Y, "Weak power"));
    }

    @Override
    protected String getMessage() {
        return "Select which block sides the redstone should be emitted at";
    }
}
