package vswe.stevesfactory.interfaces;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import vswe.stevesfactory.CollisionHelper;
import vswe.stevesfactory.Localization;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.blocks.TileEntityManager;
import vswe.stevesfactory.components.FlowComponent;
import vswe.stevesfactory.network.DataBitHelper;
import vswe.stevesfactory.network.DataWriter;
import vswe.stevesfactory.network.PacketHandler;

import java.util.Arrays;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiManager extends GuiBase {

    public GuiManager(TileEntityManager manager, InventoryPlayer player) {
        super(new ContainerManager(manager, player));

        xSize = 512;
        ySize = 256;

        this.manager = manager;
    }

    private static final ResourceLocation BACKGROUND_1 = registerTexture("Background1");
    private static final ResourceLocation BACKGROUND_2 = registerTexture("Background2");
    private static final ResourceLocation COMPONENTS = registerTexture("FlowComponents");

    @Override
    protected ResourceLocation getComponentResource() {
        return COMPONENTS;
    }

    public static int Z_LEVEL_COMPONENT_OPEN_DIFFERENCE = 100;
    public static int Z_LEVEL_COMPONENT_CLOSED_DIFFERENCE = 1;
    public static int Z_LEVEL_COMPONENT_START = 750;
    public static int Z_LEVEL_OPEN_MAXIMUM = 5;

    @Override
    public void drawWorldBackground(int val) {
        if (StevesFactoryManager.GREEN_SCREEN_MODE) {
            drawRect(0, 0, width, height, 0xFF00FF00);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }else{
            super.drawWorldBackground(val);
        }
    }



    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {



        if (!StevesFactoryManager.GREEN_SCREEN_MODE) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            bindTexture(BACKGROUND_1);
            drawTexture(0, 0, 0, 0, 256, 256);

            bindTexture(BACKGROUND_2);
            drawTexture(256, 0, 0, 0, 256, 256);
        }

        x -= guiLeft;
        y -= guiTop;

        bindTexture(COMPONENTS);
        for (int i = 0; i < manager.buttons.size(); i++) {
            TileEntityManager.Button button = manager.buttons.get(i);
            if (button.isVisible()) {
                int srcButtonY = CollisionHelper.inBounds(button.getX(), button.getY(), TileEntityManager.BUTTON_SIZE_W, TileEntityManager.BUTTON_SIZE_H, x, y) ? 1 : 0;

                drawTexture(button.getX(), button.getY(), TileEntityManager.BUTTON_SRC_X, TileEntityManager.BUTTON_SRC_Y + srcButtonY * TileEntityManager.BUTTON_SIZE_H, TileEntityManager.BUTTON_SIZE_W, TileEntityManager.BUTTON_SIZE_H);
                drawTexture(button.getX() + 1, button.getY() + 1, TileEntityManager.BUTTON_INNER_SRC_X, TileEntityManager.BUTTON_INNER_SRC_Y + i * TileEntityManager.BUTTON_INNER_SIZE_H, TileEntityManager.BUTTON_INNER_SIZE_W, TileEntityManager.BUTTON_INNER_SIZE_H);
            }
        }

        //update components completely independent on their visibility
        for (FlowComponent component : manager.getFlowItems()) {
            component.update();
        }

        int zLevel = Z_LEVEL_COMPONENT_START;
        int openCount = 0;
        for (int i = 0; i < manager.getZLevelRenderingList().size(); i++) {
            FlowComponent itemBase = manager.getZLevelRenderingList().get(i);

            if (itemBase.isVisible()) {
                if (itemBase.isOpen() && openCount == Z_LEVEL_OPEN_MAXIMUM) {
                    itemBase.close();
                }

                if (itemBase.isOpen()) {
                    zLevel -= Z_LEVEL_COMPONENT_OPEN_DIFFERENCE;
                    openCount++;
                }else{
                    zLevel -= Z_LEVEL_COMPONENT_CLOSED_DIFFERENCE;
                }
                itemBase.draw(this, x, y, zLevel);

                if (itemBase.isBeingMoved() || CollisionHelper.inBounds(itemBase.getX(), itemBase.getY(), itemBase.getComponentWidth(), itemBase.getComponentHeight(), x, y)) {
                    CollisionHelper.disableInBoundsCheck = true;
                }
            }
        }
        CollisionHelper.disableInBoundsCheck = false;

        if (!StevesFactoryManager.GREEN_SCREEN_MODE) {
            drawString(getInfo(), 5, ySize - 13, 1F, 0x606060);

            for (TileEntityManager.Button button : manager.buttons) {
                if (button.isVisible() && CollisionHelper.inBounds(button.getX(), button.getY(), TileEntityManager.BUTTON_SIZE_W, TileEntityManager.BUTTON_SIZE_H, x, y)) {
                    drawMouseOver(button.getMouseOver(), x, y);
                }
            }

            for (FlowComponent itemBase : manager.getZLevelRenderingList()) {
                if (itemBase.isVisible()) {
                    itemBase.drawMouseOver(this, x, y);
                    if (itemBase.isBeingMoved() || CollisionHelper.inBounds(itemBase.getX(), itemBase.getY(), itemBase.getComponentWidth(), itemBase.getComponentHeight(), x, y)) {
                        CollisionHelper.disableInBoundsCheck = true;
                    }
                }
            }
        }
        CollisionHelper.disableInBoundsCheck = false;

    }

    private String getInfo() {
        String ret = Localization.COMMANDS.toString() + ": " + manager.getFlowItems().size() + "  ";

        String path = "";
        FlowComponent component = manager.getSelectedComponent();

        if (component != null) {
            ret += "|";
        }
        while (component != null) {
            String nextPath = "> " + component.getName() + " " + path;
            if (getStringWidth(ret + nextPath) > xSize - 15) {
                path = "... " + path;
                break;
            }
            path = nextPath;
            component = component.getParent();
        }
        ret += path;

        return ret;
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
            if (itemBase.isVisible() && itemBase.onClick(x, y, button)) {
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
            if (guiButton.isVisible() && CollisionHelper.inBounds(guiButton.getX(), guiButton.getY(), TileEntityManager.BUTTON_SIZE_W, TileEntityManager.BUTTON_SIZE_H, x, y) && guiButton.activateOnRelease() == release) {
                DataWriter dw = PacketHandler.getButtonPacketWriter();
                dw.writeData(i, DataBitHelper.GUI_BUTTON_ID);
                if(guiButton.onClick(dw)) {
                    PacketHandler.sendDataToServer(dw);
                }
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
            if (itemBase.isVisible()) {
                itemBase.onDrag(x, y);
            }
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
                if (itemBase.isVisible()) {
                    itemBase.onRelease(x, y, button);
                }
            }
        }

        for (FlowComponent itemBase : manager.getZLevelRenderingList()) {
            if (itemBase.isVisible()) {
                itemBase.postRelease();
            }
        }

    }

    @Override
    protected void keyTyped(char c, int k) {
        for (FlowComponent itemBase : manager.getFlowItems()) {
            if (itemBase.isVisible() && itemBase.onKeyStroke(this, c, k) && k != 1) {
                return;
            }
        }

        super.keyTyped(c, k);
    }


    private TileEntityManager manager;

    public TileEntityManager getManager() {
        return manager;
    }


}
