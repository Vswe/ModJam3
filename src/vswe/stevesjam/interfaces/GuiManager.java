package vswe.stevesjam.interfaces;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import vswe.stevesjam.StevesJam;
import vswe.stevesjam.blocks.TileEntityManager;
import vswe.stevesjam.components.FlowComponent;
import vswe.stevesjam.network.DataBitHelper;
import vswe.stevesjam.network.DataWriter;
import vswe.stevesjam.network.PacketHandler;

import java.util.Arrays;
import java.util.List;


public class GuiManager extends GuiContainer {
    private static boolean disableInBoundsCheck;

    public GuiManager(TileEntityManager manager, InventoryPlayer player) {
        super(new ContainerManager(manager, player));

        xSize = 512;
        ySize = 256;

        this.manager = manager;
    }

    private static final ResourceLocation BACKGROUND_1 = registerTexture("Background1");
    private static final ResourceLocation BACKGROUND_2 = registerTexture("Background2");
    private static final ResourceLocation COMPONENTS = registerTexture("FlowComponents");

    public static final int BUTTON_SIZE_W = 14;
    public static final int BUTTON_SIZE_H = 14;
    public static final int BUTTON_SRC_X = 242;
    public static final int BUTTON_SRC_Y = 0;
    public static final int BUTTON_INNER_SIZE_W = 12;
    public static final int BUTTON_INNER_SIZE_H = 12;
    public static final int BUTTON_INNER_SRC_X = 230;
    public static final int BUTTON_INNER_SRC_Y = 0;

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
            int srcButtonY = GuiManager.inBounds(button.getX(), button.getY(), GuiManager.BUTTON_SIZE_W, GuiManager.BUTTON_SIZE_H, x, y) ? 1 : 0;

            drawTexture(button.getX(), button.getY(), GuiManager.BUTTON_SRC_X, GuiManager.BUTTON_SRC_Y + srcButtonY * GuiManager.BUTTON_SIZE_H, GuiManager.BUTTON_SIZE_W, GuiManager.BUTTON_SIZE_H);
            drawTexture(button.getX() + 1, button.getY() + 1, GuiManager.BUTTON_INNER_SRC_X, GuiManager.BUTTON_INNER_SRC_Y + i * GuiManager.BUTTON_INNER_SIZE_H, GuiManager.BUTTON_INNER_SIZE_W, GuiManager.BUTTON_INNER_SIZE_H);
        }


        for (int i = 0; i < manager.getZLevelRenderingList().size(); i++) {
            FlowComponent itemBase = manager.getZLevelRenderingList().get(i);
            GL11.glPushMatrix();
            GL11.glTranslatef(0, 0, (manager.getZLevelRenderingList().size() - i) * Z_LEVEL_COMPONENT_DIFFERENCE);

            itemBase.draw(this, x, y, i);

            GL11.glPopMatrix();
            if (itemBase.isBeingMoved() || inBounds(itemBase.getX(), itemBase.getY(), itemBase.getComponentWidth(), itemBase.getComponentHeight(), x, y)) {
                disableInBoundsCheck = true;
            }
        }
        disableInBoundsCheck = false;

        for (TileEntityManager.Button button : manager.buttons) {
            if (inBounds(button.getX(), button.getY(), BUTTON_SIZE_W, BUTTON_SIZE_H, x, y)) {
                drawMouseOver(button.getMouseOver(), x, y);
            }
        }

        for (FlowComponent itemBase : manager.getZLevelRenderingList()) {
            itemBase.drawMouseOver(this, x, y);
            if (itemBase.isBeingMoved() || inBounds(itemBase.getX(), itemBase.getY(), itemBase.getComponentWidth(), itemBase.getComponentHeight(), x, y)) {
                disableInBoundsCheck = true;
            }
        }
        disableInBoundsCheck = false;

    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
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
            if (inBounds(guiButton.getX(), guiButton.getY(), BUTTON_SIZE_W, BUTTON_SIZE_H, x, y) && guiButton.activateOnRelease() == release) {
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
        x -= guiLeft;
        y -= guiTop;

        for (FlowComponent itemBase : manager.getZLevelRenderingList()) {
            itemBase.onDrag(x, y);
        }
    }

    @Override
    protected void mouseMovedOrUp(int x, int y, int button) {
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
        drawTexturedModalRect(guiLeft + x, guiTop + y, srcX, srcY, w, h);
    }

    public static void bindTexture(ResourceLocation resource)  {
        Minecraft.getMinecraft().getTextureManager().bindTexture(resource);
    }

    public static ResourceLocation registerTexture(String name) {
        return new ResourceLocation(StevesJam.RESOURCE_LOCATION, "textures/gui/" +  name + ".png");
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

        itemRenderer.zLevel = 50F;


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
        if (disableInBoundsCheck) {
            return false;
        }
        return leftX <= mX && mX <= leftX + width && topY <= mY && mY <= topY + height;
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


}
