package vswe.stevesfactory.components;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import vswe.stevesfactory.interfaces.GuiManager;
import vswe.stevesfactory.network.DataBitHelper;
import vswe.stevesfactory.network.DataReader;
import vswe.stevesfactory.network.DataWriter;

import java.util.ArrayList;
import java.util.List;

public class LiquidSetting extends Setting {
    private Fluid fluid;
    private int amount;

    public LiquidSetting(int id) {
        super(id);
    }

    @Override
    public void clear() {
        super.clear();

        fluid = null;
        setDefaultAmount();
    }

    @Override
    public List<String> getMouseOver() {
        List<String> ret = new ArrayList<String>();

        if (fluid == null) {
            ret.add("[No liquid selected]");
        }else{
            ret.add(ComponentMenuLiquid.getDisplayName(fluid));
        }

        ret.add("");
        ret.add("Left click to change liquid");
        if (fluid != null) {
            ret.add("Right click to edit settings");
        }

        return ret;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public void setAmount(int val) {
        amount = val;
    }

    @Override
    public boolean isValid() {
        return fluid != null;
    }


    @Override
    public void writeData(DataWriter dw) {
       dw.writeData(fluid.getID(), DataBitHelper.MENU_FLUID_ID);
    }

    @Override
    public void readData(DataReader dr) {
        fluid = FluidRegistry.getFluid(dr.readData(DataBitHelper.MENU_FLUID_ID));
    }

    @Override
    public void copyFrom(Setting setting) {
        fluid = ((LiquidSetting)setting).fluid;
    }

    @Override
    public void setDefaultAmount() {
        setAmount(1000);
    }

    private static final String NBT_FLUID_ID = "FluidId";
    private static final String NBT_FLUID_AMOUNT = "Amount";
    @Override
    public void load(NBTTagCompound settingTag) {
        fluid = FluidRegistry.getFluid(settingTag.getShort(NBT_FLUID_ID));
        amount = settingTag.getInteger(NBT_FLUID_AMOUNT);
    }

    @Override
    public void save(NBTTagCompound settingTag) {
        settingTag.setShort(NBT_FLUID_ID, (short)fluid.getID());
        settingTag.setInteger(NBT_FLUID_AMOUNT, amount);
    }

    @Override
    public boolean isContentEqual(Setting otherSetting) {
        return fluid.getID() == ((LiquidSetting)otherSetting).fluid.getID();
    }

    @Override
    public void setContent(Object obj) {
        fluid = (Fluid)obj;
        setDefaultAmount();
    }

    public int getLiquidId() {
        return fluid.getID();
    }

    public void setLiquidFromId(int id) {
        fluid = FluidRegistry.getFluid(id);
    }

    public Fluid getFluid() {
        return fluid;
    }
}
