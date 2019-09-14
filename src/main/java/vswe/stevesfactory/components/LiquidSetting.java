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
            ret.add(Localization.NO_LIQUID_SELECTED.toString());
        }else{
            ret.add(ComponentMenuLiquid.getDisplayName(fluid));
        }

        ret.add("");
        ret.add(Localization.CHANGE_LIQUID.toString());
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
    public int getDefaultAmount() {
        return 1000;
    }

    private static final String NBT_FLUID_ID = "FluidId";
    private static final String NBT_FLUID_ID_STR = "FluidStr";
    private static final String NBT_FLUID_AMOUNT = "Amount";
    @Override
    public void load(NBTTagCompound settingTag) {
        if (settingTag.hasKey(NBT_FLUID_ID_STR)) {
            fluid = FluidRegistry.getFluid(settingTag.getString(NBT_FLUID_ID_STR));
        } else {
            fluid = FluidRegistry.getFluid(settingTag.getShort(NBT_FLUID_ID));
        }

        amount = settingTag.getInteger(NBT_FLUID_AMOUNT);
    }

    @Override
    public void save(NBTTagCompound settingTag) {
        settingTag.setString(NBT_FLUID_ID_STR, FluidRegistry.getFluidName(fluid.getID()));
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
