package vswe.stevesfactory;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import vswe.stevesfactory.blocks.ModBlocks;
import vswe.stevesfactory.components.ModItemHelper;
import vswe.stevesfactory.network.FileHelper;
import vswe.stevesfactory.network.PacketEventHandler;
import vswe.stevesfactory.proxy.CommonProxy;

@Mod(modid = StevesFactoryManager.MODID, name = "Steve's Factory Manager", version = GeneratedInfo.version)
public class StevesFactoryManager {


    public static final String MODID = "StevesFactoryManager";
    public static final String RESOURCE_LOCATION = "stevesfactorymanager";
    public static final String CHANNEL = "FactoryManager";
    public static final String UNLOCALIZED_START = "sfm.";

    public static FMLEventChannel packetHandler;

    @SidedProxy(clientSide = "vswe.stevesfactory.proxy.ClientProxy", serverSide = "vswe.stevesfactory.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance(MODID)
    public static StevesFactoryManager instance;


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        packetHandler = NetworkRegistry.INSTANCE.newEventDrivenChannel(CHANNEL);

        ModBlocks.init();

        proxy.preInit();

        FileHelper.setConfigDir(event.getModConfigurationDirectory());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        packetHandler.register(new PacketEventHandler());

        ModBlocks.addRecipes();
        //new ChatListener();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

        FMLInterModComms.sendMessage("Waila", "register", "vswe.stevesfactory.waila.Provider.callbackRegister");
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        ModItemHelper.init();
    }


}
