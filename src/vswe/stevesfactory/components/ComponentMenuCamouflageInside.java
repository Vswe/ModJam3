package vswe.stevesfactory.components;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import vswe.stevesfactory.Localization;
import vswe.stevesfactory.interfaces.ContainerManager;
import vswe.stevesfactory.interfaces.GuiManager;
import vswe.stevesfactory.network.DataBitHelper;
import vswe.stevesfactory.network.DataReader;
import vswe.stevesfactory.network.DataWriter;
import vswe.stevesfactory.network.PacketHandler;


public class ComponentMenuCamouflageInside extends ComponentMenuCamouflageAdvanced {
    public ComponentMenuCamouflageInside(FlowComponent parent) {
        super(parent);

        radioButtons = new RadioButtonList() {
            @Override
            public void updateSelectedOption(int selectedOption) {
                setSelectedOption(selectedOption);

                DataWriter dw = getWriterForServerComponentPacket();
                dw.writeData(radioButtons.getSelectedOption(), DataBitHelper.CAMOUFLAGE_INSIDE);
                PacketHandler.sendDataToServer(dw);
            }
        };

        for (int i = 0; i < InsideSetType.values().length; i++) {
            radioButtons.add(new RadioButton(RADIO_BUTTON_X, RADIO_BUTTON_Y + i * RADIO_BUTTON_SPACING, InsideSetType.values()[i].name));
        }
    }

    private static final int RADIO_BUTTON_X = 5;
    private static final int RADIO_BUTTON_Y = 5;
    private static final int RADIO_BUTTON_SPACING = 12;

    private RadioButtonList radioButtons;

    @Override
    protected String getWarningText() {
        return Localization.INSIDE_WARNING.toString();
    }

    @Override
    public String getName() {
        return Localization.INSIDE_MENU.toString();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY) {
        super.draw(gui, mX, mY);

        radioButtons.draw(gui, mX, mY);
    }

    @Override
    public void onClick(int mX, int mY, int button) {
        radioButtons.onClick(mX, mY, button);
    }



    @Override
    public void onDrag(int mX, int mY, boolean isMenuOpen) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onRelease(int mX, int mY, boolean isMenuOpen) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void writeData(DataWriter dw) {
        dw.writeData(radioButtons.getSelectedOption(), DataBitHelper.CAMOUFLAGE_INSIDE);
    }

    @Override
    public void readData(DataReader dr) {
        radioButtons.setSelectedOption(dr.readData(DataBitHelper.CAMOUFLAGE_INSIDE));
    }

    @Override
    public void copyFrom(ComponentMenu menu) {
        radioButtons.setSelectedOption(((ComponentMenuCamouflageInside)menu).radioButtons.getSelectedOption());
    }

    @Override
    public void refreshData(ContainerManager container, ComponentMenu newData) {
        ComponentMenuCamouflageInside newDataInside = (ComponentMenuCamouflageInside)newData;

        if (radioButtons.getSelectedOption() != newDataInside.radioButtons.getSelectedOption()) {
            radioButtons.setSelectedOption(newDataInside.radioButtons.getSelectedOption());

            DataWriter dw = getWriterForClientComponentPacket(container);
            dw.writeData(radioButtons.getSelectedOption(), DataBitHelper.CAMOUFLAGE_INSIDE);
            PacketHandler.sendDataToListeningClients(container, dw);
        }
    }

    private static final String NBT_SETTING = "Setting";

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, int version, boolean pickup) {
        radioButtons.setSelectedOption(nbtTagCompound.getByte(NBT_SETTING));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup) {
        nbtTagCompound.setByte(NBT_SETTING, (byte)radioButtons.getSelectedOption());
    }

    @Override
    public void readNetworkComponent(DataReader dr) {
        radioButtons.setSelectedOption(dr.readData(DataBitHelper.CAMOUFLAGE_INSIDE));
    }

    public InsideSetType getCurrentType() {
        return InsideSetType.values()[radioButtons.getSelectedOption()];
    }

    public enum InsideSetType{
        ONLY_OUTSIDE(Localization.CAMOUFLAGE_ONLY_OUTSIDE),
        ONLY_INSIDE(Localization.CAMOUFLAGE_ONLY_INSIDE),
        OPPOSITE(Localization.CAMOUFLAGE_OPPOSITE_INSIDE),
        SAME(Localization.CAMOUFLAGE_SAME_INSIDE),
        NOTHING(Localization.CAMOUFLAGE_NO_UPDATE);


        private Localization name;

        private InsideSetType(Localization name) {
            this.name = name;
        }
    }
}
