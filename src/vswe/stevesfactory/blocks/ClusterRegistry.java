package vswe.stevesfactory.blocks;


import net.minecraft.block.BlockContainer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClusterRegistry {

    private static HashMap<Class<? extends TileEntityClusterElement>, ClusterRegistry> registry = new HashMap<Class<? extends TileEntityClusterElement>, ClusterRegistry>();
    private static List<ClusterRegistry> registryList = new ArrayList<ClusterRegistry>();

    private Class<? extends TileEntityClusterElement> clazz;
    private BlockContainer block;
    private ItemStack itemStack;
    private ClusterRegistry nextSubRegistry;
    private ClusterRegistry prevSubRegistry;
    private int id;

    private ClusterRegistry(Class<? extends TileEntityClusterElement> clazz, BlockContainer block, ItemStack itemStack) {
        this.clazz = clazz;
        this.block = block;
        this.itemStack = itemStack;
        this.id = registryList.size();
    }

    public static void register(Class<? extends TileEntityClusterElement> clazz, BlockContainer block) {
        register(clazz, block, new ItemStack(block));
    }

    public static void register(Class<? extends TileEntityClusterElement> clazz, BlockContainer block, ItemStack itemStack) {
        ClusterRegistry registryElement = new ClusterRegistry(clazz, block, itemStack);
        registryList.add(registryElement);
        ClusterRegistry parent = registry.get(clazz);
        if (parent == null) {
            registry.put(clazz, registryElement);
        }else{
            parent.nextSubRegistry = registryElement;
            registryElement.prevSubRegistry = parent;
        }
    }

    public int getId() {
        return id;
    }

    public int getParentId() {
        if (prevSubRegistry == null) {
            return -1;
        }else{
            return prevSubRegistry.id;
        }
    }


    public int getChildId() {
        if (nextSubRegistry == null) {
            return -1;
        }else{
            return nextSubRegistry.id;
        }
    }

    public BlockContainer getBlock() {
        return block;
    }

    public ItemStack getItemStack(boolean isAdvanced) {
        if (isAdvanced) {
            return nextSubRegistry.itemStack;
        }
        return itemStack;
    }

    public static ClusterRegistry get(TileEntityClusterElement tileEntityClusterElement) {
        return registry.get(tileEntityClusterElement.getClass());
    }

    public static List<ClusterRegistry> getRegistryList() {
        return registryList;
    }

}
