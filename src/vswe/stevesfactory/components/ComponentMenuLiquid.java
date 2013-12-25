package vswe.stevesfactory.components;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import vswe.stevesfactory.interfaces.ContainerManager;
import vswe.stevesfactory.interfaces.GuiManager;
import vswe.stevesfactory.network.DataBitHelper;
import vswe.stevesfactory.network.DataReader;
import vswe.stevesfactory.network.DataWriter;

import java.util.ArrayList;
import java.util.Iterator;

public class ComponentMenuLiquid extends ComponentMenuStuff {
    public ComponentMenuLiquid(FlowComponent parent) {
        super(parent, LiquidSetting.class);
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected void drawInfoMenuContent(GuiManager gui, int mX, int mY) {

    }

    @SideOnly(Side.CLIENT)
    @Override
    protected void drawResultObject(GuiManager gui, Object obj, int x, int y) {
        gui.drawFluid((Fluid)obj, x, y);
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected void drawSettingObject(GuiManager gui, Setting setting, int x, int y) {
        drawResultObject(gui,((LiquidSetting)setting).getFluid(), x, y);
    }

    @Override
    protected void readSpecificHeaderData(DataReader dr, DataTypeHeader header, Setting setting) {
        LiquidSetting liquidSetting = (LiquidSetting)setting;

        switch (header) {
            case SET_ITEM:
                liquidSetting.setLiquidFromId(dr.readData(DataBitHelper.MENU_FLUID_ID));

                if (isEditing()) {
                    updateTextBoxes();
                }


        }
    }

    @Override
    protected void writeSpecificHeaderData(DataWriter dw, DataTypeHeader header, Setting setting) {
        LiquidSetting liquidSetting = (LiquidSetting)setting;
        switch (header) {
            case SET_ITEM:
                dw.writeData(liquidSetting.getLiquidId(), DataBitHelper.MENU_FLUID_ID);
                break;
        }
    }

    @Override
    public String getName() {
        return "Liquids";
    }

    private LiquidSetting getSelectedSetting() {
        return (LiquidSetting)selectedSetting;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected void updateSearch(boolean showAll) {
        result = new ArrayList(FluidRegistry.getRegisteredFluids().values());

        Iterator<Fluid> itemIterator = result.iterator();
        String searchString = text.toLowerCase();

        if (!showAll) {
            while (itemIterator.hasNext()) {

                Fluid fluid = itemIterator.next();

                if (!fluid.getLocalizedName().contains(searchString)) {
                    itemIterator.remove();
                }
            }
        }

        updateScrolling();
    }
}
