package vswe.stevesfactory;


import net.minecraft.util.StatCollector;

public enum Localization {
    DETECTOR_MENU,
    REQUIRE_ALL_TARGETS,
    REQUIRE_ONE_TARGET,
    RUN_SHARED_ONCE,
    RUN_ONE_PER_TARGET,
    SELECTED,
    OVERFLOW_MENU,
    NO_DETECTOR_ERROR,
    NO_OVERFLOW_ERROR,
    OVERFLOW_INFO,
    CONTAINER_TYPE_MENU,
    CRAFTING_MENU,
    EMITTER_MENU,
    NO_EMITTER_ERROR,
    REQUIRES_ALL,
    IF_ANY,
    WHITE_LIST,
    BLACK_LIST,
    EMPTY_TANK,
    FILLED_TANK,
    STRONG_POWER,
    WEAK_POWER,
    SEQUENTIAL,
    SPLIT,
    INTERVAL_MENU,
    INTERVAL_INFO,
    SECOND,
    INVENTORY_MENU,
    NO_INVENTORY_ERROR,
    ITEM_MENU,
    DAMAGE_VALUE,
    NO_CONDITION_ERROR,
    BUCKETS,
    MILLI_BUCKETS,
    LIQUIDS_MENU,
    USE_ALL,
    REVERSED,
    LOOP_ORDER_MENU,
    VALUE_ORDER_MENU,
    REDSTONE_NODE_MENU,
    NO_NODE_ERROR,
    DO_EMIT_PULSE,
    SECONDS;



    private String name;

    private Localization() {
        String[] split = super.toString().split("_");
        this.name = "";
        for (String s : split) {
            this.name += s.charAt(0) + s.substring(1).toLowerCase();
        }
    }

    public String toString() {
        return StatCollector.translateToLocal("gui." + StevesFactoryManager.UNLOCALIZED_START + name);
    }
}
