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

public class ComponentMenuRedstoneOutput extends ComponentMenu {
    public ComponentMenuRedstoneOutput(FlowComponent parent) {
        super(parent);

        textBoxes = new TextBoxNumberList();

        textBoxes.addTextBox(textBox = new TextBoxNumber(TEXT_BOX_X, TEXT_BOX_Y, 2, true) {
            @Override
            public int getMaxNumber() {
                return 15;
            }

            @Override
            public void onNumberChanged() {
                DataWriter dw = getWriterForServerComponentPacket();
                dw.writeBoolean(true); //header
                dw.writeData(getNumber(), DataBitHelper.MENU_REDSTONE_ANALOG);
                PacketHandler.sendDataToServer(dw);
            }
        });
        textBox.setNumber(15);

        radioButtons = new RadioButtonList() {
            @Override
            public void updateSelectedOption(int selectedOption) {
                setSelectedOption(selectedOption);
                DataWriter dw = getWriterForServerComponentPacket();
                dw.writeBoolean(false); //header
                dw.writeData(selectedOption, DataBitHelper.MENU_REDSTONE_OUTPUT_TYPE);
                PacketHandler.sendDataToServer(dw);
            }
        };

        for (int i = 0; i < Settings.values().length; i++) {
            int ix = i % 2;
            int iy = i / 2;

            int x = RADIO_BUTTON_X + ix * RADIO_SPACING_X;
            int y = RADIO_BUTTON_Y + iy * RADIO_SPACING_Y;


            radioButtons.add(new RadioButton(x, y, Settings.values()[i].getName()));
        }
    }

    private TextBoxNumberList textBoxes;
    private TextBoxNumber textBox;
    private RadioButtonList radioButtons;

    public int getSelectedStrength() {
        return textBox.getNumber();
    }

    public static enum Settings {
        FIXED(Localization.FIXED),
        TOGGLE(Localization.TOGGLE),
        MAX(Localization.MAX),
        MIN(Localization.MIN),
        INCREASE(Localization.INCREASE),
        DECREASE(Localization.DECREASE),
        FORWARD(Localization.FORWARD),
        BACKWARD(Localization.BACKWARD);

        private Localization name;

        private Settings(Localization name) {
            this.name = name;
        }

        public Localization getName() {
            return name;
        }

        @Override
        public String toString() {
            return name.toString();
        }


    }

    private static final int RADIO_BUTTON_X = 5;
    private static final int RADIO_BUTTON_Y = 22;
    private static final int RADIO_SPACING_X = 68;
    private static final int RADIO_SPACING_Y = 12;

    private static final int TEXT_BOX_X = 80;
    private static final int TEXT_BOX_Y = 5;

    private static final int TEXT_X = 5;
    private static final int TEXT_Y = 9;

    @Override
    public String getName() {
        return Localization.REDSTONE_OUTPUT_MENU.toString();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY) {
        if (useStrengthSetting()) {
            gui.drawString(Localization.REDSTONE_STRENGTH.toString(), TEXT_X, TEXT_Y, 0.7F, 0x404040);
            textBoxes.draw(gui, mX, mY);
        }else{
            gui.drawString(Localization.DIGITAL_TOGGLE.toString(), TEXT_X, TEXT_Y, 0.7F, 0x404040);
        }
        radioButtons.draw(gui, mX, mY);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawMouseOver(GuiManager gui, int mX, int mY) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean onKeyStroke(GuiManager gui, char c, int k) {
        if (useStrengthSetting()) {
            return textBoxes.onKeyStroke(gui, c, k);
        }else{
            return super.onKeyStroke(gui, c, k);
        }
    }

    @Override
    public void onClick(int mX, int mY, int button) {
        if (useStrengthSetting()) {
            textBoxes.onClick(mX, mY, button);
        }
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
        dw.writeData(textBox.getNumber(), DataBitHelper.MENU_REDSTONE_ANALOG);
        dw.writeData(radioButtons.getSelectedOption(), DataBitHelper.MENU_REDSTONE_OUTPUT_TYPE);
    }

    @Override
    public void readData(DataReader dr) {
        textBox.setNumber(dr.readData(DataBitHelper.MENU_REDSTONE_ANALOG));
        radioButtons.setSelectedOption(dr.readData(DataBitHelper.MENU_REDSTONE_OUTPUT_TYPE));
    }

    @Override
    public void copyFrom(ComponentMenu menu) {
        ComponentMenuRedstoneOutput menuOutput = (ComponentMenuRedstoneOutput)menu;

        textBox.setNumber(menuOutput.textBox.getNumber());
        radioButtons.setSelectedOption(menuOutput.radioButtons.getSelectedOption());
    }

    @Override
    public void refreshData(ContainerManager container, ComponentMenu newData) {
        ComponentMenuRedstoneOutput newDataOutput = (ComponentMenuRedstoneOutput)newData;

        if (textBox.getNumber() != newDataOutput.textBox.getNumber()) {
            textBox.setNumber(newDataOutput.textBox.getNumber());

            DataWriter dw = getWriterForClientComponentPacket(container);
            dw.writeBoolean(true); //header
            dw.writeData(textBox.getNumber(), DataBitHelper.MENU_REDSTONE_ANALOG);
            PacketHandler.sendDataToListeningClients(container, dw);
        }

        if (radioButtons.getSelectedOption() != newDataOutput.radioButtons.getSelectedOption())  {
            radioButtons.setSelectedOption(newDataOutput.radioButtons.getSelectedOption());

            DataWriter dw = getWriterForClientComponentPacket(container);
            dw.writeBoolean(false); //header
            dw.writeData(radioButtons.getSelectedOption(), DataBitHelper.MENU_REDSTONE_OUTPUT_TYPE);
            PacketHandler.sendDataToListeningClients(container, dw);
        }
    }

    private static final String NBT_NUMBER = "Strength";
    private static final String NBT_TYPE = "OutputType";

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, int version, boolean pickup) {
        textBox.setNumber(nbtTagCompound.getByte(NBT_NUMBER));
        radioButtons.setSelectedOption(nbtTagCompound.getByte(NBT_TYPE));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup) {
        nbtTagCompound.setByte(NBT_NUMBER, (byte)textBox.getNumber());
        nbtTagCompound.setByte(NBT_TYPE, (byte)radioButtons.getSelectedOption());
    }

    @Override
    public void readNetworkComponent(DataReader dr) {
        boolean isNumber = dr.readBoolean();
        if (isNumber) {
            textBox.setNumber(dr.readData(DataBitHelper.MENU_REDSTONE_ANALOG));
        }else{
            int type = dr.readData(DataBitHelper.MENU_REDSTONE_OUTPUT_TYPE);
            radioButtons.setSelectedOption(type);
        }
    }

    private boolean useStrengthSetting() {
        return getSelectedSetting() != Settings.TOGGLE;
    }

    public Settings getSelectedSetting() {
        return Settings.values()[radioButtons.getSelectedOption()];
    }
}
