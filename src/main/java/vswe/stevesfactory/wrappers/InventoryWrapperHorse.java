package vswe.stevesfactory.wrappers;

import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.HorseType;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.ReflectionHelper;


public class InventoryWrapperHorse extends InventoryWrapper {
    private EntityHorse horse;
    public InventoryWrapperHorse(EntityHorse horse) {
        super((IInventory)ReflectionHelper.getPrivateValue(EntityHorse.class, horse, 15));
        this.horse = horse;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        //empty stacks)
        if (!horse.isTame() || itemstack == null) {
            return super.isItemValidForSlot(i, itemstack);

        //saddle
        }else if(i == 0) {
            return itemstack.getItem() == Items.saddle;

        //armor
        }else if(i == 1 && horse.getType().isHorse()) {
            return HorseType.func_188577_b(itemstack.getItem());

        //chest
        }else {
            return i > 1 && horse.isChested();
        }

    }
}
