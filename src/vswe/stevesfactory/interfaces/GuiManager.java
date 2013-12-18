package vswe.stevesfactory.interfaces;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import vswe.stevesfactory.CollisionHelper;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.blocks.TileEntityManager;
import vswe.stevesfactory.components.FlowComponent;
import vswe.stevesfactory.network.DataBitHelper;
import vswe.stevesfactory.network.DataWriter;
import vswe.stevesfactory.network.PacketHandler;

import java.util.Arrays;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiManager extends net.minecraft.client.gui.inventory.GuiContainer {

    public GuiManager(TileEntityManager manager, InventoryPlayer player) {
        super(new ContainerManager(manager, player));

        xSize = 512;
        ySize = 256;

        this.manager = manager;
    }

    private static final ResourceLocation BACKGROUND_1 = registerTexture("Background1");
    private static final ResourceLocation BACKGROUND_2 = registerTexture("Background2");
    private static final ResourceLocation COMPONENTS = registerTexture("FlowComponents");

    public static int Z_LEVEL_COMPONENT_DIFFERENCE = 100;

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
        for (int i = 0; i < manager.buttons.size(); i++) {
            TileEntityManager.Button button = manager.buttons.get(i);
            int srcButtonY = CollisionHelper.inBounds(button.getX(), button.getY(), TileEntityManager.BUTTON_SIZE_W, TileEntityManager.BUTTON_SIZE_H, x, y) ? 1 : 0;

            drawTexture(button.getX(), button.getY(), TileEntityManager.BUTTON_SRC_X, TileEntityManager.BUTTON_SRC_Y + srcButtonY * TileEntityManager.BUTTON_SIZE_H, TileEntityManager.BUTTON_SIZE_W, TileEntityManager.BUTTON_SIZE_H);
            drawTexture(button.getX() + 1, button.getY() + 1, TileEntityManager.BUTTON_INNER_SRC_X, TileEntityManager.BUTTON_INNER_SRC_Y + i * TileEntityManager.BUTTON_INNER_SIZE_H, TileEntityManager.BUTTON_INNER_SIZE_W, TileEntityManager.BUTTON_INNER_SIZE_H);
        }


        for (int i = 0; i < manager.getZLevelRenderingList().size(); i++) {
            FlowComponent itemBase = manager.getZLevelRenderingList().get(i);
            GL11.glPushMatrix();
            GL11.glTranslatef(0, 0, (manager.getZLevelRenderingList().size() - i) * Z_LEVEL_COMPONENT_DIFFERENCE);

            itemBase.draw(this, x, y, i);

            GL11.glPopMatrix();
            if (itemBase.isBeingMoved() || CollisionHelper.inBounds(itemBase.getX(), itemBase.getY(), itemBase.getComponentWidth(), itemBase.getComponentHeight(), x, y)) {
                CollisionHelper.disableInBoundsCheck = true;
            }
        }
        CollisionHelper.disableInBoundsCheck = false;

        for (TileEntityManager.Button button : manager.buttons) {
            if (CollisionHelper.inBounds(button.getX(), button.getY(), TileEntityManager.BUTTON_SIZE_W, TileEntityManager.BUTTON_SIZE_H, x, y)) {
                drawMouseOver(button.getMouseOver(), x, y);
            }
        }

        for (FlowComponent itemBase : manager.getZLevelRenderingList()) {
            itemBase.drawMouseOver(this, x, y);
            if (itemBase.isBeingMoved() || CollisionHelper.inBounds(itemBase.getX(), itemBase.getY(), itemBase.getComponentWidth(), itemBase.getComponentHeight(), x, y)) {
                CollisionHelper.disableInBoundsCheck = true;
            }
        }
        CollisionHelper.disableInBoundsCheck = false;

    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        x = scaleX(x);
        y = scaleY(y);

        super.mouseClicked(x, y, button);

        x -= guiLeft;
        y -= guiTop;

        for (int i = 0; i < manager.getZLevelRenderingList().size(); i++) {
            FlowComponent itemBase = manager.getZLevelRenderingList().get(i);
            if (itemBase.onClick(x, y, button)) {
                manager.getZLevelRenderingList().remove(i);
                manager.getZLevelRenderingList().add(0, itemBase);
                break;
            }
        }


        onClickButtonCheck(x, y, false);
    }

    private void onClickButtonCheck(int x, int y, boolean release) {
        for (int i = 0; i < manager.buttons.size(); i++) {
            TileEntityManager.Button guiButton = manager.buttons.get(i);
            if (CollisionHelper.inBounds(guiButton.getX(), guiButton.getY(), TileEntityManager.BUTTON_SIZE_W, TileEntityManager.BUTTON_SIZE_H, x, y) && guiButton.activateOnRelease() == release) {
                DataWriter dw = PacketHandler.getButtonPacketWriter();
                dw.writeData(i, DataBitHelper.GUI_BUTTON_ID);
                guiButton.onClick(dw);
                PacketHandler.sendDataToServer(dw);
                break;
            }
        }
    }

    @Override
    protected void mouseClickMove(int x, int y, int button, long ticks) {
        x = scaleX(x);
        y = scaleY(y);

        super.mouseClickMove(x, y, button, ticks);

        x -= guiLeft;
        y -= guiTop;

        for (FlowComponent itemBase : manager.getZLevelRenderingList()) {
            itemBase.onDrag(x, y);
        }
    }

    @Override
    protected void mouseMovedOrUp(int x, int y, int button) {
        x = scaleX(x);
        y = scaleY(y);

        super.mouseMovedOrUp(x, y, button);

        x -= guiLeft;
        y -= guiTop;

        onClickButtonCheck(x, y, true);

        if (!manager.justSentServerComponentRemovalPacket) {
            for (FlowComponent itemBase : manager.getZLevelRenderingList()) {
                itemBase.onRelease(x, y);
            }
        }

    }


    public void drawTexture(int x, int y, int srcX, int srcY, int w, int h) {

        float scale = getScale();
        float fW = w / scale;
        fW *= this.mc.displayWidth;
        fW = (float)Math.floor(fW);
        fW /= this.mc.displayWidth;
        fW *= scale;

        float fH = h / scale;
        fH *= this.mc.displayHeight;
        fH = (float)Math.floor(fH);
        fH /= this.mc.displayHeight;
        fH *= scale;

        drawScaleFriendlyTexture(guiLeft + x, guiTop + y, srcX, srcY, fW, fH);
    }

    public void drawScaleFriendlyTexture(int x, int y, int srcX, int srcY, float w, float h) {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(x + 0), (double)(y + h), (double)this.zLevel, (double)((float)(srcX + 0) * f), (double)((float)(srcY + h) * f1));
        tessellator.addVertexWithUV((double)(x + w), (double)(y + h), (double)this.zLevel, (double)((float)(srcX + w) * f), (double)((float)(srcY + h) * f1));
        tessellator.addVertexWithUV((double)(x + w), (double)(y + 0), (double)this.zLevel, (double)((float)(srcX + w) * f), (double)((float)(srcY + 0) * f1));
        tessellator.addVertexWithUV((double)(x + 0), (double)(y + 0), (double)this.zLevel, (double)((float)(srcX + 0) * f), (double)((float)(srcY + 0) * f1));
        tessellator.draw();
    }

    public static void bindTexture(ResourceLocation resource)  {
        Minecraft.getMinecraft().getTextureManager().bindTexture(resource);
    }

    public static ResourceLocation registerTexture(String name) {
        return new ResourceLocation(StevesFactoryManager.RESOURCE_LOCATION, "textures/gui/" +  name + ".png");
    }

    private TileEntityManager manager;

    public TileEntityManager getManager() {
        return manager;
    }

    public void drawString(String str, int x, int y, float mult, int color) {
        GL11.glPushMatrix();
        GL11.glScalef(mult, mult, 1F);
        fontRenderer.drawString(str, (int)((x + guiLeft) / mult), (int)((y + guiTop) / mult), color);
        bindTexture(COMPONENTS);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        GL11.glPopMatrix();
    }

    public void drawSplitString(String str, int x, int y, int w, float mult, int color) {
        GL11.glPushMatrix();
        GL11.glScalef(mult, mult, 1F);
        fontRenderer.drawSplitString(str, (int)((x + guiLeft) / mult), (int)((y + guiTop) / mult), (int)(w / mult), color);
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

        itemRenderer.zLevel = 1F;


        try {
            ItemRenderHelper.renderItemIntoGUI(itemRenderer, this.mc.getTextureManager(), itemstack, x + guiLeft, y + guiTop);
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

    public int getStringWidth(String str) {
        return fontRenderer.getStringWidth(str);
    }

    @Override
    protected void keyTyped(char c, int k) {
        for (FlowComponent itemBase : manager.getFlowItems()) {
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

        //GL11.glEnable(GL11.GL_BLEND);
        //GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_DST_COLOR);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glLineWidth(5);

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2i(guiLeft + x1, guiTop + y1);
        GL11.glVertex2i(guiLeft + x2, guiTop + y2);
        GL11.glEnd();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
    }

    @Override
    public void drawDefaultBackground() {
        super.drawDefaultBackground();

        startScaling();
    }

    @Override
    public void drawScreen(int x, int y, float f) {

        super.drawScreen(scaleX(x), scaleY(y), f);

        stopScaling();
    }

    private void startScaling() {
        //start scale
        GL11.glPushMatrix();

        float scale = getScale();

        GL11.glScalef(scale, scale, 1);
        GL11.glTranslatef(-guiLeft, -guiTop, 0.0F);
        GL11.glTranslatef((this.width - this.xSize * scale) / (2 * scale), (this.height - this.ySize * scale) / (2 * scale), 0.0F);
    }

    private void stopScaling() {
        //stop scale
        GL11.glPopMatrix();
    }

    protected float getScale() {

        net.minecraft.client.gui.ScaledResolution scaledresolution = new net.minecraft.client.gui.ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
        float w = scaledresolution.getScaledWidth() * 0.9F;
        float h = scaledresolution.getScaledHeight() * 0.9F;
        float multX = w / xSize;
        float multY = h / ySize;
        float mult = Math.min(multX, multY);
        if (mult > 1F) {
            mult = 1F;
        }

        return mult;
    }

    private int scaleX(float x) {
        float scale = getScale();
        x /= scale;
        x += guiLeft;
        x -= (this.width - this.xSize * scale) / (2 * scale);
        return (int)x;
    }
    private int scaleY(float y) {
        float scale = getScale();
        y /= scale;
        y += guiTop;
        y -= (this.height - this.ySize * scale) / (2 * scale);
        return (int)y;
    }

}
