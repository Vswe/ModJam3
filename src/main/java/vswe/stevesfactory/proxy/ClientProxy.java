package vswe.stevesfactory.proxy;


import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.blocks.ModBlocks;
import vswe.stevesfactory.blocks.client.ModelLoader;
import vswe.stevesfactory.settings.Settings;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit() {
        Settings.load();
        ModelLoaderRegistry.registerLoader(new ModelLoader());

        addVariantNames(ModBlocks.blockCableRelay, "cable_relay", "cable_relay_advanced");
        addVariantNames(ModBlocks.blockCableIntake, "cable_valve", "cable_valve_instant");
        addVariantNames(ModBlocks.blockCableCluster, "cable_cluster", "cable_cluster_advanced");
        addVariantNames(ModBlocks.blockCableCamouflage, "cable_camouflage", "cable_camouflage_inside", "cable_camouflage_transform");

        registerBlockModel(ModBlocks.blockManager);
        registerBlockModel(ModBlocks.blockCable);
        registerBlockModel(ModBlocks.blockCableOutput);
        registerBlockModel(ModBlocks.blockCableInput);
        registerBlockModel(ModBlocks.blockCableCreative);
        registerBlockModel(ModBlocks.blockCableBUD);
        registerBlockModel(ModBlocks.blockCableBreaker);
        registerBlockModel(ModBlocks.blockCableSign);
        registerBlockModelForVariant(ModBlocks.blockCableRelay, 0, "cable_relay");
        registerBlockModelForVariant(ModBlocks.blockCableRelay, 8, "cable_relay_advanced");
        registerBlockModelForVariant(ModBlocks.blockCableIntake, 0, "cable_valve");
        registerBlockModelForVariant(ModBlocks.blockCableIntake, 8, "cable_valve_instant");
        registerBlockModelForVariant(ModBlocks.blockCableCluster, 0, "cable_cluster");
        registerBlockModelForVariant(ModBlocks.blockCableCluster, 8, "cable_cluster_advanced");
        registerBlockModelForVariant(ModBlocks.blockCableCamouflage, 0, "cable_camouflage");
        registerBlockModelForVariant(ModBlocks.blockCableCamouflage, 1, "cable_camouflage_inside");
        registerBlockModelForVariant(ModBlocks.blockCableCamouflage, 2, "cable_camouflage_transform");
    }

    private static void addVariantNames(Block block, String... names) {
        ResourceLocation[] locations = new ResourceLocation[names.length];
        for (int i = 0; i < names.length; i++)
        {
            locations[i] = new ResourceLocation(getResource(names[i]));
        }

        if(block != null)
            ModelBakery.registerItemVariants(Item.getItemFromBlock(block), locations);
    }

    private static void registerBlockModelForVariant(Block base, int meta, String variant) {
        registerBlockModel(base, meta, getResource(variant));
    }

    private static void registerBlockModel(Block block) {
        ResourceLocation resourceLocation = Block.blockRegistry.getNameForObject(block);

        registerBlockModel(block, 0, resourceLocation.toString());
    }

    private static void registerBlockModel(Block block, int meta, String modelName) {
        registerItemModel(Item.getItemFromBlock(block), meta, modelName);
    }

    private static void registerItemModel(Item item, int meta, String resourcePath) {
        ModelResourceLocation modelResourceLocation = new ModelResourceLocation(resourcePath, "inventory");
        net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation(item, meta, modelResourceLocation);
    }

    public static String getResource(String resource) {
        return (StevesFactoryManager.MODID.toLowerCase() + ":") + resource;
    }
}
