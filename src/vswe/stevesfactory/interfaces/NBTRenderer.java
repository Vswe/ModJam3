package vswe.stevesfactory.interfaces;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.*;


@SideOnly(Side.CLIENT)
public class NBTRenderer implements IInterfaceRenderer {

    private static final int END_TAG = 0;
    private static final int BYTE_TAG = 1;
    private static final int SHORT_TAG = 2;
    private static final int INT_TAG = 3;
    private static final int LONG_TAG = 4;
    private static final int FLOAT_TAG = 5;
    private static final int DOUBLE_TAG = 6;
    private static final int BYTE_ARRAY_TAG = 7;
    private static final int STRING_TAG = 8;
    private static final int LIST_TAG = 9;
    private static final int COMPOUND_TAG = 10;
    private static final int INT_ARRAY_TAG = 11;


    private NBTTagCompound compound;
    public NBTRenderer(NBTTagCompound compound) {
        this.compound = compound;
    }

    private static final int POS_X = 5;
    private static final int POS_Y = 5;
    private static final int LINE_HEIGHT = 8;
    private static final int INDENT_WIDTH = 20;

    @Override
    public void draw(GuiManager gui, int mX, int mY) {
        drawCompound(this.compound, gui, mX, mY, 0, 0);
    }

    private int drawCompound(NBTTagCompound compound, GuiManager gui, int mX, int mY, int depth, int line) {
        for (Object obj : compound.getTags()) {
            NBTBase tag = (NBTBase)obj;

            if (tag.getId() == END_TAG) break;

            line = drawElement(gui, tag, mX, mY, depth, line);
        }

        return line;
    }

    private int drawList(NBTTagList list, GuiManager gui, int mX, int mY, int depth, int line) {
        for (int i = 0; i < list.tagCount(); i++) {
            line = drawElement(gui, list.tagAt(i), mX, mY, depth, line);
        }

        return line;
    }

    private int drawArray(NBTTagByteArray array, GuiManager gui, int mX, int mY, int depth, int line) {
        for (byte b : array.byteArray) {
            drawValue(gui, String.valueOf(b), depth, line);
            line++;
        }


        return line;
    }

    private int drawElement(GuiManager gui, NBTBase tag, int mX, int mY, int depth, int line) {
        drawName(gui, tag.getName(), depth, line);

        switch (tag.getId()) {
            case COMPOUND_TAG:
                line = drawCompound((NBTTagCompound)tag, gui, mX, mY, depth + 1, line + 1);
                break;
            case LIST_TAG:
                line = drawList((NBTTagList)tag, gui, mX, mY, depth + 1, line + 1);
                break;
            case BYTE_ARRAY_TAG:
                line = drawArray((NBTTagByteArray)tag, gui, mX, mY, depth, line + 1);
                break;
            default:
                drawValue(gui, tag.toString() + " [type = " + tag.getId() + "]", depth, line);
                line++;
        }


        return line;
    }

    private void drawName(GuiManager gui, String name, int depth, int line) {
        gui.drawString(name, POS_X + INDENT_WIDTH * depth, POS_Y + LINE_HEIGHT * line, 0.7F, 0x404040);
    }

    private void drawValue(GuiManager gui, String val, int depth, int line) {
        gui.drawString(val, POS_X + INDENT_WIDTH * depth + 100, POS_Y + LINE_HEIGHT * line, 0.7F, 0x404040);
    }

    @Override
    public void drawMouseOver(GuiManager gui, int mX, int mY) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onClick(GuiManager gui, int mX, int mY) {
        gui.getManager().specialRenderer = null;
    }

    @Override
    public void onDrag(GuiManager gui, int mX, int mY) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onRelease(GuiManager gui, int mX, int mY) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onKeyTyped(GuiManager gui, char c, int k) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
