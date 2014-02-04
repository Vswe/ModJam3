package vswe.stevesfactory.blocks;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import vswe.stevesfactory.StevesFactoryManager;


public class ItemRelay extends ItemBlock {


    public ItemRelay(int i) {
        super(i);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public String getUnlocalizedName(ItemStack item) {
        return "tile." + StevesFactoryManager.UNLOCALIZED_START + (Blocks.blockCableRelay.isAdvanced(item.getItemDamage()) ? Blocks.CABLE_ADVANCED_RELAY_UNLOCALIZED_NAME : Blocks.CABLE_RELAY_UNLOCALIZED_NAME);
    }

}
