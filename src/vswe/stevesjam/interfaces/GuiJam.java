package vswe.stevesjam.interfaces;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import vswe.stevesjam.StevesJam;
import vswe.stevesjam.blocks.TileEntityJam;
import vswe.stevesjam.components.FlowComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class GuiJam extends GuiContainer {
    public GuiJam(TileEntityJam jam, InventoryPlayer player) {
        super(new ContainerJam(jam, player));

        xSize = 512;
        ySize = 256;

        this.jam = jam;
    }

    private static final ResourceLocation BACKGROUND_1 = registerTexture("Background1");
    private static final ResourceLocation BACKGROUND_2 = registerTexture("Background2");
    private static final ResourceLocation COMPONENTS = registerTexture("FlowComponents");

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        bindTexture(BACKGROUND_1);
        drawTexture(0, 0, 0, 0, 256, 256);

        bindTexture(BACKGROUND_2);
        drawTexture(256, 0, 0, 0, 256, 256);

        x -= guiLeft;
        y -= guiTop;

        bindTexture(COMPONENTS);
        for (FlowComponent itemBase : jam.getFlowItems()) {
            itemBase.draw(this, x, y);
        }
        for (FlowComponent itemBase : jam.getFlowItems()) {
            itemBase.drawMouseOver(this, x, y);
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        super.mouseClicked(x, y, button);

        x -= guiLeft;
        y -= guiTop;

        for (FlowComponent itemBase : jam.getFlowItems()) {
            itemBase.onClick(x, y, button);
        }
    }

    @Override
    protected void mouseClickMove(int x, int y, int button, long ticks) {
        x -= guiLeft;
        y -= guiTop;

        for (FlowComponent itemBase : jam.getFlowItems()) {
            itemBase.onDrag(x, y);
        }
    }

    @Override
    protected void mouseMovedOrUp(int x, int y, int button) {
        x -= guiLeft;
        y -= guiTop;

        for (FlowComponent itemBase : jam.getFlowItems()) {
            itemBase.onRelease(x, y);
        }
    }


    public void drawTexture(int x, int y, int srcX, int srcY, int w, int h) {
        drawTexturedModalRect(guiLeft + x, guiTop + y, srcX, srcY, w, h);
    }

    public static void bindTexture(ResourceLocation resource)  {
        Minecraft.getMinecraft().getTextureManager().bindTexture(resource);
    }

    public static ResourceLocation registerTexture(String name) {
        return new ResourceLocation(StevesJam.RESOURCE_LOCATION, "textures/gui/" +  name + ".png");
    }

    private TileEntityJam jam;

    public TileEntityJam getJam() {
        return jam;
    }

    public void drawString(String str, int x, int y, float mult, int color) {
        GL11.glPushMatrix();
        GL11.glScalef(mult, mult, 1F);
        fontRenderer.drawString(str, (int)((x + guiLeft) / mult), (int)((y + guiTop) / mult), color);
        bindTexture(COMPONENTS);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        GL11.glPopMatrix();
    }

    public void drawString(String str, int x, int y, int color) {
        drawString(str, x, y, 1F, color);
    }

    public void drawMouseOver(String str, int x, int y) {
       drawMouseOver(Arrays.asList(str.split("\n")), x, y);
    }

    public void drawMouseOver(List lst, int x, int y) {
        drawHoveringText(lst, x + guiLeft, y + guiTop, fontRenderer);
    }

    public void drawItemStack(ItemStack itemstack, int x, int y) {
        GL11.glPushMatrix();

        RenderHelper.enableGUIStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glEnable(GL11.GL_LIGHTING);

        itemRenderer.zLevel = 100.0F;
        try {
            itemRenderer.renderItemAndEffectIntoGUI(this.fontRenderer, this.mc.getTextureManager(), itemstack, x + guiLeft, y + guiTop);
        }catch (Exception ex) {
            if (itemstack.getItemDamage() != 0) {
                ItemStack newStack = itemstack.copy();
                newStack.setItemDamage(0);
                drawItemStack(newStack, x, y);
            }
        }finally {
            itemRenderer.zLevel = 0F;

            bindTexture(COMPONENTS);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(GL11.GL_LIGHTING);

            GL11.glPopMatrix();
        }

    }

    public static boolean inBounds(int leftX, int topY, int width, int height, int mX, int mY) {
        return leftX <= mX && mX <= leftX + width && topY <= mY && mY <= topY + height;
    }

    public int getStringWidth(String str) {
        return fontRenderer.getStringWidth(str);
    }

    @Override
    protected void keyTyped(char c, int k) {
        for (FlowComponent itemBase : jam.getFlowItems()) {
            if (itemBase.onKeyStroke(this, c, k) && k != 1) {
                return;
            }
        }

        super.keyTyped(c, k);
    }

    public void drawCenteredString(String str, int x, int y, float mult, int width, int color) {
        drawString(str, x + (width - (int)(getStringWidth(str) * mult)) / 2, y, mult, color);
    }

    public void drawCursor(int x, int y, int z, int color) {
        GL11.glPushMatrix();
        GL11.glTranslatef(0, 0, z);
        x += guiLeft;
        y += guiTop;
        Gui.drawRect(x , y + 1 , x + 1, y + 10, color);
        GL11.glPopMatrix();
    }


    public void drawLine(int x1, int y1, int x2, int y2) {
        GL11.glPushMatrix();

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(0.4F, 0.4F, 0.4F, 1F);

        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glLineWidth(5);

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2i(guiLeft + x1, guiTop + y1);
        GL11.glVertex2i(guiLeft + x2, guiTop + y2);
        GL11.glEnd();

        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
    }
}
