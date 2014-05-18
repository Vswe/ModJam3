package vswe.stevesfactory;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import vswe.stevesfactory.blocks.ModBlocks;
import vswe.stevesfactory.components.ModItemHelper;
import vswe.stevesfactory.network.FileHelper;
import vswe.stevesfactory.network.PacketPipeline;
import vswe.stevesfactory.proxy.CommonProxy;

@Mod(modid = "StevesFactoryManager", name = "Steve's Factory Manager", version = GeneratedInfo.version)
public class StevesFactoryManager {


    public static final String RESOURCE_LOCATION = "stevesfactory";
    public static final String CHANNEL = "FactoryManager";
    public static final boolean GREEN_SCREEN_MODE = false;
    public static final String UNLOCALIZED_START = "sfm.";
    public static PacketPipeline pipeline = new PacketPipeline();

    @SidedProxy(clientSide = "vswe.stevesfactory.proxy.ClientProxy", serverSide = "vswe.stevesfactory.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance("StevesFactoryManager")
    public static StevesFactoryManager instance;


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        FileHelper.setConfigDir(event.getModConfigurationDirectory());

        ModBlocks.init();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
        pipeline.initalise();

        ModBlocks.addRecipes();

        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

        FMLInterModComms.sendMessage("Waila", "register", "vswe.stevesfactory.waila.Provider.callbackRegister");
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        pipeline.postInitialise();
        ModItemHelper.init();
    }


}
