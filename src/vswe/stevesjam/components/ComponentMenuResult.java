package vswe.stevesjam.components;


import vswe.stevesjam.blocks.TileEntityJam;
import vswe.stevesjam.interfaces.GuiJam;
import vswe.stevesjam.network.DataBitHelper;
import vswe.stevesjam.network.DataReader;
import vswe.stevesjam.network.DataWriter;

public class ComponentMenuResult extends ComponentMenu {

    public ComponentMenuResult(FlowComponent parent) {
        super(parent);

        for (int i = 0; i < sets.length; i++) {
            ConnectionSet set = sets[i];

            if (parent.getConnectionSet().equals(set)) {
                selectedOption = i;
                break;
            }
        }
    }

    private static final int RADIO_SIZE = 8;
    private static final int RADIO_SRC_X = 30;
    private static final int RADIO_SRC_Y = 52;
    private static final int RADIO_X = 5;
    private static final int RADIO_Y = 5;
    private static final int RADIO_MARGIN = 5;
    private static final int RADIO_TEXT_X = 12;
    private static final int RADIO_TEXT_Y = 2;

    private ConnectionSet[] sets = {ConnectionSet.STANDARD, ConnectionSet.FAIL_CHECK};

    private int selectedOption;

    @Override
    public String getName() {
        return "Result";
    }

    @Override
    public void draw(GuiJam gui, int mX, int mY) {
        for (int i = 0; i < sets.length; i++) {
            ConnectionSet set = sets[i];

            int y = RADIO_Y + i * (RADIO_SIZE + RADIO_MARGIN);

            int srcRadioX = selectedOption == i ? 1 : 0;
            int srcRadioY = GuiJam.inBounds(RADIO_X, y, RADIO_SIZE, RADIO_SIZE, mX, mY) ? 1 : 0;

            gui.drawTexture(RADIO_X, y, RADIO_SRC_X + srcRadioX * RADIO_SIZE, RADIO_SRC_Y + srcRadioY * RADIO_SIZE, RADIO_SIZE, RADIO_SIZE);
            gui.drawString(set.toString(), RADIO_X + RADIO_TEXT_X, y + RADIO_TEXT_Y, 0.7F, 0x404040);
        }
    }

    @Override
    public void drawMouseOver(GuiJam gui, int mX, int mY) {

    }

    @Override
    public void onClick(int mX, int mY, int button) {
        for (int i = 0; i < sets.length; i++) {
            if (GuiJam.inBounds(RADIO_X, RADIO_Y + i * (RADIO_SIZE + RADIO_MARGIN), RADIO_SIZE, RADIO_SIZE, mX, mY)) {
                selectedOption = i;
                getParent().setConnectionSet(sets[i]);
                break;
            }
        }
    }

    @Override
    public void onDrag(int mX, int mY) {

    }

    @Override
    public void onRelease(int mX, int mY) {

    }

    @Override
    public void writeData(DataWriter dw, TileEntityJam jam) {
        dw.writeData(getParent().getConnectionSet().getId(), DataBitHelper.MENU_CONNECTION_TYPE_ID);
    }

    @Override
    public void readData(DataReader dr, TileEntityJam jam) {
        getParent().setConnectionSet(ConnectionSet.getTypeFromId(dr.readData(DataBitHelper.MENU_CONNECTION_TYPE_ID)));
    }

    @Override
    public void readDataOnServer(DataReader dr) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


}
