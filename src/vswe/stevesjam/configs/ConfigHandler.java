package vswe.stevesjam.configs;

import net.minecraftforge.common.Configuration;
import vswe.stevesjam.blocks.Blocks;

import java.io.File;

public class ConfigHandler extends Configuration {
    public ConfigHandler(File file) {
        super(file);

        load();

        Blocks.MANAGER_ID = getBlock(Blocks.MANAGER_NAME_TAG, Blocks.MANAGER_DEFAULT_ID).getInt(Blocks.MANAGER_DEFAULT_ID);
        Blocks.CABLE_ID = getBlock(Blocks.CABLE_NAME_TAG, Blocks.CABLE_DEFAULT_ID).getInt(Blocks.CABLE_DEFAULT_ID);

        save();
    }
}
