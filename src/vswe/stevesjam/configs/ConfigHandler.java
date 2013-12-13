package vswe.stevesjam.configs;

import net.minecraftforge.common.Configuration;
import vswe.stevesjam.blocks.Blocks;

import java.io.File;

public class ConfigHandler extends Configuration {
    public ConfigHandler(File file) {
        super(file);

        load();

        Blocks.JAM_ID = getBlock(Blocks.JAM_NAME_TAG, Blocks.JAM_DEFAULT_ID).getInt(Blocks.JAM_DEFAULT_ID);

        save();
    }
}
