package vswe.stevesfactory.blocks;


import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ClusterRecipe implements IRecipe {

    private ItemStack output;

    @Override
    public boolean matches(InventoryCrafting inventorycrafting, World world) {
        output = null;


        ItemStack cluster = null;
        for (int i = 0; i < inventorycrafting.getSizeInventory(); i++) {
            ItemStack item = inventorycrafting.getStackInSlot(i);

            if (item != null && Block.getBlockFromItem(item.getItem()) == ModBlocks.blockCableCluster) {
                if (cluster != null) {
                    return false; //multiple clusters
                }else{
                    cluster = item;
                }
            }
        }

        if (cluster != null) {
            boolean foundClusterComponent = false;
            List<Integer> types = new ArrayList<Integer>();
            NBTTagCompound compound = cluster.getTagCompound();
            if (compound != null && compound.hasKey(ItemCluster.NBT_CABLE)) {
                byte[] typeIds = compound.getCompoundTag(ItemCluster.NBT_CABLE).getByteArray(ItemCluster.NBT_TYPES);
                for (byte typeId : typeIds) {
                    types.add((int)typeId);
                }
            }

            for (int i = 0; i < inventorycrafting.getSizeInventory(); i++) {
                ItemStack item = inventorycrafting.getStackInSlot(i);

                if (item != null && Block.getBlockFromItem(item.getItem()) != ModBlocks.blockCableCluster) {
                    boolean validItem = false;
                    for (int j = 0; j < ClusterRegistry.getRegistryList().size(); j++) {
                        if (item.isItemEqual(ClusterRegistry.getRegistryList().get(j).getItemStack())) {
                            if (ClusterRegistry.getRegistryList().get(j).isChainPresentIn(types)) {
                                return false; //duplicate item
                            }
                            types.add(j);
                            validItem = true;
                            foundClusterComponent = true;
                            break;
                        }
                    }
                    if (!validItem) {
                        return false; //invalid item
                    }
                }
            }

            byte[] typeIds = new byte[types.size()];
            for (int i = 0; i < types.size(); i++) {
                typeIds[i] = (byte)(int)types.get(i);
            }

            if (!foundClusterComponent) {
                return false; //nothing added
            }

            output = new ItemStack(ModBlocks.blockCableCluster, 1, cluster.getItemDamage());
            NBTTagCompound newCompound = new NBTTagCompound();
            output.setTagCompound(newCompound);
            NBTTagCompound subCompound = new NBTTagCompound();
            newCompound.setTag(ItemCluster.NBT_CABLE, subCompound);
            subCompound.setByteArray(ItemCluster.NBT_TYPES, typeIds);

            return true;
        }

        return false;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventorycrafting) {
        return output.copy();
    }

    @Override
    public int getRecipeSize() {
        return 10;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return output;
    }
}
