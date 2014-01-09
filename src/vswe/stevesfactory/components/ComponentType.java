package vswe.stevesfactory.components;


public enum ComponentType {
    TRIGGER(0, "Trigger", "Trigger",
            new ConnectionSet[] {ConnectionSet.CONTINUOUSLY, ConnectionSet.REDSTONE},
            ComponentMenuInterval.class, ComponentMenuRedstoneSidesTrigger.class, ComponentMenuRedstoneStrength.class, ComponentMenuResult.class),
    INPUT(1, "Input", "Input",
            new ConnectionSet[]{ConnectionSet.STANDARD},
            ComponentMenuInventory.class, ComponentMenuTargetInventory.class, ComponentMenuItem.class, ComponentMenuResult.class),
    OUTPUT(2, "Output", "Output",
            new ConnectionSet[]{ConnectionSet.STANDARD},
            ComponentMenuInventory.class, ComponentMenuTargetInventory.class, ComponentMenuItemOutput.class, ComponentMenuResult.class),
    CONDITION(3, "Condition", "Condition",
            new ConnectionSet[]{ConnectionSet.STANDARD_CONDITION},
            ComponentMenuInventoryCondition.class, ComponentMenuTargetInventory.class, ComponentMenuItemCondition.class, ComponentMenuResult.class),
    FLOW_CONTROL(4, "Flow", "Flow Control",
            new ConnectionSet[]{ConnectionSet.MULTIPLE_INPUT_2, ConnectionSet.MULTIPLE_INPUT_5, ConnectionSet.MULTIPLE_OUTPUT_2, ConnectionSet.MULTIPLE_OUTPUT_5},
            ComponentMenuSplit.class, ComponentMenuResult.class),
    LIQUID_INPUT(5, "Input (L)", "Liquid Input",
            new ConnectionSet[]{ConnectionSet.STANDARD},
            ComponentMenuTank.class, ComponentMenuTargetTank.class, ComponentMenuLiquid.class, ComponentMenuResult.class),
    LIQUID_OUTPUT(6, "Output (L)", "Liquid Output",
            new ConnectionSet[]{ConnectionSet.STANDARD},
            ComponentMenuTank.class, ComponentMenuTargetTank.class, ComponentMenuLiquidOutput.class, ComponentMenuResult.class),
    LIQUID_CONDITION(7, "Condition (L)", "Liquid Condition",
            new ConnectionSet[]{ConnectionSet.STANDARD_CONDITION},
            ComponentMenuTankCondition.class, ComponentMenuTargetTank.class, ComponentMenuLiquidCondition.class, ComponentMenuResult.class),
    /*REDSTONE_EMITTER(8, "Emitter", "Redstone Emitter",
            new ConnectionSet[]{ConnectionSet.STANDARD},
            ComponentMenuEmitters.class, ComponentMenuRedstoneSidesEmitter.class, ComponentMenuRedstoneOutput.class, ComponentMenuResult.class),*/

    ;/*AUTO_CRAFTING(9, "Crafter", "Auto Crafter",
            new ConnectionSet[]{ConnectionSet.STANDARD},
            ComponentMenuCrafting.class, ComponentMenuResult.class); */


    private Class<? extends ComponentMenu>[] classes;
    private int id;
    private ConnectionSet[] sets;
    private String name;
    private String longName;

    private ComponentType(int id, String name, String longName, ConnectionSet[] sets, Class<? extends ComponentMenu> ... classes) {
        this.classes = classes;
        this.id = id;
        this.sets = sets;
        this.name = name;
        this.longName = longName;
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


    public String getName() {
        return name;
    }

    public String getLongName() {
        return longName;
    }

    @Override
    public String toString() {
        return name + "[" + longName + "]";
    }
}
