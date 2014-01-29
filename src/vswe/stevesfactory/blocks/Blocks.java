package vswe.stevesfactory.blocks;


import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;

public final class Blocks {

    public static final byte NBT_CURRENT_PROTOCOL_VERSION = 8;
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


    private static final String CABLE_RELAY_TILE_ENTITY_TAG = "TileEntityCableRelayName";
    public static int CABLE_RELAY_ID;
    public static final String CABLE_RELAY_NAME_TAG = "BlockCableRelayName";
    public static final String CABLE_ADVANCED_RELAY_NAME_TAG = "BlockAdvancedCableRelayName";
    public static final String CABLE_RELAY_LOCALIZED_NAME = "Inventory Relay";
    public static final String CABLE_ADVANCED_RELAY_LOCALIZED_NAME = "Advanced Inventory Relay";
    public static final int CABLE_RELAY_DEFAULT_ID = 1313;

    private static final String CABLE_OUTPUT_TILE_ENTITY_TAG = "TileEntityCableOutputName";
    public static int CABLE_OUTPUT_ID;
    public static final String CABLE_OUTPUT_NAME_TAG = "BlockCableOutputName";
    public static final String CABLE_OUTPUT_LOCALIZED_NAME = "Redstone Emitter";
    public static final int CABLE_OUTPUT_DEFAULT_ID = 1314;

    private static final String CABLE_INPUT_TILE_ENTITY_TAG = "TileEntityCableInputName";
    public static int CABLE_INPUT_ID;
    public static final String CABLE_INPUT_NAME_TAG = "BlockCableInputName";
    public static final String CABLE_INPUT_LOCALIZED_NAME = "Redstone Receiver";
    public static final int CABLE_INPUT_DEFAULT_ID = 1315;

    private static final String CABLE_CREATIVE_TILE_ENTITY_TAG = "TileEntityCableCreativeName";
    public static int CABLE_CREATIVE_ID;
    public static final String CABLE_CREATIVE_NAME_TAG = "BlockCableCreativeName";
    public static final String CABLE_CREATIVE_LOCALIZED_NAME = "Creative Supplier";
    public static final int CABLE_CREATIVE_DEFAULT_ID = 1316;

    private static final String CABLE_INTAKE_TILE_ENTITY_TAG = "TileEntityCableIntakeName";
    public static int CABLE_INTAKE_ID;
    public static final String CABLE_INTAKE_NAME_TAG = "BlockCableIntakeName";
    public static final String CABLE_INTAKE_LOCALIZED_NAME = "Item Valve";
    public static final int CABLE_INTAKE_DEFAULT_ID = 1317;

    private static final String CABLE_BUD_TILE_ENTITY_TAG = "TileEntityCableBUDName";
    public static int CABLE_BUD_ID;
    public static final String CABLE_BUD_NAME_TAG = "BlockCableBUDName";
    public static final String CABLE_BUD_LOCALIZED_NAME = "Block Detector";
    public static final int CABLE_BUD_DEFAULT_ID = 1318;

    private static final String CABLE_BREAKER_TILE_ENTITY_TAG = "TileEntityCableBreakerName";
    public static int CABLE_BREAKER_ID;
    public static final String CABLE_BREAKER_NAME_TAG = "BlockCableBreakerName";
    public static final String CABLE_BREAKER_LOCALIZED_NAME = "Block Gate";
    public static final int CABLE_BREAKER_DEFAULT_ID = 1319;

    public static BlockManager blockManager;
    public static BlockCable blockCable;
    public static BlockCableRelay blockCableRelay;
    public static BlockCableOutput blockCableOutput;
    public static BlockCableInput blockCableInput;
    public static BlockCableCreative blockCableCreative;
    public static BlockCableIntake blockCableIntake;
    public static BlockCableBUD blockCableBUD;
    public static BlockCableBreaker blockCableBreaker;
    public static CreativeTabs creativeTab;

