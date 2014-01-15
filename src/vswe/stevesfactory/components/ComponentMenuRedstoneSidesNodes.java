package vswe.stevesfactory.components;


public class ComponentMenuRedstoneSidesNodes extends ComponentMenuRedstoneSidesTrigger{

    public ComponentMenuRedstoneSidesNodes(FlowComponent parent) {
        super(parent);
    }

    @Override
    public String getName() {
        return "Sides";
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
