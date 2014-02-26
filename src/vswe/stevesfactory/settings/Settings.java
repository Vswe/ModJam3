package vswe.stevesfactory.settings;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import vswe.stevesfactory.blocks.TileEntityManager;
import vswe.stevesfactory.network.DataReader;
import vswe.stevesfactory.network.DataWriter;
import vswe.stevesfactory.network.FileHelper;


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

    @SideOnly(Side.CLIENT)
    public static void openMenu(TileEntityManager manager) {
        manager.specialRenderer = new SettingsScreen(manager);
    }

    public static void load() {
        DataReader dr = FileHelper.read(NAME);

        if (dr != null) {
            int version = dr.readByte();

            autoCloseGroup = dr.readBoolean();
             //TODO
            dr.close();
        }else{
            loadDefault();
        }
    }

    private static void loadDefault() {
        autoCloseGroup = true;
    }

    private static void save() {
        DataWriter dw = FileHelper.getWriter(NAME);

        if (dw != null) {
            dw.writeByte(VERSION);

            dw.writeBoolean(autoCloseGroup);
            //TODO
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

    private Settings() {}
}
