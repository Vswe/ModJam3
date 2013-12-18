package vswe.stevesfactory;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import vswe.stevesfactory.blocks.Blocks;
import vswe.stevesfactory.configs.ConfigHandler;
import vswe.stevesfactory.network.PacketHandler;
import vswe.stevesfactory.proxy.CommonProxy;

@Mod(modid = "StevesFactoryManager", name = "Steve's Factory Manager", version = GeneratedInfo.version)
@NetworkMod(channels = {StevesFactoryManager.CHANNEL}, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketHandler.class)
public class StevesFactoryManager {


    public static final String RESOURCE_LOCATION = "stevesfactory";
    public static final String CHANNEL = "FactoryManager";

    @SidedProxy(clientSide = "vswe.stevesfactory.proxy.ClientProxy", serverSide = "vswe.stevesfactory.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance("StevesFactoryManager")
    public static StevesFactoryManager instance;


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigHandler config = new ConfigHandler(event.getSuggestedConfigurationFile());

        Blocks.init();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        Blocks.addNames();
        Blocks.addRecipes();

        NetworkRegistry.instance().registerGuiHandler(this, new GuiHandler());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }

}
