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

        numberTextBoxes.addTextBox(amountTextBoxBuckets = new TextBoxNumber(10 ,50, 3, true) {
            @Override
            public boolean isVisible() {
                return selectedSetting.isLimitedByAmount();
            }

            @Override
            public void onNumberChanged() {
                sendAmountData();
            }
        });

        numberTextBoxes.addTextBox(amountTextBoxMilli = new TextBoxNumber(60 ,50, 3, true) {
            @Override
            public boolean isVisible() {
                return selectedSetting.isLimitedByAmount();
            }

            @Override
            public void onNumberChanged() {
                sendAmountData();
            }
        });
    }



    private void sendAmountData() {
        selectedSetting.setAmount(amountTextBoxBuckets.getNumber() * 1000 + amountTextBoxMilli.getNumber());
        writeServerData(DataTypeHeader.AMOUNT);
    }
    private TextBoxNumber amountTextBoxBuckets;
    private TextBoxNumber amountTextBoxMilli;

    @SideOnly(Side.CLIENT)
    @Override
    protected void drawInfoMenuContent(GuiManager gui, int mX, int mY) {
        if (selectedSetting.isLimitedByAmount()) {
            gui.drawCenteredString("Buckets", amountTextBoxBuckets.getX(), amountTextBoxBuckets.getY() - 7, 0.7F, amountTextBoxBuckets.getWidth(), 0x404040);
            gui.drawCenteredString("Milli Buckets", amountTextBoxMilli.getX(), amountTextBoxMilli.getY() - 7, 0.55F, amountTextBoxMilli.getWidth(), 0x404040);
        }
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
    protected void drawResultObjectMouseOver(GuiManager gui, Object obj, int x, int y) {
        gui.drawMouseOver(getDisplayName((Fluid) obj), x, y);
    }

    @Override
    protected void drawSettingObjectMouseOver(GuiManager gui, Setting setting, int x, int y) {
        drawResultObjectMouseOver(gui, ((LiquidSetting)setting).getFluid(), x, y);
    }

    @Override
    protected void updateTextBoxes() {
        int amount = selectedSetting.getAmount();
        amountTextBoxBuckets.setNumber(amount / 1000);
        amountTextBoxMilli.setNumber(amount % 1000);
    }

    @Override
    protected DataBitHelper getAmountBitLength() {
        return DataBitHelper.MENU_LIQUID_AMOUNT;
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
        }
    }



    @Override
    public String getName() {
        return "Liquids";
    }

    protected LiquidSetting getSelectedSetting() {
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

                if (!getDisplayName(fluid).toLowerCase().contains(searchString)) {
                    itemIterator.remove();
                }
            }
        }

        updateScrolling();
    }

    public static String getDisplayName(Fluid fluid) {
        //different mods store the name in different ways apparently
        String name = fluid.getLocalizedName();
        if (name.indexOf(".") != -1) {
            name = FluidRegistry.getFluidName(fluid.getID());
        }

        return name;
    }
}
