package vswe.stevesjam.blocks;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import vswe.stevesjam.components.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TileEntityJam extends TileEntity {

    private List<FlowComponent> items;
    private Connection currentlyConnecting;

    public TileEntityJam() {
        items = new ArrayList<>();

    }

    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);

        items.add(new FlowComponent(this, 30, 30, ComponentType.OUTPUT));
        items.add(new FlowComponent(this, 200, 30, ComponentType.INPUT));
        items.add(new FlowComponent(this, 200, 80, ComponentType.TRIGGER));
        items.add(new FlowComponent(this, 330, 30, ComponentType.INPUT));
        items.add(new FlowComponent(this, 400, 30, ComponentType.OUTPUT));
        items.add(new FlowComponent(this, 100, 30, ComponentType.INPUT));
        items.add(new FlowComponent(this, 100, 80, ComponentType.INPUT));
        items.add(new FlowComponent(this, 400, 80, ComponentType.TRIGGER));
    }

    public List<FlowComponent> getFlowItems() {
        return items;
    }

    public List<TileEntity> getConnectedInventories() {
        List<TileEntity> inventories = new ArrayList<>();

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    TileEntity tileEntity = worldObj.getBlockTileEntity(x + xCoord, y + yCoord, z + zCoord);
                    if (tileEntity != null && tileEntity instanceof IInventory) {
                        inventories.add(tileEntity);
                    }
                }
            }
        }

        return  inventories;
    }

    public Connection getCurrentlyConnecting() {
        return currentlyConnecting;
    }

    public void setCurrentlyConnecting(Connection currentlyConnecting) {
        this.currentlyConnecting = currentlyConnecting;
    }

    private int timer = 0;

    @Override
    public void updateEntity() {
        if (timer >= 20) {
            timer = 0;
            if (!worldObj.isRemote) {
                doStuff();
            }
        }else{
            timer++;
        }
    }



    public void doStuff() {
        for (FlowComponent item : items) {
            if (item.getType() == ComponentType.TRIGGER) {
                new CommandExecutor(this).executeCommand(item);
            }
        }
    }






}
