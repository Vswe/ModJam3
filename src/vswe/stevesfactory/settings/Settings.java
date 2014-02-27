package vswe.stevesfactory.settings;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import vswe.stevesfactory.blocks.TileEntityManager;
import vswe.stevesfactory.network.DataReader;
import vswe.stevesfactory.network.DataWriter;
import vswe.stevesfactory.network.FileHelper;
import vswe.stevesfactory.network.PacketHandler;


public final class Settings {

    private static final String NAME = "StevesFactoryManagerInside";
    private static final int VERSION = 0;
    private static boolean autoCloseGroup;
    private static boolean largeOpenHitBox;
    private static boolean largeOpenHitBoxMenu;
    private static boolean quickGroupOpen;
    private static boolean commandTypes;
    private static boolean autoSide;
    private static boolean autoBlacklist;
    private static boolean enlargeInterfaces;

    @SideOnly(Side.CLIENT)
    public static void openMenu(TileEntityManager manager) {
        manager.specialRenderer = new SettingsScreen(manager);
    }

    public static void load() {
        DataReader dr = FileHelper.read(NAME);

        if (dr != null) {
            try {
                int version = dr.readByte();

                autoCloseGroup = dr.readBoolean();
                largeOpenHitBox = dr.readBoolean();
                largeOpenHitBoxMenu = dr.readBoolean();
                quickGroupOpen = dr.readBoolean();
                commandTypes = dr.readBoolean();
                autoSide = dr.readBoolean();
                autoBlacklist = dr.readBoolean();
                enlargeInterfaces = dr.readBoolean();
            }catch (Exception ignored){
                loadDefault();
            }finally {
                dr.close();
            }
        }else{
            loadDefault();
        }
    }

    private static void loadDefault() {
        autoCloseGroup = false;
        largeOpenHitBox = false;
        largeOpenHitBoxMenu = false;
        quickGroupOpen = false;
        commandTypes = false;
        autoSide = false;
        autoBlacklist = false;
        enlargeInterfaces = false;
    }

    private static void save() {
        DataWriter dw = FileHelper.getWriter(NAME);

        if (dw != null) {
            dw.writeByte(VERSION);

            dw.writeBoolean(autoCloseGroup);
            dw.writeBoolean(largeOpenHitBox);
            dw.writeBoolean(largeOpenHitBoxMenu);
            dw.writeBoolean(quickGroupOpen);
            dw.writeBoolean(commandTypes);
            dw.writeBoolean(autoSide);
            dw.writeBoolean(autoBlacklist);
            dw.writeBoolean(enlargeInterfaces);

            FileHelper.write(dw);
        }
    }

    public static boolean isAutoCloseGroup() {
        return autoCloseGroup;
    }

    public static void setAutoCloseGroup(boolean autoCloseGroup) {
        Settings.autoCloseGroup = autoCloseGroup;
        save();
    }

    public static boolean isLargeOpenHitBox() {
        return largeOpenHitBox;
    }

    public static void setLargeOpenHitBox(boolean largeOpenHitBox) {
        Settings.largeOpenHitBox = largeOpenHitBox;
        save();
    }

    public static boolean isLargeOpenHitBoxMenu() {
        return largeOpenHitBoxMenu;
    }

    public static void setLargeOpenHitBoxMenu(boolean largeOpenHitBoxMenu) {
        Settings.largeOpenHitBoxMenu = largeOpenHitBoxMenu;
        save();
    }

    public static boolean isQuickGroupOpen() {
        return quickGroupOpen;
    }

    public static void setQuickGroupOpen(boolean quickGroupOpen) {
        Settings.quickGroupOpen = quickGroupOpen;
        save();
    }

    public static boolean isCommandTypes() {
        return commandTypes;
    }

    public static void setCommandTypes(boolean commandTypes) {
        Settings.commandTypes = commandTypes;
        save();
    }

    public static boolean isAutoSide() {
        return autoSide;
    }

    public static void setAutoSide(boolean autoSide) {
        Settings.autoSide = autoSide;
        save();
    }

    public static boolean isAutoBlacklist() {
        return autoBlacklist;
    }

    public static void setAutoBlacklist(boolean autoBlacklist) {
        Settings.autoBlacklist = autoBlacklist;
        save();
    }

    public static boolean isLimitless(TileEntityManager manager) {
       return ( manager.worldObj.getBlockMetadata(manager.xCoord, manager.yCoord, manager.zCoord) & 1) != 0;
    }

    public static void setLimitless(TileEntityManager manager, boolean limitless) {
        if (manager.worldObj.isRemote) {
            DataWriter dw = PacketHandler.getWriterForServerActionPacket();
            dw.writeBoolean(limitless);
            PacketHandler.sendDataToServer(dw);
        }else{
            int meta = manager.worldObj.getBlockMetadata(manager.xCoord, manager.yCoord, manager.zCoord);
            if (limitless) {
                meta |= 1;
            }else{
                meta &= ~1;
            }
            manager.worldObj.setBlockMetadataWithNotify(manager.xCoord, manager.yCoord, manager.zCoord, meta, 3);
        }
    }

    public static boolean isEnlargeInterfaces() {
        return enlargeInterfaces;
    }

    public static void setEnlargeInterfaces(boolean enlargeInterfaces) {
        Settings.enlargeInterfaces = enlargeInterfaces;
        save();
    }

    private Settings() {}
}
