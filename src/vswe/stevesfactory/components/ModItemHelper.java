package vswe.stevesfactory.components;


import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.ItemData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.HashMap;
import java.util.Map;

public final class ModItemHelper {


    private static Map<Integer, String> items;

    public static void init() {
        NBTTagList lst = new NBTTagList();
        GameData.writeItemData(lst);

        items = new HashMap<Integer, String>();
        for (int i = 0; i < lst.tagCount(); i++) {
            ItemData data = new ItemData((NBTTagCompound)lst.tagAt(i));

            int id = data.getItemId();
            String modId = data.getModId();

            items.put(id, modId);
        }
    }

    public static boolean areItemsFromSameMod(Item item1, Item item2) {
        if (item1 == null || item2 == null) {
            return false;
        }else{
            String mod1 = items.get(item1.itemID);
            String mod2 = items.get(item2.itemID);

            return mod1 != null && mod1.equals(mod2);
        }
    }



    private ModItemHelper() {}
}
