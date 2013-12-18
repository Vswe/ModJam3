package vswe.stevesfactory.components;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import vswe.stevesfactory.CollisionHelper;
import vswe.stevesfactory.interfaces.ContainerManager;
import vswe.stevesfactory.interfaces.GuiManager;
import vswe.stevesfactory.network.DataBitHelper;
import vswe.stevesfactory.network.DataReader;
import vswe.stevesfactory.network.DataWriter;
import vswe.stevesfactory.network.PacketHandler;

import java.util.List;

public class ComponentMenuInventory extends ComponentMenu {


    private static final int ARROW_SIZE_W = 6;
    private static final int ARROW_SIZE_H = 10;
    private static final int ARROW_SRC_X = 18;
    private static final int ARROW_SRC_Y = 20;
    private static final int ARROW_X_LEFT = 3;
    private static final int ARROW_X_RIGHT = 111;
    private static final int ARROW_Y = 7;

    private static final int MAX_INVENTORIES = 5;
    private static final int INVENTORY_SIZE = 16;
    private static final int INVENTORY_SIZE_W_WITH_MARGIN = 20;
    private static final int INVENTORY_X = 12;
    private static final int INVENTORY_Y = 5;
    private static final int INVENTORY_SRC_X = 30;
    private static final int INVENTORY_SRC_Y = 20;


    private int selectedInventory = -1;
    private List<TileEntity> inventories;

    public ComponentMenuInventory(FlowComponent parent) {
        super(parent);
    }

