package vswe.stevesfactory.components;


public class ComponentMenuTankCondition extends ComponentMenuTank {
    public ComponentMenuTankCondition(FlowComponent parent) {
        super(parent);
    }

    protected void initRadioButtons() {
        radioButtons.add(new RadioButtonInventory(0, "Run a shared command once"));
        radioButtons.add(new RadioButtonInventory(1, "Require all targets"));
        radioButtons.add(new RadioButtonInventory(2, "Require one target"));
    }
}
