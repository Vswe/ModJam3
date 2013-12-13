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

    private static Block blockJam;

    public static void init() {
        blockJam = new BlockJam(JAM_ID);

        GameRegistry.registerBlock(blockJam, JAM_NAME_TAG);
        GameRegistry.registerTileEntity(TileEntityJam.class, JAM_TILE_ENTITY_TAG);
    }

    public static void addNames() {
        LanguageRegistry.addName(blockJam, JAM_LOCALIZED_NAME);
    }

    public static void addRecipes() {

    }

    private Blocks() {}
}
