package vswe.stevesfactory.components;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;
import vswe.stevesfactory.CollisionHelper;
import vswe.stevesfactory.Localization;
import vswe.stevesfactory.interfaces.ContainerManager;
import vswe.stevesfactory.interfaces.GuiManager;
import vswe.stevesfactory.network.DataBitHelper;
import vswe.stevesfactory.network.DataReader;
import vswe.stevesfactory.network.DataWriter;
import vswe.stevesfactory.network.PacketHandler;

import java.util.List;

public abstract class ComponentMenuTarget extends ComponentMenu {


    public ComponentMenuTarget(FlowComponent parent) {
        super(parent);

        selectedDirectionId = -1;

    }

    private static final int DIRECTION_SIZE_W = 31;
    private static final int DIRECTION_SIZE_H = 12;
    private static final int DIRECTION_SRC_X = 0;
    private static final int DIRECTION_SRC_Y = 70;
    private static final int DIRECTION_X_LEFT = 2;
    private static final int DIRECTION_X_RIGHT = 88;
    private static final int DIRECTION_Y = 5;
    private static final int DIRECTION_MARGIN = 10;
    private static final int DIRECTION_TEXT_X = 2;
    private static final int DIRECTION_TEXT_Y = 3;

    private static final int BUTTON_SIZE_W = 42;
    private static final int BUTTON_SIZE_H = 12;
    private static final int BUTTON_SRC_X = 0;
    private static final int BUTTON_SRC_Y = 106;
    private static final int BUTTON_X = 39;
    private static final int BUTTON_TEXT_Y = 5;



    private Button[] buttons = {new Button(5) {
        @Override
        protected String getLabel() {
            return isActive(selectedDirectionId) ? Localization.DEACTIVATE.toString() : Localization.ACTIVATE.toString();
        }

        @Override
        protected String getMouseOverText() {
            return isActive(selectedDirectionId) ? Localization.DEACTIVATE_LONG.toString() : Localization.ACTIVATE_LONG.toString();
        }

        @Override
        protected void onClicked() {
            writeData(DataTypeHeader.ACTIVATE, isActive(selectedDirectionId) ? 0 : 1);
        }
    },
    getSecondButton()};

    protected abstract Button getSecondButton();




    @Override
    public String getName() {
        return Localization.TARGET_MENU.toString();
    }


    public static ForgeDirection[] directions = ForgeDirection.VALID_DIRECTIONS;