    @Override
    public String getName() {
        return "Inventory";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY) {
        inventories = gui.getManager().getConnectedInventories();

        canScroll = inventories.size() > MAX_INVENTORIES;

        if (!canScroll) {
            offset = 0;
        }

        for (int i = 0; i < inventories.size(); i++) {
            TileEntity te = inventories.get(i);
            int x = getInventoryPosition(i);

            if (x > ARROW_X_LEFT + ARROW_SIZE_W && x + INVENTORY_SIZE < ARROW_X_RIGHT) {


                int srcInventoryX = i == selectedInventory ? 1 : 0;
                int srcInventoryY = CollisionHelper.inBounds(x, INVENTORY_Y, INVENTORY_SIZE, INVENTORY_SIZE, mX, mY) ? 1 : 0;

                gui.drawTexture(x, INVENTORY_Y, INVENTORY_SRC_X + srcInventoryX * INVENTORY_SIZE, INVENTORY_SRC_Y + srcInventoryY * INVENTORY_SIZE, INVENTORY_SIZE, INVENTORY_SIZE);
                gui.drawItemStack(new ItemStack(te.getBlockType(), 1, te.getBlockMetadata()), x, INVENTORY_Y);
            }
        }

        drawArrow(gui, false, mX, mY);
        drawArrow(gui, true, mX, mY);

        if (clicked) {
            offset += dir;
            int min = (inventories.size() - MAX_INVENTORIES) * -INVENTORY_SIZE_W_WITH_MARGIN;
            int max = 0;

            if (offset < min) {
                offset = min;
            }else if(offset > max) {
                offset = max;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawMouseOver(GuiManager gui, int mX, int mY) {

        for (int i = 0; i < inventories.size(); i++) {
            TileEntity te = inventories.get(i);
            int x = getInventoryPosition(i);

            if (x > ARROW_X_LEFT + ARROW_SIZE_W && x + INVENTORY_SIZE < ARROW_X_RIGHT) {
                if (CollisionHelper.inBounds(x, INVENTORY_Y, INVENTORY_SIZE, INVENTORY_SIZE, mX, mY)) {
                    String name = ((IInventory)te).getInvName();

                    if (!((IInventory)te).isInvNameLocalized()) {
                        name = StatCollector.translateToLocal(name);
                    }

                    String str = name;
                    str += "\nX: " + te.xCoord + " Y: " + te.yCoord + " Z: " + te.zCoord;
                    str += "\n" + (int)Math.round(Math.sqrt(gui.getManager().getDistanceFrom(te.xCoord + 0.5, te.yCoord + 0.5, te.zCoord + 0.5))) + " block(s) away";


                    gui.drawMouseOver(str, mX, mY);
                }
            }
        }
    }

    private boolean clicked;
    private int dir;
    private int offset;
    private boolean canScroll;

    @SideOnly(Side.CLIENT)
    private void drawArrow(GuiManager gui, boolean right, int mX, int mY) {
        int srcArrowX = right ? 1 : 0;
        int srcArrowY = canScroll ? clicked && right == (dir == -1) ? 2 : inArrowBounds(right, mX, mY) ? 1 : 0 : 3;

        gui.drawTexture(right ? ARROW_X_RIGHT : ARROW_X_LEFT, ARROW_Y, ARROW_SRC_X + srcArrowX * ARROW_SIZE_W, ARROW_SRC_Y + srcArrowY * ARROW_SIZE_H, ARROW_SIZE_W, ARROW_SIZE_H);
    }

    private boolean inArrowBounds(boolean right, int mX, int mY) {
        return CollisionHelper.inBounds(right ? ARROW_X_RIGHT : ARROW_X_LEFT, ARROW_Y, ARROW_SIZE_W, ARROW_SIZE_H, mX, mY);
    }

    @Override
    public void onClick(int mX, int mY, int button) {
        if (canScroll) {
            if (inArrowBounds(true, mX, mY)) {
                clicked = true;
                dir = -1;
                DataWriter dw = new DataWriter();
                dw.writeBoolean(true);
                PacketHandler.sendDataToServer(dw);
            }else if (inArrowBounds(false, mX, mY)) {
                clicked = true;
                dir = 1;
            }
        }

        if (inventories != null) {
            for (int i = 0; i < inventories.size(); i++) {
                TileEntity te = inventories.get(i);
                int x = getInventoryPosition(i);

                if (x > ARROW_X_LEFT + ARROW_SIZE_W && x + INVENTORY_SIZE < ARROW_X_RIGHT) {
                    if (CollisionHelper.inBounds(x, INVENTORY_Y, INVENTORY_SIZE, INVENTORY_SIZE, mX, mY)) {
                        int temp;
                        if (selectedInventory == i){
                            setSelectedInventoryAndSync(-1);
                        }else{
                            setSelectedInventoryAndSync(i);
                        }

                        break;
                    }
                }
            }
        }

    }

    @Override
    public void onDrag(int mX, int mY) {

    }

    @Override
    public void onRelease(int mX, int mY) {
        clicked = false;
    }

    @Override
    public void writeData(DataWriter dw) {
        writeData(dw, selectedInventory);
    }

    @Override
    public void readData(DataReader dr) {
        readTheData(dr);
    }

    @Override
    public void copyFrom(ComponentMenu menu) {
        selectedInventory = ((ComponentMenuInventory)menu).selectedInventory;
    }

    @Override
    public void refreshData(ContainerManager container, ComponentMenu newData) {
        ComponentMenuInventory newDataInv = ((ComponentMenuInventory)newData);

        if (selectedInventory != newDataInv.selectedInventory) {
            selectedInventory = newDataInv.selectedInventory;

            DataWriter dw = getWriterForClientComponentPacket(container);
            writeData(dw, selectedInventory);
            PacketHandler.sendDataToListeningClients(container, dw);
        }
    }

    @Override
    public void readNetworkComponent(DataReader dr) {
        readTheData(dr);
    }

    private void readTheData(DataReader dr) {
        selectedInventory = dr.readData(DataBitHelper.MENU_INVENTORY_SELECTION) - 1;
    }

    private void writeData(DataWriter dw, int val) {
        dw.writeData(val + 1, DataBitHelper.MENU_INVENTORY_SELECTION);
    }

    private void setSelectedInventoryAndSync(int val) {
        DataWriter dw = getWriterForServerComponentPacket();
        writeData(dw, val);
        PacketHandler.sendDataToServer(dw);
    }

    private int getInventoryPosition(int i) {
        return INVENTORY_X + i * INVENTORY_SIZE_W_WITH_MARGIN + offset;
    }

    public int getSelectedInventory() {
        return selectedInventory;
    }

    public void setSelectedInventory(int val) {
        selectedInventory = val;
    }

    private static final String NBT_SELECTION = "InventorySelection";

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        setSelectedInventory(nbtTagCompound.getShort(NBT_SELECTION));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
       nbtTagCompound.setShort(NBT_SELECTION, (short)getSelectedInventory());
    }

    @Override
    public void addErrors(List<String> errors) {
        if (selectedInventory == -1) {
            errors.add("No inventory selected");
        }
    }
}
