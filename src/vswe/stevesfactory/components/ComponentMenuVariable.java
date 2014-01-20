package vswe.stevesfactory.components;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import org.lwjgl.opengl.GL11;
import vswe.stevesfactory.CollisionHelper;
import vswe.stevesfactory.interfaces.ContainerManager;
import vswe.stevesfactory.interfaces.GuiManager;
import vswe.stevesfactory.network.DataBitHelper;
import vswe.stevesfactory.network.DataReader;
import vswe.stevesfactory.network.DataWriter;
import vswe.stevesfactory.network.PacketHandler;

public class ComponentMenuVariable extends ComponentMenu {
    public ComponentMenuVariable(FlowComponent parent) {
        super(parent);

        int declarationCount = 0;
        int modificationCount = 0;

        radioButtons = new RadioButtonList() {
            @Override
            public void updateSelectedOption(int selectedOption) {
                setSelectedOption(selectedOption);
                DataWriter dw = getWriterForServerComponentPacket();
                dw.writeBoolean(false); //mode
                dw.writeData(selectedOption, DataBitHelper.CONTAINER_MODE);
                PacketHandler.sendDataToServer(dw);
            }

            @Override
            public int getSelectedOption() {
                int id = super.getSelectedOption();
                VariableMode mode = VariableMode.values()[id];
                if (mode.declaration != isDeclaration()) {
                    setSelectedOption(id = getDefaultId());
                }

                return id;
            }

        };

        for (int i = 0; i < VariableMode.values().length; i++) {
            final VariableMode mode = VariableMode.values()[i];
            int id = mode.declaration ? declarationCount++ : modificationCount++;
            radioButtons.add(new RadioButton(RADIO_BUTTON_X, RADIO_BUTTON_Y + id * RADIO_BUTTON_SPACING, mode.toString()) {
                @Override
                public boolean isVisible() {
                    return mode.declaration == isDeclaration();
                }
            });
        }

        radioButtons.setSelectedOption(getDefaultId());
    }

    @Override
    public String getName() {
        return "Variable";
    }


    private static final int RADIO_BUTTON_X = 5;
    private static final int RADIO_BUTTON_Y = 28;
    private static final int RADIO_BUTTON_SPACING = 12;

    private static final int VARIABLE_SRC_X = 32;
    private static final int VARIABLE_SRC_Y = 130;
    private static final int VARIABLE_SIZE = 14;
    private static final int VARIABLE_X = 20;
    private static final int VARIABLE_Y = 5;

    private static final int ARROW_SRC_X = 18;
    private static final int ARROW_SRC_Y = 20;
    private static final int ARROW_WIDTH = 6;
    private static final int ARROW_HEIGHT = 10;
    private static final int ARROW_X_LEFT = 5;
    private static final int ARROW_X_RIGHT = 43;
    private static final int ARROW_Y = 7;

