package com.dumbdogdiner.stickycommands.utils;

import com.dumbdogdiner.stickyapi.common.config.FileConfiguration;
import com.dumbdogdiner.stickyapi.common.config.providers.YamlProvider;
import com.dumbdogdiner.stickyapi.common.util.MathUtil;
import com.dumbdogdiner.stickycommands.StickyCommands;
import com.dumbdogdiner.stickycommands.utils.Constants;
import com.dumbdogdiner.stickycommands.utils.ResourceUtils;
import com.google.common.collect.ImmutableList;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;

@UtilityClass
public class ItemWorths {
    private static YamlConfiguration worthCfg;

    static {
        reloadWorths();
    }


    public static void reloadWorths() {
        worthCfg = YamlConfiguration.loadConfiguration(ResourceUtils.getOrCreate(Constants.Files.ITEM_WORTHS));
    }

    private static final ImmutableList<Material> illegals = ImmutableList.of(
            Material.COMMAND_BLOCK, Material.REPEATING_COMMAND_BLOCK, Material.JIGSAW, Material.STRUCTURE_BLOCK,
            Material.BEDROCK, Material.KNOWLEDGE_BOOK, Material.CHAIN_COMMAND_BLOCK, Material.COMMAND_BLOCK_MINECART,
            Material.DEBUG_STICK
    );

    public static double getWorthOfItems(List<ItemStack> stacks){
        double worth = 0D;
        // Control the scope for these variables, use persistent to prevent reallocation
        {
            Material type;
            int qty;
            double singleWorth;
            double durability;

            ItemMeta stackMeta;
            for (ItemStack stack : stacks) {
                type = stack.getType();
                qty = stack.getAmount();
                singleWorth = ItemWorths.get(type);
                stackMeta = stack.getItemMeta();

                // Silk touch is worht double
                if(stack.containsEnchantment(Enchantment.SILK_TOUCH))
                    singleWorth *= 2;
                // Take damage into account
                // TODO: figure out how to use damagable properly


                if (isDamagable(stack)) {
                    durability = ((double) ((Damageable) stackMeta).getDamage())
                            /((double) type.getMaxDurability());
                    singleWorth = damagableWorth(singleWorth, durability);
                }
                worth += singleWorth * qty;
            }
        }

        return MathUtil.round2Places(worth);
    }

    /**
     *
     * @param baseWorth The base worth of the item
     * @param damageAmount (Between 0 and 1)
     * @return The worht after damage is taken into account
     */
    public static double damagableWorth(double baseWorth, double damageAmount){
        // Feel free to graph this equation, or maybe improve
        return baseWorth * Math.min(1.004D/Math.sqrt(1+Math.pow(damageAmount/0.554D, -8.4D)), 1);
    }

    /**
     * Get the value of a given material
     * @param key Material to examine
     * @return The value of it
     */
    public static double get(Material key) {
        StickyCommands.getInstance().getLogger().severe(key.toString());
        return worthCfg.getDouble(key.toString(), 0D);
    }

    public static boolean isIllegal(ItemStack item){
        if(item == null)
            return false;
        return isIllegal(item.getType());
    }

    public static boolean isIllegal(Material material){
        return illegals.contains(material);
    }

    public static boolean isDamagable(ItemStack itemStack){
        return isDamagable(itemStack.getType());
    }

    public static boolean isDamagable(Material type){
        return type.getMaxDurability() > 0;
    }


}
