package vswe.stevesfactory.components;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.ServerChatEvent;
import vswe.stevesfactory.blocks.TileEntityManager;

public class ChatListener  {

    public ChatListener() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @ForgeSubscribe
    public void invoke(ServerChatEvent event) {
        String msg = event.message;
        System.out.println("Invoke: " + event.message);
        msg = msg.trim();


        if (msg.length() > 0) {
            msg = msg.trim();

            if (msg.startsWith("#SFM ")) {
                msg = msg.substring("#SFM ".length());
                String[] coordinates = msg.split(",");
                if (coordinates.length == 3) {
                    try {
                        int x = Integer.parseInt(coordinates[0]);
                        int y = Integer.parseInt(coordinates[1]);
                        int z = Integer.parseInt(coordinates[2]);
                        World world = MinecraftServer.getServer().getEntityWorld();
                        TileEntity te = world.getBlockTileEntity(x, y, z);
                        if (te instanceof TileEntityManager) {
                            ((TileEntityManager)te).triggerChat();
                        }
                    }catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

        }

    }
}
