package vswe.stevesfactory.components;



import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

public class StackTankHolder {
    private FluidStack fluidStack;
    private IFluidHandler tank;

    public StackTankHolder(FluidStack fluidStack, IFluidHandler tank) {
        this.fluidStack = fluidStack;
        this.tank = tank;
    }

    public FluidStack getFluidStack() {
        return fluidStack;
    }


    public IFluidHandler getTank() {
        return tank;
    }


}
