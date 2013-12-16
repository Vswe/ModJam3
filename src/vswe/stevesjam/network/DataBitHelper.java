package vswe.stevesjam.network;


public enum DataBitHelper {
    FLOW_CONTROL_COUNT(6),
    FLOW_CONTROL_X(9),
    FLOW_CONTROL_Y(8),
    FLOW_CONTROL_TYPE_ID(3),
    MENU_ITEM_ID(16),
    MENU_ITEM_META(15),
    MENU_ITEM_AMOUNT(10),
    MENU_CONNECTION_TYPE_ID(3),
    MENU_TARGET_RANGE(7),
    MENU_INVENTORY_SELECTION(12),
    FLOW_CONTROL_MENU_COUNT(3),
    MENU_INTERVAL(10),

    MENU_TARGET_DIRECTION_ID(3),
    MENU_TARGET_TYPE_HEADER(2),
    BOOLEAN(1),
    EMPTY(0),
    MENU_ITEM_SETTING_ID(5),
    MENU_ITEM_TYPE_HEADER(3),

    CONNECTION_ID(3),

    CLIENT_HEADER(2),
    WORLD_COORDINATE(32),
    GUI_BUTTON_ID(3);



    private int bitCount;

    private DataBitHelper(int bitCount) {
        this.bitCount = bitCount;
    }

    public int getBitCount() {
        return bitCount;
    }
}
