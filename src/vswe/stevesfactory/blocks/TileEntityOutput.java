package vswe.stevesfactory.blocks;


import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import vswe.stevesfactory.components.ComponentMenuRedstoneOutput;
import vswe.stevesfactory.components.ComponentMenuRedstoneSidesEmitter;

public class TileEntityOutput extends TileEntity {

    private int[] strengths;

    public TileEntityOutput() {
        strengths = new int[ForgeDirection.VALID_DIRECTIONS.length];

        strengths[0] = 5;
        strengths[2] = 3;
        strengths[3] = 2;
        strengths[4] = 15;
    }


    public int getStrengthFromSide(int side) {
        return strengths[side];
    }

    public int getStrengthFromOppositeSide(int side) {
        return getStrengthFromSide(ForgeDirection.getOrientation(side).getOpposite().ordinal());
    }

    public void updateState(ComponentMenuRedstoneSidesEmitter sides, ComponentMenuRedstoneOutput output) {
        for (int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i++) {
            if (sides.isSideRequired(i)) {
                updateSideState(i, output);
            }
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
                strength = (strength + selectedStrength) % 15;
                break;
            case BACKWARD:
                strength -= selectedStrength;
                if (strength < 0) strength += 15;
                break;
        }

        if (strengths[side] != strength) {
            strengths[side] = strength;
            ForgeDirection direction = ForgeDirection.getOrientation(side);
            int x = xCoord + direction.offsetX;
            int y = yCoord + direction.offsetY;
            int z = zCoord + direction.offsetZ;

            if (worldObj.getBlockId(x, y, z) != Blocks.blockCable.blockID) {
                //TODO prevent infinite trigger when connecting an emitter directly to a receiver
                worldObj.notifyBlockOfNeighborChange(x, y, z, Blocks.blockCableOutput.blockID);
            }
        }
    }
}
