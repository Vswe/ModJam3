package vswe.stevesfactory.blocks;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import vswe.stevesfactory.components.ComponentMenuPulse;
import vswe.stevesfactory.components.ComponentMenuRedstoneOutput;
import vswe.stevesfactory.components.ComponentMenuRedstoneSidesEmitter;
import vswe.stevesfactory.network.DataReader;
import vswe.stevesfactory.network.DataWriter;
import vswe.stevesfactory.network.IPacketBlock;
import vswe.stevesfactory.network.PacketHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TileEntityOutput extends TileEntity implements IPacketBlock, IRedstoneNode {

    private int[] strengths;
    private boolean[] strong;

    public TileEntityOutput() {
        strengths = new int[ForgeDirection.VALID_DIRECTIONS.length];
        strong = new boolean[ForgeDirection.VALID_DIRECTIONS.length];

        pulseTimers = new List[ForgeDirection.VALID_DIRECTIONS.length];
        for (int i = 0; i < pulseTimers.length; i++) {
            pulseTimers[i] = new ArrayList<PulseTimer>();
        }
    }

    public boolean hasStrongSignalAtSide(int side) {
        return strong[side];
    }


    public boolean hasStrongSignalAtOppositeSide(int side) {
        return strong[getOpposite(side)];
    }

    public int getStrengthFromSide(int side) {
        return strengths[side];
    }

    public int getStrengthFromOppositeSide(int side) {
        return getStrengthFromSide(getOpposite(side));
    }

    private int getOpposite(int side) {
        return ForgeDirection.getOrientation(side).getOpposite().ordinal();
    }


    public void updateState(ComponentMenuRedstoneSidesEmitter sides, ComponentMenuRedstoneOutput output, ComponentMenuPulse pulse) {
        boolean updateClient = false;
        for (int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i++) {
            if (sides.isSideRequired(i)) {
                int oldStrength = strengths[i];
                boolean oldStrong = strong[i];

                updateSideState(i, output);
                strong[i] = sides.useStrongSignal();


                if (((strengths[i] > 0) != (oldStrength > 0)) || (oldStrong != strong[i])) {
                    updateClient = true;
                }
                boolean updateBlocks = oldStrength != strengths[i] || oldStrong != strong[i];





                if (updateBlocks) {
                    addBlockScheduledForUpdate(i);

                    if (pulse.shouldEmitPulse()) {
                        PulseTimer timer = new PulseTimer(oldStrength, oldStrong, pulse.getPulseTime() + 1); //add one to counter the first tick (which is the same tick as we add it)
                        List<PulseTimer> timers = pulseTimers[i];

                        if (timers.size() < 200) { //to block a huge amount of pulses at the same time
                            switch (pulse.getSelectedPulseOverride()) {
                                case EXTEND_OLD:
                                    if (timers.size() > 0) {
                                        if (timers.size() > 1) {
                                            PulseTimer temp = timers.get(0);
                                            timers.clear();
                                            timers.add(temp);
                                        }

                                        PulseTimer oldTimer = timers.get(0);
                                        oldTimer.ticks = Math.max(oldTimer.ticks, timer.ticks);
                                    }else{
                                        timers.add(timer);
                                    }
                                    break;
                                case KEEP_ALL:
                                    timers.add(timer);
                                    break;
                                case KEEP_NEW:
                                    timers.clear();
                                    timers.add(timer);
                                    break;
                                case KEEP_OLD:
                                    if (timers.isEmpty()) {
                                        timers.add(timer);
                                    }
                            }
                        }
                    }
                }


            }
        }

        if (updateClient) {
            PacketHandler.sendBlockPacket(this, null, 0);
        }
    }

    private void addBlockScheduledForUpdate(int side) {
        hasUpdatedThisTick = true;
        ForgeDirection direction = ForgeDirection.getOrientation(side);
        int x = xCoord + direction.offsetX;
        int y = yCoord + direction.offsetY;
        int z = zCoord + direction.offsetZ;

        WorldCoordinate coordinate = new WorldCoordinate(x, y, z);
        if (!scheduledToUpdate.contains(coordinate)) {
            scheduledToUpdate.add(coordinate);
        }
    }


    private void updateSideState(int side, ComponentMenuRedstoneOutput output) {
        int strength = strengths[side];
        int selectedStrength = output.getSelectedStrength();

        switch (output.getSelectedSetting()) {
            case FIXED:
                strength = selectedStrength;
                break;
            case TOGGLE:
                strength = strength > 0 ? 0 : 15;
                break;
            case MAX:
                strength = Math.max(strength, selectedStrength);
                break;
            case MIN:
                strength = Math.min(strength, selectedStrength);
                break;
            case INCREASE:
                strength = Math.min(15, strength + selectedStrength);
                break;
            case DECREASE:
                strength = Math.max(0, strength - selectedStrength);
                break;
            case FORWARD:
                strength = (strength + selectedStrength) % 16;
                break;
            case BACKWARD:
                strength -= selectedStrength;
                if (strength < 0) strength += 16;
                break;
        }


        strengths[side] = strength;
    }


    private void notifyUpdate(int x, int y, int z, boolean spread) {
        if (worldObj.getBlockId(x, y, z) != Blocks.blockCable.blockID && (x != xCoord || y != yCoord || z != zCoord)) {
            worldObj.notifyBlockOfNeighborChange(x, y, z, Blocks.blockCableOutput.blockID);
            if (spread) {
                notifyUpdate(x - 1, y, z, false);
                notifyUpdate(x + 1, y, z, false);
                notifyUpdate(x,         y - 1,      z,      false);
                notifyUpdate(x,         y + 1,      z,      false);
                notifyUpdate(x,         y,          z - 1,  false);
                notifyUpdate(x,         y,          z + 1,  false);
            }
        }
    }



    private static final String NBT_SIDES = "Sides";
    private static final String NBT_STRENGTH = "Strength";
    private static final String NBT_STRONG = "Strong";
    private static final String NBT_TICK = "Tick";
    private static final String NBT_PULSES = "Pulses";

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);

        int version = nbtTagCompound.getByte(Blocks.NBT_PROTOCOL_VERSION);


        NBTTagList sidesTag = nbtTagCompound.getTagList(NBT_SIDES);
        for (int i = 0; i < sidesTag.tagCount(); i++) {

            NBTTagCompound sideTag = (NBTTagCompound)sidesTag.tagAt(i);

            strengths[i] = sideTag.getByte(NBT_STRENGTH);
            strong[i] = sideTag.getBoolean(NBT_STRONG);

            List<PulseTimer> timers = pulseTimers[i];
            timers.clear();
            NBTTagList pulsesTag = sideTag.getTagList(NBT_PULSES);
            for (int j = 0; j < pulsesTag.tagCount(); j++) {
                NBTTagCompound pulseTag = (NBTTagCompound)pulsesTag.tagAt(j);

                timers.add(new PulseTimer(pulseTag.getByte(NBT_STRENGTH), pulseTag.getBoolean(NBT_STRONG), pulseTag.getShort(NBT_TICK)));
            }
        }
    }



    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);

        nbtTagCompound.setByte(Blocks.NBT_PROTOCOL_VERSION, Blocks.NBT_CURRENT_PROTOCOL_VERSION);

        NBTTagList sidesTag = new NBTTagList();
        for (int i = 0; i < strengths.length; i++) {
            NBTTagCompound sideTag = new NBTTagCompound();

            sideTag.setByte(NBT_STRENGTH, (byte)strengths[i]);
            sideTag.setBoolean(NBT_STRONG, strong[i]);

            NBTTagList pulsesTag = new NBTTagList();
            List<PulseTimer> timers = pulseTimers[i];

            for (PulseTimer timer : timers) {
                NBTTagCompound pulseTag = new NBTTagCompound();
                pulseTag.setByte(NBT_STRENGTH, (byte)timer.strength);
                pulseTag.setBoolean(NBT_STRONG, timer.strong);
                pulseTag.setShort(NBT_TICK, (short)timer.ticks);
                pulsesTag.appendTag(pulseTag);
            }
            sideTag.setTag(NBT_PULSES, pulsesTag);

            sidesTag.appendTag(sideTag);
        }


        nbtTagCompound.setTag(NBT_SIDES, sidesTag);
    }


    @Override
    public void writeData(DataWriter dw, EntityPlayer player, boolean onServer, int id) {
        if (onServer) {
            for (int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i++) {
                boolean isOn = strengths[i] > 0;
                dw.writeBoolean(isOn);
                if (isOn) {
                    dw.writeBoolean(strong[i]);
                }
            }
        }else{
            //nothing to write, empty packet
        }
    }

    @Override
    public void readData(DataReader dr, EntityPlayer player, boolean onServer, int id) {
        if (onServer) {
            //respond by sending the data to the client that required it
            PacketHandler.sendBlockPacket(this, player, 0);
        }else{
            for (int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i++) {
                boolean isOn = dr.readBoolean();
                strengths[i] = isOn ? 15 : 0;
                if (isOn) {
                    strong[i] = dr.readBoolean();
                }
            }
            worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
        }
    }


    @Override
    public int infoBitLength(boolean onServer) {
        return 0; //won't use the id
    }

    private List<PulseTimer>[] pulseTimers;
    private boolean hasUpdatedThisTick;
    private List<WorldCoordinate> scheduledToUpdate = new ArrayList<WorldCoordinate>();

    @Override
    public void updateEntity() {
       if (worldObj.isRemote) {
           keepClientDataUpdated();
       }else{
           updatePulses();

           if (hasUpdatedThisTick) {
               hasUpdatedThisTick = false;
               for (WorldCoordinate coordinate : scheduledToUpdate) {
                   notifyUpdate(coordinate.getX(), coordinate.getY(), coordinate.getZ(), true);
               }
               scheduledToUpdate.clear();
           }
       }
    }

    private void updatePulses() {
        boolean updateClient = false;

        for (int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i++) {
            Iterator<PulseTimer> iterator = pulseTimers[i].iterator();

            while (iterator.hasNext()) {
                PulseTimer timer = iterator.next();
                timer.ticks--;
                if (timer.ticks == 0) {
                    if (strengths[i] != timer.strength || strong[i] == timer.strong) {
                        strengths[i] = timer.strength;
                        strong[i] = timer.strong;
                        addBlockScheduledForUpdate(i);
                        updateClient = true;
                    }
                    iterator.remove();
                }
            }
        }

        if (updateClient) {
            PacketHandler.sendBlockPacket(this, null, 0);
        }
    }

    private static final int UPDATE_BUFFER_DISTANCE = 5;
    private boolean hasUpdatedData;

    @SideOnly(Side.CLIENT)
    private void keepClientDataUpdated() {
        double distance = Minecraft.getMinecraft().thePlayer.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);

        if (distance > Math.pow(PacketHandler.BLOCK_UPDATE_RANGE, 2)) {
            hasUpdatedData = false;
        }else if(!hasUpdatedData && distance < Math.pow(PacketHandler.BLOCK_UPDATE_RANGE - UPDATE_BUFFER_DISTANCE, 2)) {
            hasUpdatedData = true;
            PacketHandler.sendBlockPacket(this, Minecraft.getMinecraft().thePlayer, 0);
        }
    }

    @Override
    public int[] getPower() {
        return strengths;
    }

    private class PulseTimer {
        private int strength;
        private boolean strong;
        private int ticks;

        private PulseTimer(int strength, boolean strong, int ticks) {
            this.strength = strength;
            this.strong = strong;
            this.ticks = ticks;
        }
    }
}
