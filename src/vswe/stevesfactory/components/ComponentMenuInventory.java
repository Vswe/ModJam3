package vswe.stevesfactory.components;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import vswe.stevesfactory.CollisionHelper;
import vswe.stevesfactory.interfaces.ContainerManager;
import vswe.stevesfactory.interfaces.GuiManager;
import vswe.stevesfactory.network.DataBitHelper;
import vswe.stevesfactory.network.DataReader;
import vswe.stevesfactory.network.DataWriter;
import vswe.stevesfactory.network.PacketHandler;

import java.util.ArrayList;
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

    private static final int RADIO_BUTTON_X = 2;
    private static final int RADIO_BUTTON_Y = 27;
    private static final int RADIO_BUTTON_SPACING = 15;


    //private int selectedInventory = -1;
    private List<Integer> selectedInventories;
    private List<TileEntity> inventories;
    protected RadioButtonList radioButtons;

    public ComponentMenuInventory(FlowComponent parent) {
        super(parent);

        selectedInventories = new ArrayList<Integer>();
        radioButtons = new RadioButtonList() {
            @Override
            public void updateSelectedOption(int selectedOption) {
               DataWriter dw = getWriterForServerComponentPacket();
               writeRadioButtonData(dw, selectedOption);
               PacketHandler.sendDataToServer(dw);
            }
        };

        initRadioButtons();
    }

    protected void initRadioButtons() {
        radioButtons.add(new RadioButtonInventory(0, "Run a shared command once"));
        radioButtons.add(new RadioButtonInventory(1, "Run command once per target"));
    }

    protected class RadioButtonInventory extends RadioButton {

        public RadioButtonInventory(int id, String text) {
            super(RADIO_BUTTON_X, RADIO_BUTTON_Y + id * RADIO_BUTTON_SPACING, text);
        }
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


                int srcInventoryX = selectedInventories.contains(i) ? 1 : 0;
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

        if (selectedInventories.size() > 1) {
            radioButtons.draw(gui, mX, mY);
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
                int x = getInventoryPosition(i);

                if (x > ARROW_X_LEFT + ARROW_SIZE_W && x + INVENTORY_SIZE < ARROW_X_RIGHT) {
                    if (CollisionHelper.inBounds(x, INVENTORY_Y, INVENTORY_SIZE, INVENTORY_SIZE, mX, mY)) {
                        setSelectedInventoryAndSync(i, !selectedInventories.contains(i));


                        break;
                    }
                }
            }
        }

        if (selectedInventories.size() > 1) {
            radioButtons.onClick(mX, mY, button);
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
        dw.writeData(getOption(), DataBitHelper.MENU_INVENTORY_MULTI_SELECTION_TYPE);
        dw.writeData(selectedInventories.size(), DataBitHelper.MENU_INVENTORY_SELECTION);
        for (int selectedInventory : selectedInventories) {
            dw.writeData(selectedInventory, DataBitHelper.MENU_INVENTORY_SELECTION);
        }
    }

    @Override
    public void readData(DataReader dr) {
        setOption(dr.readData(DataBitHelper.MENU_INVENTORY_MULTI_SELECTION_TYPE));
        selectedInventories.clear();
        int count = dr.readData(DataBitHelper.MENU_INVENTORY_SELECTION);
        for(int i = 0; i < count; i++) {
            selectedInventories.add(dr.readData(DataBitHelper.MENU_INVENTORY_SELECTION));
        }
    }

    @Override
    public void copyFrom(ComponentMenu menu) {
        setOption(((ComponentMenuInventory) menu).getOption());
        selectedInventories.clear();
        for (int selectedInventory : ((ComponentMenuInventory)menu).selectedInventories) {
            selectedInventories.add(selectedInventory);
        }
    }

    @Override
    public void refreshData(ContainerManager container, ComponentMenu newData) {
        ComponentMenuInventory newDataInv = ((ComponentMenuInventory)newData);

        if (newDataInv.getOption() != getOption()) {
            setOption(newDataInv.getOption());

            DataWriter dw = getWriterForClientComponentPacket(container);
            writeRadioButtonData(dw, getOption());
            PacketHandler.sendDataToListeningClients(container, dw);
        }

        int count = newDataInv.selectedInventories.size();
        for (int i = 0; i < count; i++) {
            int id = newDataInv.selectedInventories.get(i);
            if (!selectedInventories.contains(id)) {
                selectedInventories.add(id);
                sendClientData(container, id, true);
            }
        }

        for (int i = selectedInventories.size() - 1; i >= 0; i--) {
            int id = selectedInventories.get(i);
            if (!newDataInv.selectedInventories.contains(id)) {
                selectedInventories.remove(i);
                sendClientData(container, id, false);
            }
        }
    }

    private void sendClientData(ContainerManager container, int id, boolean select) {
        DataWriter dw = getWriterForClientComponentPacket(container);
        writeData(dw, id, select);
        PacketHandler.sendDataToListeningClients(container, dw);
    }

    @Override
    public void readNetworkComponent(DataReader dr) {
        if (dr.readBoolean()) {
            setOption(dr.readData(DataBitHelper.MENU_INVENTORY_MULTI_SELECTION_TYPE));
        }else{
            int id = dr.readData(DataBitHelper.MENU_INVENTORY_SELECTION);
            if (dr.readBoolean()) {
                selectedInventories.add(id);
            }else{
                selectedInventories.remove((Integer)id);
            }
        }
    }

    private void writeRadioButtonData(DataWriter dw, int option) {
        dw.writeBoolean(true);
        dw.writeData(option, DataBitHelper.MENU_INVENTORY_MULTI_SELECTION_TYPE);
    }

    private void setSelectedInventoryAndSync(int val, boolean select) {
        DataWriter dw = getWriterForServerComponentPacket();
        writeData(dw, val, select);
        PacketHandler.sendDataToServer(dw);
    }

    private void writeData(DataWriter dw, int id, boolean select) {
        dw.writeBoolean(false);
        dw.writeData(id, DataBitHelper.MENU_INVENTORY_SELECTION);
        dw.writeBoolean(select);
    }

    private int getInventoryPosition(int i) {
        return INVENTORY_X + i * INVENTORY_SIZE_W_WITH_MARGIN + offset;
    }

    public List<Integer> getSelectedInventories() {
        return selectedInventories;
    }

    private static final String NBT_SELECTION = "InventorySelection";
    private static final String NBT_SELECTION_ID = "InventoryID";
    private static final String NBT_SHARED = "SharedCommand";
    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, int version) {
        selectedInventories.clear();
        //in earlier version one could only select one inventory
        if (version < 2) {
            selectedInventories.add((int)nbtTagCompound.getShort(NBT_SELECTION));
            setOption(0);
        }else{
            NBTTagList tagList = nbtTagCompound.getTagList(NBT_SELECTION);

            for (int i = 0; i < tagList.tagCount(); i++) {
                NBTTagCompound selectionTag = (NBTTagCompound)tagList.tagAt(i);

                selectedInventories.add((int)selectionTag.getShort(NBT_SELECTION_ID));
            }
            setOption(nbtTagCompound.getByte(NBT_SHARED));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        NBTTagList tagList = new NBTTagList();

        for (int i = 0; i < selectedInventories.size(); i++) {
            NBTTagCompound selectionTag = new NBTTagCompound();

            selectionTag.setShort(NBT_SELECTION_ID, (short)(int)selectedInventories.get(i));
            tagList.appendTag(selectionTag);
        }

        nbtTagCompound.setTag(NBT_SELECTION, tagList);
        nbtTagCompound.setByte(NBT_SHARED, (byte) getOption());
    }

    @Override
    public void addErrors(List<String> errors) {
        if (selectedInventories.isEmpty()) {
            errors.add("No inventory selected");
        }
    }


    public void setSelectedInventories(List<Integer> selectedInventories) {
        this.selectedInventories = selectedInventories;
    }

    public int getOption() {
        return radioButtons.getSelectedOption();
    }

    protected void setOption(int val) {
        radioButtons.setSelectedOption(val);
    }



}
