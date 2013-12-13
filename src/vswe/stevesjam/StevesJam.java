package vswe.stevesjam;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import vswe.stevesjam.blocks.Blocks;
import vswe.stevesjam.configs.ConfigHandler;
import vswe.stevesjam.network.PacketHandler;
import vswe.stevesjam.proxy.CommonProxy;

@Mod(modid = "StevesJam", name = "Steve's Jam", version = "Pancakes")
@NetworkMod(channels = {"Jam"}, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketHandler.class)
public class StevesJam {



    @SidedProxy(clientSide = "vswe.stevesjam.proxy.ClientProxy", serverSide = "vswe.stevesjam.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance("StevesJam")
    public static StevesJam instance;


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
