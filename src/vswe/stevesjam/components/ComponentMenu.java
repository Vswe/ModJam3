package vswe.stevesjam.components;


public abstract class ComponentMenu {


    private FlowComponent parent;

    public ComponentMenu(FlowComponent parent) {
        this.parent = parent;
    }

    public abstract String getName();
}
