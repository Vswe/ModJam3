package vswe.stevesfactory.components;

import net.minecraft.nbt.NBTTagCompound;
import vswe.stevesfactory.interfaces.ContainerManager;
import vswe.stevesfactory.interfaces.GuiManager;
import vswe.stevesfactory.network.DataBitHelper;
import vswe.stevesfactory.network.DataReader;
import vswe.stevesfactory.network.DataWriter;
import vswe.stevesfactory.network.PacketHandler;

import java.util.Comparator;


public class ComponentMenuLoopOrder extends ComponentMenu {
    public ComponentMenuLoopOrder(FlowComponent parent) {
        super(parent);

        radioButtons = new RadioButtonList() {
            @Override
            public void updateSelectedOption(int selectedOption) {
                setSelectedOption(selectedOption);
                sendServerData();
            }
        };

        for (int i = 0; i < LoopOrder.values().length; i++) {
            int x = RADIO_BUTTON_X;
            int y = RADIO_BUTTON_Y + i * RADIO_SPACING_Y;

            radioButtons.add(new RadioButton(x, y, LoopOrder.values()[i].toString()));
        }

        checkBoxes = new CheckBoxList();
        checkBoxes.addCheckBox(new CheckBox("Reversed", CHECK_BOX_X, CHECK_BOX_Y) {
            @Override
            public void setValue(boolean val) {
                reversed = val;
            }

            @Override
            public boolean getValue() {
                return reversed;
            }

            @Override
            public void onUpdate() {
                sendServerData();
            }


        });
    }

    @Override
    public String getName() {
        return "Loop Order";
    }

    private static final int RADIO_BUTTON_X = 5;
    private static final int RADIO_BUTTON_Y = 5;
    private static final int RADIO_SPACING_Y = 12;

    private static final int CHECK_BOX_X = 5;
    private static final int CHECK_BOX_Y = 45;

    private RadioButtonList radioButtons;
    private CheckBoxList checkBoxes;
    private boolean reversed;

    @Override
    public void draw(GuiManager gui, int mX, int mY) {
       radioButtons.draw(gui, mX, mY);
       if (canReverse()) {
           checkBoxes.draw(gui, mX, mY);
       }
    }

    @Override
    public void drawMouseOver(GuiManager gui, int mX, int mY) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onClick(int mX, int mY, int button) {
        radioButtons.onClick(mX, mY, button);
        if (canReverse()) {
            checkBoxes.onClick(mX, mY);
        }
    }

    private void sendServerData() {
        DataWriter dw = getWriterForServerComponentPacket();
        dw.writeBoolean(reversed);
        dw.writeData(radioButtons.getSelectedOption(), DataBitHelper.ORDER_TYPES);
        PacketHandler.sendDataToServer(dw);
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
        dw.writeBoolean(reversed);
        dw.writeData(radioButtons.getSelectedOption(), DataBitHelper.ORDER_TYPES);
    }

    @Override
    public void readData(DataReader dr) {
        reversed = dr.readBoolean();
        radioButtons.setSelectedOption(dr.readData(DataBitHelper.ORDER_TYPES));
    }

    @Override
    public void copyFrom(ComponentMenu menu) {
        reversed = ((ComponentMenuLoopOrder)menu).reversed;
        radioButtons.setSelectedOption(((ComponentMenuLoopOrder)menu).radioButtons.getSelectedOption());
    }

    @Override
    public void refreshData(ContainerManager container, ComponentMenu newData) {
        ComponentMenuLoopOrder newDataOrder = (ComponentMenuLoopOrder)newData;

        if (reversed != newDataOrder.reversed || radioButtons.getSelectedOption() != newDataOrder.radioButtons.getSelectedOption()) {
            reversed = newDataOrder.reversed;
            radioButtons.setSelectedOption(newDataOrder.radioButtons.getSelectedOption());

            DataWriter dw = getWriterForClientComponentPacket(container);
            dw.writeBoolean(reversed);
            dw.writeData(radioButtons.getSelectedOption(), DataBitHelper.ORDER_TYPES);
            PacketHandler.sendDataToListeningClients(container, dw);
        }
    }

    private static final String NBT_REVERSED = "Reversed";
    private static final String NBT_ORDER = "Order";

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, int version) {
        reversed = nbtTagCompound.getBoolean(NBT_REVERSED);
        radioButtons.setSelectedOption(nbtTagCompound.getByte(NBT_ORDER));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        nbtTagCompound.setBoolean(NBT_REVERSED, reversed);
        nbtTagCompound.setByte(NBT_ORDER, (byte)radioButtons.getSelectedOption());
    }

    @Override
    public void readNetworkComponent(DataReader dr) {
        reversed = dr.readBoolean();
        radioButtons.setSelectedOption(dr.readData(DataBitHelper.ORDER_TYPES));
    }

    public Comparator<? super Integer> getComparator() {
        return reversed ? getOrder().reversedComparator : getOrder().comparator;
    }

    private ComponentMenu self = this;

    public boolean isReversed() {
        return reversed;
    }

    public enum LoopOrder {
        NORMAL("Standard", null),
        CABLE("Cable order", new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 < o2 ? -1 : 1;
            }
        }),
        RANDOM("Randomize", null);

        private String name;
        private Comparator<Integer> comparator;
        private Comparator<Integer> reversedComparator;

        private LoopOrder(String name, final Comparator<Integer> comparator) {
            this.name = name;
            this.comparator = comparator;
            if (comparator != null) {
                reversedComparator = new Comparator<Integer>() {
                    @Override
                    public int compare(Integer o1, Integer o2) {
                        return comparator.compare(o2, o1);
                    }
                };
            }
        }


        @Override
        public String toString() {
            return name;
        }


    }

    private boolean canReverse() {
        return getOrder() != LoopOrder.RANDOM;
    }

    public LoopOrder getOrder() {
        return LoopOrder.values()[radioButtons.getSelectedOption()];
    }
}
