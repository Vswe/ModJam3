package vswe.stevesfactory.components;


import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import vswe.stevesfactory.Localization;
import vswe.stevesfactory.network.DataBitHelper;
import vswe.stevesfactory.network.DataReader;
import vswe.stevesfactory.network.DataWriter;

import java.util.ArrayList;
import java.util.List;

public class FluidSetting extends Setting {
    private Fluid fluid;
    private int amount;

    public FluidSetting(int id) {
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
            ret.add(Localization.NO_FLUID_SELECTED.toString());
        }else{
            ret.add(ComponentMenuFluid.getDisplayName(fluid));
        }

        ret.add("");
        ret.add(Localization.CHANGE_FLUID.toString());
        if (fluid != null) {
            ret.add(Localization.EDIT_SETTING.toString());
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
       dw.writeString(fluid.getName(), DataBitHelper.MENU_FLUID_ID_LENGTH);
    }

    @Override
    public void readData(DataReader dr) {
        fluid = FluidRegistry.getFluid(dr.readString(DataBitHelper.MENU_FLUID_ID_LENGTH));
    }

    @Override
    public void copyFrom(Setting setting) {
        fluid = ((FluidSetting)setting).fluid;
    }

    @Override
    public int getDefaultAmount() {
        return 1000;
    }

    private static final String NBT_FLUID_NAME = "FluidName";
    private static final String NBT_FLUID_AMOUNT = "Amount";
    @Override
    public void load(NBTTagCompound settingTag) {
        //TODO load properly
        fluid = FluidRegistry.getFluid(settingTag.getString(NBT_FLUID_NAME));
        amount = settingTag.getInteger(NBT_FLUID_AMOUNT);
    }

    @Override
    public void save(NBTTagCompound settingTag) {
        //TODO save properly
        settingTag.setString(NBT_FLUID_NAME, fluid.getName());
        settingTag.setInteger(NBT_FLUID_AMOUNT, amount);
    }

    @Override
    public boolean isContentEqual(Setting otherSetting) {
        return fluid.getName().equals(((FluidSetting)otherSetting).fluid.getName());
    }

    @Override
    public void setContent(Object obj) {
        fluid = (Fluid)obj;
        setDefaultAmount();
    }

    public String getFluidName() {
        return fluid.getName();
    }

    public void setFluidFromName(String name) {
        fluid = FluidRegistry.getFluid(name);
    }

    public Fluid getFluid() {
        return fluid;
    }
}
