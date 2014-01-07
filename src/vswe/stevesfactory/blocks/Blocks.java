package vswe.stevesfactory.blocks;


import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class Blocks {

    public static final byte NBT_CURRENT_PROTOCOL_VERSION = 5;
    public static final String NBT_PROTOCOL_VERSION = "ProtocolVersion";

    private static final String MANAGER_TILE_ENTITY_TAG = "TileEntityMachineManagerName";
    public static int MANAGER_ID;
    public static final String MANAGER_NAME_TAG = "BlockMachineManagerName";
    public static final String MANAGER_LOCALIZED_NAME = "Machine Inventory Manager";
    public static final int MANAGER_DEFAULT_ID = 1311;

    public static int CABLE_ID;
    public static final String CABLE_NAME_TAG = "BlockCableName";
    public static final String CABLE_LOCALIZED_NAME = "Inventory Cable";
    public static final int CABLE_DEFAULT_ID = 1312;

    private static final String CABLE_OUTPUT_TILE_ENTITY_TAG = "TileEntityCableOutputName";
    public static int CABLE_OUTPUT_ID;
    public static final String CABLE_OUTPUT_NAME_TAG = "BlockCableOutputName";
    public static final String CABLE_OUTPUT_LOCALIZED_NAME = "Signal Emitter";
    public static final int CABLE_OUTPUT_DEFAULT_ID = 1313;

    public static BlockManager blockManager;
    public static BlockCable blockCable;
    public static BlockCableOutput blockCableOutput;

    public static void init() {
        blockManager = new BlockManager(MANAGER_ID);
        GameRegistry.registerBlock(blockManager, MANAGER_NAME_TAG);
        GameRegistry.registerTileEntity(TileEntityManager.class, MANAGER_TILE_ENTITY_TAG);

        blockCable = new BlockCable(CABLE_ID);
        GameRegistry.registerBlock(blockCable, CABLE_NAME_TAG);

        blockCableOutput = new BlockCableOutput(CABLE_OUTPUT_ID);
        GameRegistry.registerBlock(blockCableOutput, CABLE_OUTPUT_NAME_TAG);
        GameRegistry.registerTileEntity(TileEntityOutput.class, CABLE_OUTPUT_TILE_ENTITY_TAG);
    }

    public static void addNames() {
        LanguageRegistry.addName(blockManager, MANAGER_LOCALIZED_NAME);
        LanguageRegistry.addName(blockCable, CABLE_LOCALIZED_NAME);
        LanguageRegistry.addName(blockCableOutput, CABLE_OUTPUT_LOCALIZED_NAME);
    }

    public static void addRecipes() {
        GameRegistry.addRecipe(new ItemStack(blockManager),
                "III",
                "IRI",
                "SPS",
                'R', Block.blockRedstone,
                'P', Block.pistonBase,
                'I', Item.ingotIron,
                'S', Block.stone
        );

        GameRegistry.addRecipe(new ItemStack(blockCable, 8),
                "GPG",
                "IRI",
                "GPG",
                'R', Item.redstone,
                'G', Block.glass,
                'I', Item.ingotIron,
                'P', Block.pressurePlateIron
        );

    }

    private Blocks() {}
}
