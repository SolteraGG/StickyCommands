package com.dumbdogdiner.stickycommands.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemDeserializer {
    private final String resourceName;
    private static final GsonComponentSerializer G = GsonComponentSerializer.gson();
    private Map<String, ItemStack> items;
    private static final JsonParser P = new JsonParser();
    public ItemDeserializer(String resourceName) {
        this.resourceName = resourceName;
        reloadItems();
    }

    public void reloadItems(){
        items = new HashMap<>();
        File resource = ResourceUtils.getOrCreate(resourceName);
        try {
            JsonObject itemData = P.parse(new FileReader(resource)).getAsJsonObject();
            for(Map.Entry<String,JsonElement> entry : itemData.entrySet()) {
                try {
                    JsonObject itemObj = entry.getValue().getAsJsonObject();//itemData.getAsJsonObject(entry.getValue());
                    ItemStack stack = new ItemStack(Material.valueOf(itemObj.get("item").getAsString()));
                    ItemMeta meta = stack.getItemMeta();
                    meta.displayName(G.deserializeFromTree(itemObj.get("display")));
                    if (itemObj.has("unbreakable"))
                        meta.setUnbreakable(itemObj.get("unbreakable").getAsBoolean());
                    List<Component> lore = new ArrayList<>();
                    for (JsonElement loreEl : itemObj.get("lore").getAsJsonArray()) {
                        lore.add(G.deserializeFromTree(loreEl));
                    }
                    meta.lore(lore);
                    stack.setItemMeta(meta);
                    JsonElement ench = itemObj.get("enchantment");
                    if(ench != null) {
                        Enchantment e = Enchantment.getByKey(NamespacedKey.minecraft(ench.getAsString()));
                        stack.addUnsafeEnchantment(e, 1);
                    }
                    items.put(entry.getKey(), stack);
                } catch (Throwable e){
                    e.printStackTrace();
                    System.err.println(entry.getKey());
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ItemStack get(String key){
        return items.get(key);
    }

}
