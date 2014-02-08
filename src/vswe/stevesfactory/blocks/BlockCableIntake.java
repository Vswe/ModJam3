package vswe.stevesfactory.blocks;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import vswe.stevesfactory.StevesFactoryManager;

//This is indeed not a subclass to the cable, you can't relay signals through this block
public class BlockCableIntake extends BlockCableDirectionAdvanced {
    public BlockCableIntake(int id) {
        super(id);
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityIntake();
    }


    @Override
    protected String getFrontTextureName(boolean isAdvanced) {
        return isAdvanced ? "cable_intake_out_instant" : "cable_intake_out";
    }

    @Override
    protected String getSideTextureName(boolean isAdvanced) {
        return isAdvanced ? "cable_intake_instant" : "cable_intake";
    }

}