    public static void init() {
        creativeTab = new CreativeTabs("SFM") {
            @Override
            public ItemStack getIconItemStack() {
                return new ItemStack(blockManager);
            }
        };

        blockManager = new BlockManager(MANAGER_ID);
        GameRegistry.registerBlock(blockManager, MANAGER_NAME_TAG);
        GameRegistry.registerTileEntity(TileEntityManager.class, MANAGER_TILE_ENTITY_TAG);

        blockCable = new BlockCable(CABLE_ID);
        GameRegistry.registerBlock(blockCable, CABLE_NAME_TAG);

        blockCableRelay = new BlockCableRelay(CABLE_RELAY_ID);
        GameRegistry.registerBlock(blockCableRelay, ItemRelay.class, CABLE_RELAY_NAME_TAG);
        GameRegistry.registerTileEntity(TileEntityRelay.class, CABLE_RELAY_TILE_ENTITY_TAG);

        blockCableOutput = new BlockCableOutput(CABLE_OUTPUT_ID);
        GameRegistry.registerBlock(blockCableOutput, CABLE_OUTPUT_NAME_TAG);
        GameRegistry.registerTileEntity(TileEntityOutput.class, CABLE_OUTPUT_TILE_ENTITY_TAG);

        blockCableInput = new BlockCableInput(CABLE_INPUT_ID);
        GameRegistry.registerBlock(blockCableInput, CABLE_INPUT_NAME_TAG);
        GameRegistry.registerTileEntity(TileEntityInput.class, CABLE_INPUT_TILE_ENTITY_TAG);

        blockCableCreative = new BlockCableCreative(CABLE_CREATIVE_ID);
        GameRegistry.registerBlock(blockCableCreative, CABLE_CREATIVE_NAME_TAG);
        GameRegistry.registerTileEntity(TileEntityCreative.class, CABLE_CREATIVE_TILE_ENTITY_TAG);

        blockCableIntake = new BlockCableIntake(CABLE_INTAKE_ID);
        GameRegistry.registerBlock(blockCableIntake, CABLE_INTAKE_NAME_TAG);
        GameRegistry.registerTileEntity(TileEntityIntake.class, CABLE_INTAKE_TILE_ENTITY_TAG);

        blockCableBUD = new BlockCableBUD(CABLE_BUD_ID);
        GameRegistry.registerBlock(blockCableBUD, CABLE_BUD_NAME_TAG);
        GameRegistry.registerTileEntity(TileEntityBUD.class, CABLE_BUD_TILE_ENTITY_TAG);

        blockCableBreaker = new BlockCableBreaker(CABLE_BREAKER_ID);
        GameRegistry.registerBlock(blockCableBreaker, CABLE_BREAKER_NAME_TAG);
        GameRegistry.registerTileEntity(TileEntityBreaker.class, CABLE_BREAKER_TILE_ENTITY_TAG);
    }

    public static void addNames() {
        LanguageRegistry.addName(blockManager, MANAGER_LOCALIZED_NAME);
        LanguageRegistry.addName(blockCable, CABLE_LOCALIZED_NAME);
        LanguageRegistry.addName(new ItemStack(blockCableRelay, 1 , 0), CABLE_RELAY_LOCALIZED_NAME);
        LanguageRegistry.addName(new ItemStack(blockCableRelay, 1 , 8), CABLE_ADVANCED_RELAY_LOCALIZED_NAME);
        LanguageRegistry.addName(blockCableOutput, CABLE_OUTPUT_LOCALIZED_NAME);
        LanguageRegistry.addName(blockCableInput, CABLE_INPUT_LOCALIZED_NAME);
        LanguageRegistry.addName(blockCableCreative, CABLE_CREATIVE_LOCALIZED_NAME);
        LanguageRegistry.addName(blockCableIntake, CABLE_INTAKE_LOCALIZED_NAME);
        LanguageRegistry.addName(blockCableBUD, CABLE_BUD_LOCALIZED_NAME);
        LanguageRegistry.addName(blockCableBreaker, CABLE_BREAKER_LOCALIZED_NAME);
        LanguageRegistry.instance().addStringLocalization(creativeTab.getTranslatedTabLabel(), "en_US", "Steve's Factory Manager");
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

        GameRegistry.addShapelessRecipe(new ItemStack(blockCableRelay, 1),
                blockCable,
                Block.hopperBlock
        );

        GameRegistry.addShapelessRecipe(new ItemStack(blockCableOutput, 1),
                blockCable,
                Item.redstone,
                Item.redstone,
                Item.redstone
        );


        GameRegistry.addShapelessRecipe(new ItemStack(blockCableInput, 1),
                blockCable,
                Item.redstone
        );

        GameRegistry.addShapelessRecipe(new ItemStack(blockCableRelay, 1, 8),
                new ItemStack(blockCableRelay, 1, 0),
                new ItemStack(Item.dyePowder, 1, 4)
        );

        GameRegistry.addShapelessRecipe(new ItemStack(blockCableIntake, 1),
                blockCable,
                Block.hopperBlock,
                Block.hopperBlock,
                Block.dropper
        );

        GameRegistry.addShapelessRecipe(new ItemStack(blockCableBUD, 1),
                blockCable,
                Item.netherQuartz,
                Item.netherQuartz,
                Item.netherQuartz
        );


        GameRegistry.addShapelessRecipe(new ItemStack(blockCableBreaker, 1),
                blockCable,
                Item.pickaxeIron,
                Block.dispenser
        );
    }

    private Blocks() {}
}
