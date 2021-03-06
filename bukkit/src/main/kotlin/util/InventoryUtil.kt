/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.util

import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object InventoryUtil {

    /**
     * Remove items from a player's inventory
     * @param inventory to remove items from
     * @param type of material to remove from the inventory
     * @param amount of items to remove
     */
    fun removeItems(inventory: Inventory, type: Material, amount: Int) {
        var _amount = amount
        if (_amount <= 0) return
        val size: Int = inventory.size
        for (slot in 0 until size) {
            val stack: ItemStack = inventory.getItem(slot) ?: continue
            if (type == stack.type) {
                val newAmount = stack.amount - _amount
                if (newAmount > 0) {
                    stack.amount = newAmount
                    break
                } else {
                    inventory.clear(slot)
                    _amount = -newAmount
                    if (_amount == 0) break
                }
            }
        }
    }

    /**
     * Get the count of a material in an inventory
     * @param inventory to count
     * @param type of material to count
    */
    fun count(inventory: Inventory, type: Material): Int {
        var count = 0
        val size = inventory.size
        for (slot in 0 until size) {
            val stack: ItemStack = inventory.getItem(slot) ?: continue
            if (type == stack.type) {
                count += stack.amount
            }
        }
        return count
    }
}
