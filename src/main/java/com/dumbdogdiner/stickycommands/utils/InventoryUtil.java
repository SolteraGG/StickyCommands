package com.dumbdogdiner.stickycommands.utils;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

public class InventoryUtil {
    /**
     * Remove items from a player's inventory
     * @param inventory to remove items from
     * @param type of material to remove from the inventory
     * @param amount of items to remove
     */
    public static void removeItems(Inventory inventory, Material type, int amount) {
        if (amount <= 0) return;
        var size = inventory.getSize();
        for (var slot = 0; slot < size; slot++) {
            var stack = inventory.getItem(slot);
            if (stack == null) continue;
            if (type == stack.getType()) {
                var newAmount = stack.getAmount() - amount;
                if (newAmount > 0) {
                    stack.setAmount(newAmount);
                    break;
                } else {
                    inventory.clear(slot);
                    amount = -newAmount;
                    if (amount == 0) break;
                }
            }
        }
    }

    /**
     * Get the count of a material in an inventory
     * @param inventory to count
     * @param type of material to count
     */
    public static int count(Inventory inventory, Material type) {
        var count = 0;
        var size = inventory.getSize();
        for (var slot = 0; slot < size; slot++) {
            var stack = inventory.getItem(slot);
            if (stack == null) continue;
            if (type == stack.getType()) {
                count += stack.getAmount();
            }
        }
        return count;
    }
}
