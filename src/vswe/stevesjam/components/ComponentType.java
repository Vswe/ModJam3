package vswe.stevesjam.components;


public enum ComponentType {
    INPUT(0, ComponentMenuInventory.class, ComponentMenuTarget.class, ComponentMenuItem.class, ComponentMenuResult.class);

    private Class<? extends ComponentMenu>[] classes;
    private int id;

    private ComponentType(int id, Class<? extends ComponentMenu> ... classes) {
        this.classes = classes;
        this.id = id;
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
}
