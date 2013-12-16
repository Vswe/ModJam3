package vswe.stevesjam.blocks;


import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public final class Blocks {

    private static final String MANAGER_TILE_ENTITY_TAG = "TileEntityMachineManagerName";
    public static int MANAGER_ID;
    public static final String MANAGER_NAME_TAG = "BlockMachineManagerName";
    public static final String MANAGER_LOCALIZED_NAME = "Machine Inventory Manager";
    public static final int MANAGER_DEFAULT_ID = 1311;

    public static int CABLE_ID;
    public static final String CABLE_NAME_TAG = "BlockCableName";
    public static final String CABLE_LOCALIZED_NAME = "Inventory Cable";
    public static final int CABLE_DEFAULT_ID = 1312;

    public static BlockManager blockManager;
    public static BlockCable blockCable;

    public static void init() {
        blockManager = new BlockManager(MANAGER_ID);

        GameRegistry.registerBlock(blockManager, MANAGER_NAME_TAG);
        GameRegistry.registerTileEntity(TileEntityManager.class, MANAGER_TILE_ENTITY_TAG);

        blockCable = new BlockCable(CABLE_ID);

        GameRegistry.registerBlock(blockCable, CABLE_NAME_TAG);
    }

    public static void addNames() {
        LanguageRegistry.addName(blockManager, MANAGER_LOCALIZED_NAME);
        LanguageRegistry.addName(blockCable, CABLE_LOCALIZED_NAME);
    }

    public static void addRecipes() {

    }

    private Blocks() {}
}
