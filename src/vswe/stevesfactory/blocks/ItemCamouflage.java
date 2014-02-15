package vswe.stevesfactory.blocks;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import vswe.stevesfactory.StevesFactoryManager;


public class ItemCamouflage  extends ItemBlock {

    public ItemCamouflage(int i) {
        super(i);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public String getUnlocalizedName(ItemStack item) {
        return "tile." + StevesFactoryManager.UNLOCALIZED_START + TileEntityCamouflage.CamouflageType.values()[Blocks.blockCableCamouflage.getId(item.getItemDamage())].getUnlocalized();
    }

}
