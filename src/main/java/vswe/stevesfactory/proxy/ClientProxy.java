package vswe.stevesfactory.proxy;


import cpw.mods.fml.client.registry.RenderingRegistry;
import vswe.stevesfactory.blocks.ModBlocks;
import vswe.stevesfactory.blocks.RenderCamouflage;
import vswe.stevesfactory.settings.Settings;

public class ClientProxy extends CommonProxy {
    @Override
    public void init() {
        RenderCamouflage camouflage = new RenderCamouflage();
        ModBlocks.CAMOUFLAGE_RENDER_ID = camouflage.getRenderId();
        RenderingRegistry.registerBlockHandler(camouflage);
        Settings.load();
    }
}
