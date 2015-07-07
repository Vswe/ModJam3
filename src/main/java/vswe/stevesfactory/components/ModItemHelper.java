package vswe.stevesfactory.components;


import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.HashMap;
import java.util.Map;

public final class ModItemHelper {


    private static Map<Integer, String> items;

    public static void init() {
        FMLControlledNamespacedRegistry<Item> itemRegistry = GameData.getItemRegistry();

        items = new HashMap<Integer, String>();
        Object[] keys = itemRegistry.getKeys().toArray();
        for (int i = 0; i < keys.length; i++) {


            Item item = (Item) itemRegistry.getObject((ResourceLocation) keys[i]);
            GameRegistry.UniqueIdentifier uniqueIdentity = GameRegistry.findUniqueIdentifierFor(item);
            String modId = uniqueIdentity == null ? null : uniqueIdentity.modId;

            items.put(i, modId);
        }
    }

    public static boolean areItemsFromSameMod(Item item1, Item item2) {
        if (item1 == null || item2 == null) {
            return false;
        }else{
            String mod1 = items.get(GameData.getItemRegistry().getId(item1));
            String mod2 = items.get(GameData.getItemRegistry().getId(item2));

            return mod1 != null && mod1.equals(mod2);
        }
    }



    private ModItemHelper() {}
}
