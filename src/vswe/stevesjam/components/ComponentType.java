package vswe.stevesjam.components;


public enum ComponentType {
    TRIGGER(0,
            new ConnectionSet[] {ConnectionSet.CONTINUOUSLY, ConnectionSet.REDSTONE},
            ComponentMenuInterval.class, ComponentMenuResult.class),
    INPUT(1,
            new ConnectionSet[]{ConnectionSet.STANDARD},
            ComponentMenuInventory.class, ComponentMenuTarget.class, ComponentMenuItem.class, ComponentMenuResult.class),
    OUTPUT(2,
            new ConnectionSet[]{ConnectionSet.STANDARD},
            ComponentMenuInventory.class, ComponentMenuTarget.class, ComponentMenuItemOutput.class, ComponentMenuResult.class),
    CONDITION(3,
            new ConnectionSet[]{ConnectionSet.STANDARD_CONDITION},
            ComponentMenuInventory.class, ComponentMenuTarget.class, ComponentMenuItemCondition.class, ComponentMenuResult.class);

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
}
