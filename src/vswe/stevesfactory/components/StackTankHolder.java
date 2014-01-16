package vswe.stevesfactory.components;



import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

public class StackTankHolder {
    private FluidStack fluidStack;
    private IFluidHandler tank;
    private ForgeDirection side;
    private int sizeLeft;

    public StackTankHolder(FluidStack fluidStack, IFluidHandler tank, ForgeDirection side) {
        this.fluidStack = fluidStack;
        this.tank = tank;
        this.side = side;
        if (fluidStack != null) {
            this.sizeLeft = fluidStack.amount;
        }
    }

    public FluidStack getFluidStack() {
        return fluidStack;
    }


    public IFluidHandler getTank() {
        return tank;
    }

    public ForgeDirection getSide() {
        return side;
    }

    public void reduceAmount(int val) {
        sizeLeft -= val;
        fluidStack.amount -= val;
    }

    public int getSizeLeft() {
        return Math.min(fluidStack.amount, sizeLeft);
    }

    public StackTankHolder getSplitElement(int elementAmount, int id, boolean fair) {
        StackTankHolder element = new StackTankHolder(this.fluidStack, this.tank, this.side);
        int oldAmount = getSizeLeft();
        int amount = oldAmount / elementAmount;
        if (!fair) {
            int amountLeft = oldAmount % elementAmount;
            if (id < amountLeft) {
                amount++;
            }
        }

        element.sizeLeft = amount;
        return element;
    }
}
