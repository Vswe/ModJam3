package vswe.stevesfactory.blocks;


import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import vswe.stevesfactory.Localization;
import vswe.stevesfactory.StevesFactoryManager;

import java.util.List;

public class ItemCluster extends ItemBlock {


    public ItemCluster(Block block) {
        super(block);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    public static final String NBT_CABLE = "Cable";
    public static final String NBT_TYPES = "Types";

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound != null && compound.hasKey(NBT_CABLE)) {
            NBTTagCompound cable = compound.getCompoundTag(NBT_CABLE);
            if (cable.hasKey(NBT_TYPES)) {
                return super.onItemUse(stack, player, world, pos, hand, side, hitX, hitY, hitZ);
            }
        }

        return EnumActionResult.PASS;
    }

    @Override
    public void addInformation(ItemStack item, EntityPlayer player, List list, boolean extraInfo) {
        NBTTagCompound compound = item.getTagCompound();
        if (compound != null && compound.hasKey(NBT_CABLE)) {
            NBTTagCompound cable = compound.getCompoundTag(NBT_CABLE);
            byte[] types = cable.getByteArray(ItemCluster.NBT_TYPES);
            for (byte type : types) {
                list.add(ClusterRegistry.getRegistryList().get(type).getItemStack().getDisplayName());
            }
        }else{
            list.add(Localization.EMPTY_CLUSTER.toString());
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack item) {
        return "tile." + StevesFactoryManager.UNLOCALIZED_START + (ModBlocks.blockCableCluster.isAdvanced(item.getItemDamage()) ? ModBlocks.CABLE_ADVANCED_CLUSTER_UNLOCALIZED_NAME : ModBlocks.CABLE_CLUSTER_UNLOCALIZED_NAME);
    }
}
