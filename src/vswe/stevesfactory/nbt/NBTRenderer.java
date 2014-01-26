package vswe.stevesfactory.nbt;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.*;
import vswe.stevesfactory.interfaces.GuiManager;
import vswe.stevesfactory.interfaces.IInterfaceRenderer;


@SideOnly(Side.CLIENT)
public class NBTRenderer implements IInterfaceRenderer {




    private NBTNode root;
    public NBTRenderer(NBTTagCompound compound) {
        root = NBTNode.generateNodes(compound);
    }

    private static final int POS_X = 5;
    private static final int POS_Y = 5;
    private static final int LINE_HEIGHT = 8;
    private static final int INDENT_WIDTH = 5;
    private static final int VALUE_OFFSET = 70;

    @Override
    public void draw(GuiManager gui, int mX, int mY) {
        drawNode(this.root, gui, mX, mY);
    }

    private void drawNode(NBTNode node, GuiManager gui, int mX, int mY) {
        if (node.getCachedDepth() >= 0) {
            gui.drawString(node.getName(), POS_X + INDENT_WIDTH * node.getCachedDepth(), POS_Y + LINE_HEIGHT * node.getCachedLine(), 0.7F, 0x404040);

            gui.drawString(node.getValue(), POS_X + INDENT_WIDTH * node.getCachedDepth() + VALUE_OFFSET, POS_Y + LINE_HEIGHT * node.getCachedLine(), 0.7F, 0x404040);
        }

        if (node.isOpen() && node.getNodes() != null) {
            for (NBTNode child : node.getNodes()) {
                drawNode(child, gui, mX, mY);
            }
        }
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
