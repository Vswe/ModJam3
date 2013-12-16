package vswe.stevesjam.blocks;


import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;

public final class Blocks {

    private static final String JAM_TILE_ENTITY_TAG = "TileEntityJamName";
    public static int JAM_ID;
    public static final String JAM_NAME_TAG = "BlockJamName";
    public static final String JAM_LOCALIZED_NAME = "Jam Block";
    public static final int JAM_DEFAULT_ID = 1311;

    public static int CABLE_ID;
    public static final String CABLE_NAME_TAG = "BlockCableName";
    public static final String CABLE_LOCALIZED_NAME = "Cable";
    public static final int CABLE_DEFAULT_ID = 1312;

    public static BlockJam blockJam;
    public static BlockCable blockCable;

    public static void init() {
        blockJam = new BlockJam(JAM_ID);

        GameRegistry.registerBlock(blockJam, JAM_NAME_TAG);
        GameRegistry.registerTileEntity(TileEntityJam.class, JAM_TILE_ENTITY_TAG);

        blockCable = new BlockCable(CABLE_ID);

        GameRegistry.registerBlock(blockCable, CABLE_NAME_TAG);
    }

    public static void addNames() {
        LanguageRegistry.addName(blockJam, JAM_LOCALIZED_NAME);
        LanguageRegistry.addName(blockCable, CABLE_LOCALIZED_NAME);
    }

    public static void addRecipes() {

    }

    private Blocks() {}
}
