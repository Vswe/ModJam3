package vswe.stevesjam.interfaces;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import vswe.stevesjam.blocks.TileEntityJam;


public class GuiJam extends GuiContainer {
    public GuiJam(TileEntityJam jam, InventoryPlayer player) {
        super(new ContainerJam(jam, player));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {

    }
}
