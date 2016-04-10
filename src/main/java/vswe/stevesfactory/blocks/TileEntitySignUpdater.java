package vswe.stevesfactory.blocks;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import vswe.stevesfactory.components.ComponentMenuSignText;

import java.util.EnumSet;


public class TileEntitySignUpdater extends TileEntityClusterElement {
    @Override
    protected EnumSet<ClusterMethodRegistration> getRegistrations() {
        return EnumSet.of(ClusterMethodRegistration.ON_BLOCK_PLACED_BY);
    }

    public void updateSign(ComponentMenuSignText menu) {
        EnumFacing direction = EnumFacing.getFront(getBlockMetadata() % EnumFacing.values().length);
        TileEntity te = worldObj.getTileEntity(new BlockPos(getPos().getX() + direction.getFrontOffsetX(), getPos().getY() + direction.getFrontOffsetY(), getPos().getZ() + direction.getFrontOffsetZ()));
        if (te != null && te instanceof  TileEntitySign) {
            TileEntitySign sign = (TileEntitySign)te;
            //if (sign.func_142009_b() == null) {
                sign.setPlayer(null);
                boolean updated = false;
                for (int i = 0; i < 4; i++) {
                    if (menu.shouldUpdate(i)) {
                        IChatComponent oldText = sign.signText[i];
                        ChatComponentText newText = new ChatComponentText(menu.getText(i));
                        if (!newText.equals(oldText)) {
                            sign.signText[i] = newText;
                            updated = true;
                        }
                    }
                }
                if (updated) {
                    sign.markDirty();
                    worldObj.markBlockForUpdate(new BlockPos(sign.getPos().getX(), sign.getPos().getY(), sign.getPos().getZ()));
                }

            //}
        }
    }
}
