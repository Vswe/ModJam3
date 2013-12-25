package vswe.stevesfactory.components;


public enum ComponentType {
    TRIGGER(0,
            new ConnectionSet[] {ConnectionSet.CONTINUOUSLY, ConnectionSet.REDSTONE},
            ComponentMenuInterval.class, ComponentMenuRedstone.class, ComponentMenuResult.class),
    INPUT(1,
            new ConnectionSet[]{ConnectionSet.STANDARD},
            ComponentMenuInventory.class, ComponentMenuTargetInventory.class, ComponentMenuItem.class, ComponentMenuResult.class),
    OUTPUT(2,
            new ConnectionSet[]{ConnectionSet.STANDARD},
            ComponentMenuInventory.class, ComponentMenuTargetInventory.class, ComponentMenuItemOutput.class, ComponentMenuResult.class),
    CONDITION(3,
            new ConnectionSet[]{ConnectionSet.STANDARD_CONDITION},
            ComponentMenuInventoryCondition.class, ComponentMenuTargetInventory.class, ComponentMenuItemCondition.class, ComponentMenuResult.class),
    FLOW_CONTROL(4,
            new ConnectionSet[]{ConnectionSet.MULTIPLE_INPUT_2, ConnectionSet.MULTIPLE_INPUT_5},
            ComponentMenuResult.class),
    LIQUID_INPUT(5,                     //TODO Add the liquid menu
            new ConnectionSet[]{ConnectionSet.STANDARD},
            ComponentMenuTank.class, ComponentMenuTargetTank.class, ComponentMenuLiquid.class, ComponentMenuResult.class),
    LIQUID_OUTPUT(6,
            new ConnectionSet[]{ConnectionSet.STANDARD},
            ComponentMenuTank.class, ComponentMenuTargetTank.class, ComponentMenuLiquid.class, ComponentMenuResult.class);

    private Class<? extends ComponentMenu>[] classes;
    private int id;
    private ConnectionSet[] sets;

    private ComponentType(int id, ConnectionSet[] sets, Class<? extends ComponentMenu> ... classes) {
        this.classes = classes;
        this.id = id;
        this.sets = sets;
    }

    public Class<? extends ComponentMenu>[] getClasses() {
        return classes;
    }

    public int getId() {
        return id;
    }

    public static ComponentType getTypeFromId(int id) {
        for (ComponentType componentType : values()) {
            if (id == componentType.id) {
                return componentType;
            }
        }
        return  null;
    }

    public ConnectionSet[] getSets() {
        return sets;
    }


    @Override
    public String toString() {
        String[] words = super.toString().split("_");
        String ret = "";
        for (int i = 0; i < words.length; i++) {
            if (i != 0) {
                ret += " ";
            }

            ret += words[i].charAt(0) + words[i].toLowerCase().substring(1);
        }

        return ret;
    }
}