    protected int selectedDirectionId;
    private boolean[] activatedDirections = new boolean[directions.length];
    private boolean[] useRangeForDirections = new boolean[directions.length];



    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY) {
        for (int i = 0; i < directions.length; i++) {
            ForgeDirection direction = directions[i];

            int x = getDirectionX(i);
            int y = getDirectionY(i);

            int srcDirectionX = isActive(i) ? 1 : 0;
            int srcDirectionY = selectedDirectionId != -1 && selectedDirectionId != i ? 2 : CollisionHelper.inBounds(x, y, DIRECTION_SIZE_W, DIRECTION_SIZE_H, mX, mY) ? 1 : 0;


            gui.drawTexture(x, y, DIRECTION_SRC_X + srcDirectionX * DIRECTION_SIZE_W, DIRECTION_SRC_Y + srcDirectionY * DIRECTION_SIZE_H, DIRECTION_SIZE_W, DIRECTION_SIZE_H);

            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            int color =  selectedDirectionId != -1 && selectedDirectionId != i ? 0x70404040 : 0x404040;
            gui.drawString(Localization.getForgeDirectionLocalization(i).toString(), x + DIRECTION_TEXT_X, y + DIRECTION_TEXT_Y, color);
            GL11.glPopMatrix();
        }

        if (selectedDirectionId != -1) {
            for (Button button : buttons) {
                int srcButtonY = CollisionHelper.inBounds(BUTTON_X, button.y, BUTTON_SIZE_W, BUTTON_SIZE_H, mX, mY) ? 1 : 0;

                gui.drawTexture(BUTTON_X, button.y, BUTTON_SRC_X, BUTTON_SRC_Y + srcButtonY * BUTTON_SIZE_H, BUTTON_SIZE_W, BUTTON_SIZE_H);
                gui.drawCenteredString(button.getLabel(), BUTTON_X, button.y + BUTTON_TEXT_Y, 0.5F, BUTTON_SIZE_W, 0x404040);
            }

            if (useAdvancedSetting(selectedDirectionId)) {
                drawAdvancedComponent(gui, mX, mY);
            }
        }
    }



    public boolean isActive(int i) {
        return activatedDirections[i];
    }

    private int getDirectionX(int i) {
        return i % 2 == 0 ? DIRECTION_X_LEFT : DIRECTION_X_RIGHT;
    }


    public boolean useAdvancedSetting(int i) {
        return useRangeForDirections[i];
    }



    private int getDirectionY(int i) {
        return DIRECTION_Y + (DIRECTION_SIZE_H + DIRECTION_MARGIN) * (i / 2);
    }
    @SideOnly(Side.CLIENT)
    @Override
    public void drawMouseOver(GuiManager gui, int mX, int mY) {
        if (selectedDirectionId != -1) {
            for (Button button : buttons) {
                if (CollisionHelper.inBounds(BUTTON_X, button.y, BUTTON_SIZE_W, BUTTON_SIZE_H, mX, mY)) {
                    gui.drawMouseOver(button.getMouseOverText(), mX, mY);
                }
            }
        }
    }

    @Override
    public void onClick(int mX, int mY, int button) {
        for (int i = 0; i < directions.length; i++) {
            if (CollisionHelper.inBounds(getDirectionX(i), getDirectionY(i), DIRECTION_SIZE_W, DIRECTION_SIZE_H, mX, mY)) {
                if (selectedDirectionId == i) {
                    selectedDirectionId = -1;
                }else{
                    selectedDirectionId = i;
                    refreshAdvancedComponent();
                }

                break;
            }
        }

        if (selectedDirectionId != -1) {
            for (Button optionButton : buttons) {
                if (CollisionHelper.inBounds(BUTTON_X, optionButton.y, BUTTON_SIZE_W, BUTTON_SIZE_H, mX, mY)) {
                    optionButton.onClicked();
                    break;
                }
            }

            if (useAdvancedSetting(selectedDirectionId)) {
                onAdvancedClick(mX, mY, button);
            }
        }
    }

    @Override
    public void onDrag(int mX, int mY, boolean isMenuOpen) {

    }

    @Override
    public void onRelease(int mX, int mY, boolean isMenuOpen) {

    }

    protected abstract class Button {
        private int y;

        protected Button(int y) {
            this.y = y;
        }

        protected abstract String getLabel();
        protected abstract String getMouseOverText();
        protected abstract void onClicked();
    }



    @Override
    public void writeData(DataWriter dw) {
        for (int i = 0; i < directions.length; i++) {
            dw.writeBoolean(isActive(i));
            dw.writeBoolean(useAdvancedSetting(i));
            if (useAdvancedSetting(i)) {
                writeAdvancedSetting(dw, i);
            }

        }
    }

    @SideOnly(Side.CLIENT)
    protected abstract void drawAdvancedComponent(GuiManager gui, int mX, int mY);
    protected abstract void refreshAdvancedComponent();
    protected abstract void writeAdvancedSetting(DataWriter dw, int i);
    protected abstract void readAdvancedSetting(DataReader dr, int i);
    protected abstract void copyAdvancedSetting(ComponentMenu menuTarget, int i);
    protected abstract void onAdvancedClick(int mX, int mY, int button);
    protected abstract void loadAdvancedComponent(NBTTagCompound directionTag, int i);
    protected abstract void saveAdvancedComponent(NBTTagCompound directionTag, int i);
    protected abstract void resetAdvancedSetting(int i);
    protected abstract void refreshAdvancedComponentData(ContainerManager container, ComponentMenu newData, int i);
    protected abstract void readAdvancedNetworkComponent(DataReader dr, DataTypeHeader header, int i);

    @Override
    public void readData(DataReader dr) {
        for (int i = 0; i < directions.length; i++) {

            activatedDirections[i] = dr.readBoolean();
            useRangeForDirections[i] = dr.readBoolean();
            if (useAdvancedSetting(i)) {
                readAdvancedSetting(dr, i);
            }else{
                resetAdvancedSetting(i);
            }

        }
    }

    @Override
    public void copyFrom(ComponentMenu menu) {
        ComponentMenuTarget menuTarget = (ComponentMenuTarget)menu;

        for (int i = 0; i < directions.length; i++) {
            activatedDirections[i] = menuTarget.activatedDirections[i];
            useRangeForDirections[i] = menuTarget.useRangeForDirections[i];
            copyAdvancedSetting(menu, i);
        }
    }


    @Override
    public void refreshData(ContainerManager container, ComponentMenu newData) {
        ComponentMenuTarget newDataTarget = (ComponentMenuTarget)newData;

        for (int i = 0; i < directions.length; i++) {
            if (activatedDirections[i] != newDataTarget.activatedDirections[i]) {
                activatedDirections[i] =  newDataTarget.activatedDirections[i];

                writeUpdatedData(container, i, DataTypeHeader.ACTIVATE, activatedDirections[i] ? 1 : 0);
            }

            if (useRangeForDirections[i] != newDataTarget.useRangeForDirections[i]) {
                useRangeForDirections[i] =  newDataTarget.useRangeForDirections[i];

                writeUpdatedData(container, i, DataTypeHeader.USE_ADVANCED_SETTING, useRangeForDirections[i] ? 1 : 0);
            }

            refreshAdvancedComponentData(container, newData, i);
        }
    }




    protected void writeUpdatedData(ContainerManager container, int id, DataTypeHeader header, int data) {
        DataWriter dw = getWriterForClientComponentPacket(container);
        writeData(dw, id, header, data);
        PacketHandler.sendDataToListeningClients(container, dw);
    }

    @Override
    public void readNetworkComponent(DataReader dr) {
       int direction = dr.readData(DataBitHelper.MENU_TARGET_DIRECTION_ID);
       int headerId = dr.readData(DataBitHelper.MENU_TARGET_TYPE_HEADER);
       DataTypeHeader header = getHeaderFromId(headerId);

       switch (header) {
           case ACTIVATE:
               activatedDirections[direction] =  dr.readData(header.bits) != 0;
               break;
           case USE_ADVANCED_SETTING:
               useRangeForDirections[direction] =  dr.readData(header.bits) != 0;
               if (!useAdvancedSetting(direction)) {
                   resetAdvancedSetting(direction);
               }
               break;
           default:
               readAdvancedNetworkComponent(dr, header, direction);
       }
    }




    protected void writeData(DataTypeHeader header, int data) {
        DataWriter dw = getWriterForServerComponentPacket();
        writeData(dw, selectedDirectionId, header, data);
        PacketHandler.sendDataToServer(dw);
    }

    private void writeData(DataWriter dw, int id, DataTypeHeader header, int data) {
        dw.writeData(id, DataBitHelper.MENU_TARGET_DIRECTION_ID);
        dw.writeData(header.id, DataBitHelper.MENU_TARGET_TYPE_HEADER);
        dw.writeData(data, header.bits);
    }

    protected enum DataTypeHeader {
        ACTIVATE(0, DataBitHelper.BOOLEAN),
        USE_ADVANCED_SETTING(1, DataBitHelper.BOOLEAN),
        START_OR_TANK_DATA(2, DataBitHelper.MENU_TARGET_RANGE),
        END(3, DataBitHelper.MENU_TARGET_RANGE);

        private int id;
        private DataBitHelper bits;

        private DataTypeHeader(int header, DataBitHelper bits) {
            this.id = header;
            this.bits = bits;
        }

        public int getId() {
            return id;
        }

        public DataBitHelper getBits() {
            return bits;
        }
    }

    private DataTypeHeader getHeaderFromId(int id) {
        for (DataTypeHeader header : DataTypeHeader.values()) {
            if (id == header.id) {
                return header;
            }
        }
        return  null;
    }

    private static final String NBT_DIRECTIONS = "Directions";
    private static final String NBT_ACTIVE = "Active";
    private static final String NBT_RANGE = "UseRange";


    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, int version, boolean pickup) {
        NBTTagList directionTagList = nbtTagCompound.getTagList(NBT_DIRECTIONS, 10);

        for (int i = 0; i < directionTagList.tagCount(); i++) {
            NBTTagCompound directionTag = directionTagList.getCompoundTagAt(i);
            activatedDirections[i] = directionTag.getBoolean(NBT_ACTIVE);
            useRangeForDirections[i] = directionTag.getBoolean(NBT_RANGE);
            loadAdvancedComponent(directionTag, i);
        }
    }


    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup) {
        NBTTagList directionTagList = new NBTTagList();

        for (int i = 0; i < directions.length; i++)  {
            NBTTagCompound directionTag = new NBTTagCompound();
            directionTag.setBoolean(NBT_ACTIVE, isActive(i));
            directionTag.setBoolean(NBT_RANGE, useAdvancedSetting(i));
            saveAdvancedComponent(directionTag, i);
            directionTagList.appendTag(directionTag);
        }

        nbtTagCompound.setTag(NBT_DIRECTIONS, directionTagList);
    }



    @Override
    public void addErrors(List<String> errors) {
        for (int i = 0; i < directions.length; i++) {
            if (isActive(i)) {
                return;
            }
        }

        errors.add(Localization.NO_DIRECTION_ERROR.toString());
    }

    public void setActive(int side) {
        activatedDirections[side] = true;
    }
}