    private RadioButtonList radioButtons;
    private int selectedVariable = 0;


    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY) {
        radioButtons.draw(gui, mX, mY);

        VariableColor.values()[selectedVariable].applyColor();
        gui.drawTexture(VARIABLE_X, VARIABLE_Y, VARIABLE_SRC_X, VARIABLE_SRC_Y, VARIABLE_SIZE, VARIABLE_SIZE);
        GL11.glColor4f(1F, 1F, 1F, 1F);

        for (int i = 0; i < 2; i++) {
            int x = i == 0 ? ARROW_X_LEFT : ARROW_X_RIGHT;
            int y = ARROW_Y;

            int srcXArrow = i;
            int srcYArrow = CollisionHelper.inBounds(x, y, ARROW_WIDTH, ARROW_HEIGHT, mX, mY) ? 1 : 0;

            gui.drawTexture(x, y, ARROW_SRC_X + srcXArrow * ARROW_WIDTH, ARROW_SRC_Y + srcYArrow * ARROW_HEIGHT, ARROW_WIDTH, ARROW_HEIGHT);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawMouseOver(GuiManager gui, int mX, int mY) {
        if (CollisionHelper.inBounds(VARIABLE_X, VARIABLE_Y, VARIABLE_SIZE, VARIABLE_SIZE, mX, mY)) {
            VariableColor color = VariableColor.values()[selectedVariable];
            gui.drawMouseOver(color.getTextColor().toString() + color.toString() + " Variable", mX, mY);
        }
    }

    @Override
    public void onClick(int mX, int mY, int button) {
        radioButtons.onClick(mX, mY, button);

        for (int i = -1; i <= 1; i+=2) {
            int x = i == 1 ? ARROW_X_RIGHT : ARROW_X_LEFT;
            int y = ARROW_Y;


            if (CollisionHelper.inBounds(x, y, ARROW_WIDTH, ARROW_HEIGHT, mX, mY)) {
                selectedVariable += i;
                if (selectedVariable < 0) {
                    selectedVariable = VariableColor.values().length - 1;
                }else if(selectedVariable == VariableColor.values().length) {
                    selectedVariable = 0;
                }

                DataWriter dw = getWriterForServerComponentPacket();
                dw.writeBoolean(true); //var
                dw.writeData(selectedVariable, DataBitHelper.VARIABLE_TYPE);
                PacketHandler.sendDataToServer(dw);
                break;
            }
        }
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
        dw.writeData(selectedVariable, DataBitHelper.VARIABLE_TYPE);
        dw.writeData(radioButtons.getSelectedOption(), DataBitHelper.CONTAINER_MODE);
    }

    @Override
    public void readData(DataReader dr) {
        selectedVariable = dr.readData(DataBitHelper.VARIABLE_TYPE);
        radioButtons.setSelectedOption(dr.readData(DataBitHelper.CONTAINER_MODE));
    }

    @Override
    public void copyFrom(ComponentMenu menu) {
        selectedVariable = ((ComponentMenuVariable) menu).selectedVariable;
       radioButtons.setSelectedOption(((ComponentMenuVariable) menu).radioButtons.getSelectedOption());

    }

    @Override
    public void refreshData(ContainerManager container, ComponentMenu newData) {
        ComponentMenuVariable newDataMode = (ComponentMenuVariable)newData;

        if (selectedVariable != newDataMode.selectedVariable) {
            selectedVariable = newDataMode.selectedVariable;

            DataWriter dw = getWriterForClientComponentPacket(container);
            dw.writeBoolean(true); //var
            dw.writeData(selectedVariable, DataBitHelper.VARIABLE_TYPE);
            PacketHandler.sendDataToListeningClients(container, dw);
        }

        if (radioButtons.getRawSelectedOption() != newDataMode.radioButtons.getRawSelectedOption()) {
            radioButtons.setSelectedOption(newDataMode.radioButtons.getRawSelectedOption());

            DataWriter dw = getWriterForClientComponentPacket(container);
            dw.writeBoolean(false); //mode
            dw.writeData(radioButtons.getRawSelectedOption(), DataBitHelper.CONTAINER_MODE);
            PacketHandler.sendDataToListeningClients(container, dw);
        }

    }

    private static final String NBT_VARIABLE = "Variable";
    private static final String NBT_MODE = "Mode";

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, int version) {
        selectedVariable = nbtTagCompound.getByte(NBT_VARIABLE);
       radioButtons.setSelectedOption(nbtTagCompound.getByte(NBT_MODE));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        nbtTagCompound.setByte(NBT_VARIABLE, (byte)selectedVariable);
        nbtTagCompound.setByte(NBT_MODE, (byte)radioButtons.getSelectedOption());
    }

    @Override
    public void readNetworkComponent(DataReader dr) {
        if (dr.readBoolean()) {
            selectedVariable = dr.readData(DataBitHelper.VARIABLE_TYPE);
        }else{
            radioButtons.setSelectedOption(dr.readData(DataBitHelper.CONTAINER_MODE));
        }
    }

    private enum VariableMode {
        GLOBAL(true),
        LOCAL(true),
        ADD(false),
        REMOVE(false),
        SET(false);

        private boolean declaration;

        private VariableMode(boolean declaration) {
            this.declaration = declaration;
        }


        @Override
        public String toString() {
            return super.toString().charAt(0) + super.toString().substring(1).toLowerCase();
        }
    }

    public boolean isDeclaration() {
        return getParent().getConnectionSet() == ConnectionSet.EMPTY;
    }

    private int getDefaultId() {
        return isDeclaration() ? 1 : 2;
    }

    public VariableMode getVariableMode() {
        return VariableMode.values()[radioButtons.getSelectedOption()];
    }
}
