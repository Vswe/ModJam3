package vswe.stevesfactory.components;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import vswe.stevesfactory.interfaces.ContainerManager;
import vswe.stevesfactory.interfaces.GuiManager;
import vswe.stevesfactory.network.DataReader;
import vswe.stevesfactory.network.DataWriter;

/**
 * Created with IntelliJ IDEA.
 * User: Vswe
 * Date: 15/02/14
 * Time: 15:09
 * To change this template use File | Settings | File Templates.
 */
public class ComponentMenuCamouflageShape extends ComponentMenuCamouflageAdvanced {
    public ComponentMenuCamouflageShape(FlowComponent parent) {
        super(parent);
    }

    @Override
    protected String getWarningText() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getName() {
        return "Shape"; //TODO localization
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY) {
        super.draw(gui, mX, mY);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onClick(int mX, int mY, int button) {
        //To change body of implemented methods use File | Settings | File Templates.
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
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void readData(DataReader dr) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void copyFrom(ComponentMenu menu) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void refreshData(ContainerManager container, ComponentMenu newData) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, int version, boolean pickup) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void readNetworkComponent(DataReader dr) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
