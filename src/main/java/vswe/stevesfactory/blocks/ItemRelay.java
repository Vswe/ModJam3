package vswe.stevesfactory.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import vswe.stevesfactory.StevesFactoryManager;


public class ItemRelay extends ItemBlock {


    public ItemRelay(Block block) {
        super(block);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public String getUnlocalizedName(ItemStack item) {
        return "tile." + StevesFactoryManager.UNLOCALIZED_START + (ModBlocks.blockCableRelay.isAdvanced(item.getItemDamage()) ? ModBlocks.CABLE_ADVANCED_RELAY_UNLOCALIZED_NAME : ModBlocks.CABLE_RELAY_UNLOCALIZED_NAME);
    }

}
