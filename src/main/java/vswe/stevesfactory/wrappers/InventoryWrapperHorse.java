package vswe.stevesfactory.wrappers;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Created with IntelliJ IDEA.
 * User: Vswe
 * Date: 15/01/14
 * Time: 16:48
 * To change this template use File | Settings | File Templates.
 */
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
        }else if(i == 1 && horse.getHorseType() == 0) {
            return horse.func_110259_cr() && EntityHorse.func_146085_a(itemstack.getItem());

        //chest
        }else {
            return i > 1 && horse.isChested();
        }

    }
}
