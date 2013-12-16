package vswe.stevesjam.components;


import vswe.stevesjam.interfaces.ContainerJam;
import vswe.stevesjam.interfaces.GuiJam;
import vswe.stevesjam.network.DataBitHelper;
import vswe.stevesjam.network.DataReader;
import vswe.stevesjam.network.DataWriter;
import vswe.stevesjam.network.PacketHandler;

public class ComponentMenuInterval extends ComponentMenu {
    public ComponentMenuInterval(FlowComponent parent) {
        super(parent);

        textBoxes = new TextBoxNumberList();
        textBoxes.addTextBox(interval = new TextBoxNumber(TEXT_BOX_X, TEXT_BOX_Y, 3, true) {
            @Override
            public void onNumberChanged() {
                DataWriter dw = getWriterForServerComponentPacket();
                dw.writeData(getNumber(), DataBitHelper.MENU_INTERVAL);
                PacketHandler.sendDataToServer(dw);
            }
        });

        interval.setNumber(1);
    }

    private static final int TEXT_BOX_X = 15;
    private static final int TEXT_BOX_Y = 35;
    private static final int TEXT_X = 5;
    private static final int TEXT_Y = 10;
    private static final int TEXT_Y2 = 15;

    private static final int TEXT_SECONDS_X = 60;
    private static final int TEXT_SECOND_Y = 38;

    @Override
    public String getName() {
        return "Interval";
    }

    private TextBoxNumberList textBoxes;
    private TextBoxNumber interval;

    @Override
    public void draw(GuiJam gui, int mX, int mY) {
        gui.drawString("Time between this command",TEXT_X, TEXT_Y, 0.7F, 0x404040);
        gui.drawString("is triggered",TEXT_X, TEXT_Y2, 0.7F, 0x404040);
        gui.drawString("second(s)",TEXT_SECONDS_X, TEXT_SECOND_Y, 0.7F, 0x404040);
        textBoxes.draw(gui, mX, mY);
    }

    @Override
    public void drawMouseOver(GuiJam gui, int mX, int mY) {

    }

    @Override
    public void onClick(int mX, int mY, int button) {
        textBoxes.onClick(mX, mY, button);
    }

    @Override
    public boolean onKeyStroke(GuiJam gui, char c, int k) {
        return textBoxes.onKeyStroke(gui, c, k);
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
        int val = getInterval();
        if (val == 0) {
            val = 1;
        }

        dw.writeData(val, DataBitHelper.MENU_INTERVAL);
    }

    @Override
    public void readData(DataReader dr) {
        setInterval(dr.readData(DataBitHelper.MENU_INTERVAL));
    }

    @Override
    public void copyFrom(ComponentMenu menu) {
       setInterval(((ComponentMenuInterval)menu).getInterval());
    }

    @Override
    public void refreshData(ContainerJam container, ComponentMenu newData) {
        ComponentMenuInterval newDataInterval = (ComponentMenuInterval)newData;

        if (newDataInterval.getInterval() != getInterval()) {
            setInterval(newDataInterval.getInterval());

            DataWriter dw = getWriterForClientComponentPacket(container);
            dw.writeData(getInterval(), DataBitHelper.MENU_INTERVAL);
            PacketHandler.sendDataToListeningClients(container, dw);
        }
}

    @Override
    public void readNetworkComponent(DataReader dr) {
       setInterval(dr.readData(DataBitHelper.MENU_INTERVAL));
    }

    public int getInterval() {
        return interval.getNumber();
    }

    public void setInterval(int val) {
        interval.setNumber(val);
    }
}
