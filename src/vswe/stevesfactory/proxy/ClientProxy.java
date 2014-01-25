package vswe.stevesfactory.proxy;


import vswe.stevesfactory.blocks.Blocks;

public class ClientProxy extends CommonProxy {
    @Override
    public void init() {
        Blocks.addNames();
    }
}
