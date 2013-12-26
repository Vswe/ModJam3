package vswe.stevesfactory.components;



import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

public class StackTankHolder {
    private FluidStack fluidStack;
    private IFluidHandler tank;
    private ForgeDirection side;

    public StackTankHolder(FluidStack fluidStack, IFluidHandler tank, ForgeDirection side) {
        this.fluidStack = fluidStack;
        this.tank = tank;
        this.side = side;
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
}
