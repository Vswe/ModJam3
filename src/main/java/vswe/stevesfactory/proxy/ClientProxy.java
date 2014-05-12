package vswe.stevesfactory.proxy;


import cpw.mods.fml.client.registry.RenderingRegistry;
import vswe.stevesfactory.blocks.SetupBlock;
import vswe.stevesfactory.blocks.RenderCamouflage;
import vswe.stevesfactory.settings.Settings;

public class ClientProxy extends CommonProxy {
    @Override
    public void init() {
        RenderCamouflage camouflage = new RenderCamouflage();
        SetupBlock.CAMOUFLAGE_RENDER_ID = camouflage.getRenderId();
        RenderingRegistry.registerBlockHandler(camouflage);
        Settings.load();
    }
}
