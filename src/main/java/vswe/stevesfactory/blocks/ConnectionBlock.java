package vswe.stevesfactory.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.tileentity.TileEntity;
import vswe.stevesfactory.Localization;
import vswe.stevesfactory.components.ComponentMenuContainer;
import vswe.stevesfactory.components.IContainerSelection;
import vswe.stevesfactory.components.Variable;
import vswe.stevesfactory.interfaces.GuiManager;

import java.util.EnumSet;

public class ConnectionBlock implements IContainerSelection {

    private TileEntity tileEntity;
    private EnumSet<ConnectionBlockType> types;
    private int id;
    private int cableDistance;

    public ConnectionBlock(TileEntity tileEntity, int cableDistance) {
        this.tileEntity = tileEntity;
        types = EnumSet.noneOf(ConnectionBlockType.class);
        this.cableDistance = cableDistance;
    }

    public void addType(ConnectionBlockType type) {
        types.add(type);
    }

    public static boolean isOfType(EnumSet<ConnectionBlockType> types, ConnectionBlockType type) {
        return type == null || types.contains(type) || (type == ConnectionBlockType.NODE && (types.contains(ConnectionBlockType.RECEIVER) || types.contains(ConnectionBlockType.EMITTER)));
    }

    public boolean isOfType(ConnectionBlockType type) {
       return isOfType(this.types, type);
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

        str += "\n" + Localization.X + ": " + tileEntity.xCoord + " " + Localization.Y + ": " + tileEntity.yCoord + " " + Localization.Z + ": " + tileEntity.zCoord;
        int distance = getDistance(gui.getManager());
        str += "\n" + distance + " " + (distance > 1 ? Localization.BLOCKS_AWAY : Localization.BLOCK_AWAY);
        str += "\n" + cableDistance + " " + (cableDistance > 1 ? Localization.CABLES_AWAY : Localization.CABLE_AWAY);

        return str;
    }


    public int getDistance(TileEntityManager manager) {
        return (int)Math.round(Math.sqrt(manager.getDistanceFrom(tileEntity.xCoord + 0.5, tileEntity.yCoord + 0.5, tileEntity.zCoord + 0.5)));
    }

    public int getCableDistance() {
        return cableDistance;
    }

    @Override
    public String getName(GuiManager gui) {
        return gui.getBlockName(tileEntity);
    }

    @Override
    public boolean isVariable() {
        return false;
    }


    @SideOnly(Side.CLIENT)
    private String getVariableTag(GuiManager gui) {
        int count = 0;
        String result = "";

        if (GuiScreen.isShiftKeyDown()) {
            for (Variable variable : gui.getManager().getVariables()) {
                if (isPartOfVariable(variable)) {
                    result += "\n" + variable.getDescription(gui);
                    count++;
                }
            }
        }

        return count == 0 ? "" : result;
    }

    @SideOnly(Side.CLIENT)
    public boolean isPartOfVariable(Variable variable) {
        return variable.isValid() && ((ComponentMenuContainer)variable.getDeclaration().getMenus().get(2)).getSelectedInventories().contains(id);
    }
}
