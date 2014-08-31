package vswe.stevesfactory.components;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import vswe.stevesfactory.network.DataReader;
import vswe.stevesfactory.network.DataWriter;

import java.util.List;


public abstract class Setting {
    private int id;

    private boolean isLimitedByAmount;

    public Setting(int id) {
        this.id = id;
        clear();
    }

    @SideOnly(Side.CLIENT)
    public abstract List<String> getMouseOver();

    public void clear() {
        isLimitedByAmount = false;
    }

    public int getId() {
        return id;
    }

    public boolean isLimitedByAmount() {
        return isLimitedByAmount;
    }

    public void setLimitedByAmount(boolean limitedByAmount) {
        isLimitedByAmount = limitedByAmount;
    }

    public void setDefaultAmount() {
        setAmount(getDefaultAmount());
    }

    public abstract int getAmount();
    public abstract void setAmount(int val);
    public abstract boolean isValid();

    public abstract void writeData(DataWriter dw);
    public abstract void readData(DataReader dr);
    public abstract void copyFrom(Setting setting);

    public abstract int getDefaultAmount();

    public abstract void load(NBTTagCompound settingTag);
    public abstract void save(NBTTagCompound settingTag);

    public abstract boolean isContentEqual(Setting otherSetting);

    public abstract void setContent(Object obj);

    public boolean isAmountSpecific() {
        return true;
    }

    public void delete() {
        clear();
    }
}