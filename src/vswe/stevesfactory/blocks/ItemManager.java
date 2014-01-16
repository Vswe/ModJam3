package vswe.stevesfactory.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;


public class ItemManager extends ItemBlock {
    public ItemManager(int id) {
        super(id);
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        boolean flag = super.onItemUse(itemStack, player, world, x, y, z, side, hitX, hitY, hitZ);
        System.out.println(flag);
        if (flag) {
            TileEntity te = world.getBlockTileEntity(x, y, z);

            if (te != null && te instanceof TileEntityManager) {
                TileEntityManager manager = (TileEntityManager)te;
                if (itemStack.hasTagCompound()) {
                    manager.readFromNBT(itemStack.getTagCompound());
                    System.out.println("read");
                }else{
                    System.out.println("no data");
                }
                return true;
            }else{
                world.setBlockToAir(x, y, z);
                System.out.println("clear");
            }
        }
        System.out.println("failed to read");
        return false;
    }
}
