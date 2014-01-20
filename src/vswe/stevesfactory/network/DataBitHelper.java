package vswe.stevesfactory.network;


public enum DataBitHelper {
    FLOW_CONTROL_COUNT(7),
    FLOW_CONTROL_X(9),
    FLOW_CONTROL_Y(8),
    FLOW_CONTROL_TYPE_ID(4),
    MENU_ITEM_ID(16),
    MENU_ITEM_META(15),
    MENU_ITEM_AMOUNT(10),
    MENU_CONNECTION_TYPE_ID(3),
    MENU_TARGET_RANGE(7),
    MENU_INVENTORY_SELECTION(10),
    MENU_INVENTORY_MULTI_SELECTION_TYPE(2),
    FLOW_CONTROL_MENU_COUNT(3),
    MENU_INTERVAL(10),

    MENU_TARGET_DIRECTION_ID(3),
    MENU_TARGET_TYPE_HEADER(2),
    MENU_REDSTONE_SETTING(6),

    BOOLEAN(1),
    EMPTY(0),
    MENU_ITEM_SETTING_ID(5),
    MENU_ITEM_TYPE_HEADER(3),

    CONNECTION_ID(3),
    NODE_ID(4),

    CLIENT_HEADER(2),
    WORLD_COORDINATE(32),
    GUI_BUTTON_ID(4),

    MENU_FLUID_ID(10) //Actually an int, but I think 10 bits is too much anyways. If someone has over 1024 fluids I have a problem
    ,
    MENU_LIQUID_AMOUNT(20),

    MENU_SPLIT_DATA_ID(2),

    MENU_REDSTONE_ANALOG(4),
    MENU_REDSTONE_OUTPUT_TYPE(3),
    PULSE_COMPONENT_TYPES(2),
    PULSE_TYPES(2),
    PULSE_SECONDS(7),
    PULSE_TICKS(5),

    NAME_LENGTH(5),
    PERMISSION_ID(8),

    COMMAND_NAME(5),
    NBT_LENGTH(15),
    FUZZY_MODE(2),

    CONTAINER_TYPE(2),
    CONTAINER_MODE(3),
    VARIABLE_TYPE(4);



    private int bitCount;

    private DataBitHelper(int bitCount) {
        this.bitCount = bitCount;
    }

    public int getBitCount() {
        return bitCount;
    }
}
