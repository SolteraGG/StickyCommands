/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.util

import com.dumbdogdiner.stickyapi.common.configuration.InvalidConfigurationException
import com.dumbdogdiner.stickyapi.common.configuration.file.FileConfiguration
import com.dumbdogdiner.stickyapi.common.configuration.file.YamlConfiguration
import com.dumbdogdiner.stickycommands.StickyCommands
import java.io.File
import java.io.IOException
import java.text.DecimalFormat
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

// FIXME I feel this could be done better...
class WorthTable : WithPlugin {
    var configFile: File? = null
    var localConfig: FileConfiguration? = null
    var decimalFormat = DecimalFormat("0.00") // We don't want something like 25.3333333333, instead we want 25.33

    init {
        val worthFile: String? = plugin.config.getString("worth-file", "worth.yml")
        configFile = File(plugin.dataFolder, worthFile)
        if (!configFile!!.exists()) {
            configFile!!.parentFile.mkdirs()
            plugin.saveResource(worthFile!!, false)
        }

        val fc: FileConfiguration = YamlConfiguration()
        try {
            fc.load(configFile!!)
            localConfig = fc
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InvalidConfigurationException) {
            e.printStackTrace()
        }
    }

    /**
     * Get the worth of an {@link org.bukkit.inventory.ItemStack}
     *
     * @param stack The ItemStack
     * @return The worth of the ItemStack
     */
    fun getWorth(stack: ItemStack): Double {
        val name: String = stack.type.toString()
        val worth = localConfig!!.getDouble(name, 0.0)
        return if (!isSellable(stack)) 0.0 else decimalFormat.format(worth).toDouble()
    }

    /**
     * Check if an item stack has "notsellable" in nbt data
     * @param stack The ItemStack
     * @return True if the item can be sold
     */
    fun isSellable(stack: ItemStack): Boolean {
        val meta = stack.itemMeta
        val dataStore = meta.persistentDataContainer
        return !dataStore.has(NamespacedKey(StickyCommands.plugin, "notsellable"), PersistentDataType.STRING)
    }
}
