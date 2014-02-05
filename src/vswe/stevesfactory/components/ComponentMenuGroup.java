package vswe.stevesfactory.components;

import net.minecraft.nbt.NBTTagCompound;
import vswe.stevesfactory.CollisionHelper;
import vswe.stevesfactory.Localization;
import vswe.stevesfactory.interfaces.ContainerManager;
import vswe.stevesfactory.interfaces.GuiManager;
import vswe.stevesfactory.network.DataReader;
import vswe.stevesfactory.network.DataWriter;


public class ComponentMenuGroup extends ComponentMenu {
    public ComponentMenuGroup(FlowComponent parent) {
        super(parent);
    }

    @Override
    public String getName() {
        return Localization.GROUP_MENU.toString();
    }

    @Override
    public void draw(GuiManager gui, int mX, int mY) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void drawMouseOver(GuiManager gui, int mX, int mY) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onClick(int mX, int mY, int button) {
        if (CollisionHelper.inBounds(0, 0, 50, 50, mX, mY)) {
            getParent().getManager().setSelectedComponent(getParent());
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
