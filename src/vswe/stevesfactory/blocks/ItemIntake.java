package vswe.stevesfactory.blocks;


import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import vswe.stevesfactory.StevesFactoryManager;

public class ItemIntake extends ItemBlock {


    public ItemIntake(int i) {
        super(i);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public String getUnlocalizedName(ItemStack item) {
        return "tile." + StevesFactoryManager.UNLOCALIZED_START + (Blocks.blockCableIntake.isAdvanced(item.getItemDamage()) ? Blocks.CABLE_INSTANT_INTAKE_UNLOCALIZED_NAME : Blocks.CABLE_INTAKE_UNLOCALIZED_NAME);
    }

}
