package vswe.stevesfactory.configs;

import net.minecraftforge.common.Configuration;
import vswe.stevesfactory.blocks.Blocks;

import java.io.File;

public class ConfigHandler extends Configuration {
    public ConfigHandler(File file) {
        super(file);

        load();

        Blocks.MANAGER_ID = getBlock(Blocks.MANAGER_NAME_TAG, Blocks.MANAGER_DEFAULT_ID).getInt(Blocks.MANAGER_DEFAULT_ID);
        Blocks.CABLE_ID = getBlock(Blocks.CABLE_NAME_TAG, Blocks.CABLE_DEFAULT_ID).getInt(Blocks.CABLE_DEFAULT_ID);
        Blocks.CABLE_RELAY_ID = getBlock(Blocks.CABLE_RELAY_NAME_TAG, Blocks.CABLE_RELAY_DEFAULT_ID).getInt(Blocks.CABLE_RELAY_DEFAULT_ID);
        Blocks.CABLE_OUTPUT_ID = getBlock(Blocks.CABLE_OUTPUT_NAME_TAG, Blocks.CABLE_OUTPUT_DEFAULT_ID).getInt(Blocks.CABLE_OUTPUT_DEFAULT_ID);
        Blocks.CABLE_INPUT_ID = getBlock(Blocks.CABLE_INPUT_NAME_TAG, Blocks.CABLE_INPUT_DEFAULT_ID).getInt(Blocks.CABLE_INPUT_DEFAULT_ID);
        Blocks.CABLE_CREATIVE_ID = getBlock(Blocks.CABLE_CREATIVE_NAME_TAG, Blocks.CABLE_CREATIVE_DEFAULT_ID).getInt(Blocks.CABLE_CREATIVE_DEFAULT_ID);
        Blocks.CABLE_INTAKE_ID = getBlock(Blocks.CABLE_INTAKE_NAME_TAG, Blocks.CABLE_INTAKE_DEFAULT_ID).getInt(Blocks.CABLE_INTAKE_DEFAULT_ID);
        Blocks.CABLE_BUD_ID = getBlock(Blocks.CABLE_BUD_NAME_TAG, Blocks.CABLE_BUD_DEFAULT_ID).getInt(Blocks.CABLE_BUD_DEFAULT_ID);
        Blocks.CABLE_BREAKER_ID = getBlock(Blocks.CABLE_BREAKER_NAME_TAG, Blocks.CABLE_BREAKER_DEFAULT_ID).getInt(Blocks.CABLE_BREAKER_DEFAULT_ID);
        Blocks.CABLE_CLUSTER_ID = getBlock(Blocks.CABLE_CLUSTER_NAME_TAG, Blocks.CABLE_CLUSTER_DEFAULT_ID).getInt(Blocks.CABLE_CLUSTER_DEFAULT_ID);
        Blocks.CABLE_CAMOUFLAGE_ID = getBlock(Blocks.CABLE_CAMOUFLAGE_NAME_TAG, Blocks.CABLE_CAMOUFLAGE_DEFAULT_ID).getInt(Blocks.CABLE_CAMOUFLAGE_DEFAULT_ID);

        save();
    }
}
