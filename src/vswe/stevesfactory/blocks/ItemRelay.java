package vswe.stevesfactory.blocks;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;


public class ItemRelay extends ItemBlock {


    public ItemRelay(int i) {
        super(i);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public String getUnlocalizedName(ItemStack item) {
        return Blocks.blockCableRelay.isAdvanced(item.getItemDamage()) ? Blocks.CABLE_ADVANCED_RELAY_NAME_TAG : Blocks.CABLE_RELAY_NAME_TAG;
    }

}
