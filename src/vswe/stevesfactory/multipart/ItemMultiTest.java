package vswe.stevesfactory.multipart;

import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Vector3;
import codechicken.multipart.JItemMultiPart;
import codechicken.multipart.TMultiPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import vswe.stevesfactory.blocks.Blocks;

/**
 * Created with IntelliJ IDEA.
 * User: Vswe
 * Date: 26/01/14
 * Time: 01:55
 * To change this template use File | Settings | File Templates.
 */
public class ItemMultiTest extends JItemMultiPart {
    public ItemMultiTest(int id) {
        super(id);

        setCreativeTab(Blocks.creativeTab);
    }

    /**
     * Create a new part based on the placement information parameters.
     */
    @Override
    public TMultiPart newPart(ItemStack item, EntityPlayer player, World world, BlockCoord pos, int side, Vector3 vhit) {
        return new BlockMultiTest();
    }
}
