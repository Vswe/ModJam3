package vswe.stevesfactory.blocks;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.tileentity.TileEntity;
import vswe.stevesfactory.components.ComponentMenuContainer;
import vswe.stevesfactory.components.IContainerSelection;
import vswe.stevesfactory.components.Variable;
import vswe.stevesfactory.interfaces.Color;
import vswe.stevesfactory.interfaces.GuiManager;

import java.util.EnumSet;
import java.util.List;

public class ConnectionBlock implements IContainerSelection {

    private TileEntity tileEntity;
    private EnumSet<ConnectionBlockType> types;
    private int id;

    public ConnectionBlock(TileEntity tileEntity) {
        this.tileEntity = tileEntity;
        types = EnumSet.noneOf(ConnectionBlockType.class);
    }

    public void addType(ConnectionBlockType type) {
        types.add(type);
    }

    public boolean isOfType(ConnectionBlockType type) {
        return type == null || types.contains(type);
    }

    public boolean isOfAnyType(EnumSet<ConnectionBlockType> types) {
        for (ConnectionBlockType type : types) {
            if (isOfType(type)) {
                return true;
            }
        }

        return false;
    }

    public TileEntity getTileEntity() {
        return tileEntity;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void draw(GuiManager gui, int x, int y) {
        gui.drawBlock(tileEntity, x, y);
    }

    @Override
    public String getDescription(GuiManager gui) {
        String str = gui.getBlockName(tileEntity);

        str += getVariableTag(gui);

        str += "\nX: " + tileEntity.xCoord + " Y: " + tileEntity.yCoord + " Z: " + tileEntity.zCoord;
        str += "\n" + (int)Math.round(Math.sqrt(gui.getManager().getDistanceFrom(tileEntity.xCoord + 0.5, tileEntity.yCoord + 0.5, tileEntity.zCoord + 0.5))) + " block(s) away";

        return str;
    }

    private String getVariableTag(GuiManager gui) {
        int count = 0;
        String result = "";

        if (GuiScreen.isShiftKeyDown()) {
            for (Variable variable : gui.getManager().getVariables()) {
                if (variable.isValid() && ((ComponentMenuContainer)variable.getDeclaration().getMenus().get(2)).getSelectedInventories().contains(id)) {
                    result += "\n" + variable.getDescription(gui);
                    count++;
                }
            }
        }

        return count == 0 ? "" : result;
    }
}
