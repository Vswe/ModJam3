package vswe.stevesjam.interfaces;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.ResourceManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import vswe.stevesjam.StevesJam;
import vswe.stevesjam.blocks.TileEntityJam;


public class GuiJam extends GuiContainer {
    public GuiJam(TileEntityJam jam, InventoryPlayer player) {
        super(new ContainerJam(jam, player));

        xSize = 512;
        ySize = 256;
    }


    private static final ResourceLocation BACKGROUND_1 = registerTexture("Background1");
    private static final ResourceLocation BACKGROUND_2 = registerTexture("Background2");

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        bindTexture(BACKGROUND_1);
        drawTexture(0, 0, 0, 0, 256, 256);

        bindTexture(BACKGROUND_2);
        drawTexture(256, 0, 0, 0, 256, 256);
    }




    private void drawTexture(int x, int y, int srcX, int srcY, int w, int h) {
        drawTexturedModalRect(guiLeft + x, guiTop + y, srcX, srcY, w, h);
    }

    private static void bindTexture(ResourceLocation resource)  {
        Minecraft.getMinecraft().getTextureManager().bindTexture(resource);
    }

    private static ResourceLocation registerTexture(String name) {
        return new ResourceLocation(StevesJam.RESOURCE_LOCATION, "textures/gui/" +  name + ".png");
    }
}
