package vswe.stevesfactory.components;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import vswe.stevesfactory.interfaces.ContainerManager;
import vswe.stevesfactory.interfaces.GuiManager;
import vswe.stevesfactory.network.DataBitHelper;
import vswe.stevesfactory.network.DataReader;
import vswe.stevesfactory.network.DataWriter;
import vswe.stevesfactory.network.PacketHandler;

import java.util.List;

public class ComponentMenuRedstoneSides extends ComponentMenu {

    public ComponentMenuRedstoneSides(FlowComponent parent) {
        super(parent);

        selection = 0x3F; //All selected

        checkBoxList = new CheckBoxList();

        for (int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i++) {
            checkBoxList.addCheckBox(new CheckBoxSide(i));
        }

        radioButtonList = new RadioButtonList() {
            @Override
            public void updateSelectedOption(int selectedOption) {
                setRequireAll(selectedOption == 0);
                sendServerData(true);
            }
        };

        radioButtonList.setSelectedOption(1);

        radioButtonList.add(new RadioButton(RADIO_BUTTON_X_LEFT, RADIO_BUTTON_Y, "Requires all"));
        radioButtonList.add(new RadioButton(RADIO_BUTTON_X_RIGHT, RADIO_BUTTON_Y, "If any"));
    }

    private static final int RADIO_BUTTON_X_LEFT = 5;
    private static final int RADIO_BUTTON_X_RIGHT = 65;
    private static final int RADIO_BUTTON_Y = 23;

    private static final int CHECKBOX_X = 5;
    private static final int CHECKBOX_Y = 35;
    private static final int CHECKBOX_SPACING_X = 70;
    private static final int CHECKBOX_SPACING_Y = 12;

    private static final int MENU_WIDTH = 120;
    private static final int TEXT_MARGIN_X = 5;
    private static final int TEXT_Y = 5;

    private class CheckBoxSide extends CheckBox {
        private int id;
        public CheckBoxSide(int id) {
            super(ForgeDirection.VALID_DIRECTIONS[id].toString().charAt(0) + ForgeDirection.VALID_DIRECTIONS[id].toString().toLowerCase().substring(1), CHECKBOX_X + CHECKBOX_SPACING_X * (id % 2), CHECKBOX_Y + CHECKBOX_SPACING_Y * (id / 2));

            this.id = id;
        }

        @Override
        public void setValue(boolean val) {
            if (val) {
                selection |= 1 << id;
            }else{
                 selection &= ~(1 << id);
            }
        }

        @Override
        public boolean getValue() {
            return (selection & (1 << id)) != 0;
        }

        @Override
        public void onUpdate() {
           sendServerData(false);
        }
    }

    private CheckBoxList checkBoxList;
    private RadioButtonList radioButtonList;
    private int selection;

    @Override
    public String getName() {
        return "Redstone Sides";
    }



    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY) {
        gui.drawSplitString("Select which block sides the redstone should be detected at", TEXT_MARGIN_X, TEXT_Y, MENU_WIDTH - TEXT_MARGIN_X, 0.7F, 0x404040);

        checkBoxList.draw(gui, mX, mY);
        radioButtonList.draw(gui, mX, mY);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawMouseOver(GuiManager gui, int mX, int mY) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onClick(int mX, int mY, int button) {
        checkBoxList.onClick(mX, mY);
        radioButtonList.onClick(mX, mY, button);
    }

    @Override
    public void onDrag(int mX, int mY) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onRelease(int mX, int mY) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void writeData(DataWriter dw) {
        dw.writeBoolean(requireAll());
        dw.writeData(selection, DataBitHelper.MENU_REDSTONE_SETTING);
    }

    @Override
    public void readData(DataReader dr) {
       setRequireAll(dr.readBoolean());
       selection = dr.readData(DataBitHelper.MENU_REDSTONE_SETTING);
    }

    @Override
    public void copyFrom(ComponentMenu menu) {
        ComponentMenuRedstoneSides menuRedstone = (ComponentMenuRedstoneSides)menu;

        selection = menuRedstone.selection;
        setRequireAll(menuRedstone.requireAll());
    }

    @Override
    public void refreshData(ContainerManager container, ComponentMenu newData) {
        ComponentMenuRedstoneSides newDataRedstone = (ComponentMenuRedstoneSides)newData;

       if (requireAll() != newDataRedstone.requireAll()) {
           setRequireAll(newDataRedstone.requireAll());

           sendClientData(container, true);
       }

       if (selection != newDataRedstone.selection) {
            selection = newDataRedstone.selection;

           sendClientData(container, false);
        }
    }

    private void sendClientData(ContainerManager container, boolean syncRequire) {
        DataWriter dw = getWriterForClientComponentPacket(container);
        writeData(dw, syncRequire);
        PacketHandler.sendDataToListeningClients(container, dw);
    }

    private void sendServerData(boolean syncRequire) {
        DataWriter dw = getWriterForServerComponentPacket();
        writeData(dw, syncRequire);
        PacketHandler.sendDataToServer(dw);
    }

    private void writeData(DataWriter dw, boolean syncRequire) {
        dw.writeBoolean(syncRequire);
        if (syncRequire) {
            dw.writeBoolean(requireAll());
        }else{
            dw.writeData(selection, DataBitHelper.MENU_REDSTONE_SETTING);
        }
    }

    private static final String NBT_ACTIVE = "Selection";
    private static final String NBT_ALL = "RequrieAll";

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, int version) {
        //Forgot to save it in earlier versions
        if (version >= 3) {
            selection = nbtTagCompound.getByte(NBT_ACTIVE);
            setRequireAll(nbtTagCompound.getBoolean(NBT_ALL));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        nbtTagCompound.setByte(NBT_ACTIVE, (byte)selection);
        nbtTagCompound.setBoolean(NBT_ALL, requireAll());
    }

    @Override
    public void readNetworkComponent(DataReader dr) {
        if (dr.readBoolean()) {
            setRequireAll(dr.readBoolean());
        }else{
            selection = dr.readData(DataBitHelper.MENU_REDSTONE_SETTING);
        }
    }

    @Override
    public boolean isVisible() {
        return getParent().getConnectionSet() == ConnectionSet.REDSTONE;
    }

    @Override
    public void addErrors(List<String> errors) {
        if (isVisible() && selection == 0) {
            errors.add("No redstone sides selected");
        }
    }



    public boolean requireAll() {
        return radioButtonList.getSelectedOption() == 0;
    }

    private void setRequireAll(boolean val) {
        radioButtonList.setSelectedOption(val ? 0 : 1);
    }

    public boolean isSideRequired(int i) {
        return (selection & (1 << i)) != 0;
    }
}
