/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.util.sticky

import com.dumbdogdiner.stickycommands.StickyCommands
import com.dumbdogdiner.stickycommands.WithPlugin
import com.dumbdogdiner.stickycommands.commands.afkCommand
import com.dumbdogdiner.stickycommands.commands.killCommand
import com.dumbdogdiner.stickycommands.commands.powertoolCommand
import com.dumbdogdiner.stickycommands.commands.sBackCommand
import com.dumbdogdiner.stickycommands.commands.seenCommand
import com.dumbdogdiner.stickycommands.commands.sellCommand
import com.dumbdogdiner.stickycommands.commands.smiteCommand
import com.dumbdogdiner.stickycommands.commands.speedCommand
import com.dumbdogdiner.stickycommands.commands.stickyCommand
import com.dumbdogdiner.stickycommands.commands.whoisCommand
import com.dumbdogdiner.stickycommands.commands.worthCommand
import com.dumbdogdiner.stickycommands.listeners.AfkEventListener
import com.dumbdogdiner.stickycommands.listeners.ConnectionEventListener
import com.dumbdogdiner.stickycommands.listeners.PowertoolEventListener
import com.dumbdogdiner.stickycommands.listeners.TeleportEventListener
import com.dumbdogdiner.stickycommands.tasks.StickyTask
import dev.jorel.commandapi.CommandAPI
import java.io.File
import java.util.Timer
import net.luckperms.api.LuckPerms
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.plugin.RegisteredServiceProvider

object StickyStartupUtil : WithPlugin {

    fun registerCommands() {
        logger.fine("Registering commands...")
        afkCommand.register()
        seenCommand.register()
        whoisCommand.register()
        powertoolCommand.register()
        sellCommand.register()
        speedCommand.register()
        stickyCommand.register()

        CommandAPI.unregister("worth")
        worthCommand.register()
        smiteCommand.register()
        sBackCommand.register()
        killCommand.register()
    }

    fun registerListeners() {
        logger.fine("Registering listeners...")
        plugin.server.pluginManager.registerEvents(AfkEventListener(), plugin)
        plugin.server.pluginManager.registerEvents(ConnectionEventListener(), plugin)
        plugin.server.pluginManager.registerEvents(PowertoolEventListener(), plugin)
        plugin.server.pluginManager.registerEvents(TeleportEventListener(), plugin)
    }

    fun registerTimers(vararg timers: StickyTask) {
        logger.fine("Starting timers...")
        timers.forEach { Timer().scheduleAtFixedRate(it, it.delay, it.period) }
    }

    /*
        Setup utils
    */
    fun setupStaffFacilities(): Boolean {
        StickyCommands.staffFacilitiesEnabled = Bukkit.getPluginManager().getPlugin("StaffFacilities") != null
        return StickyCommands.staffFacilitiesEnabled
    }

    fun setupPlaceholders(): Boolean {
        return if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            Bukkit.getLogger().info("Registering PlaceholderAPI placeholders")

            StickyPlaceholders.instance.register()
            true
        } else false
    }

    fun setupEconomy(): Boolean {
        if (plugin.server.pluginManager.getPlugin("Vault") == null) {
            return false
        }
        val rsp: RegisteredServiceProvider<Economy> = plugin.server.servicesManager.getRegistration(Economy::class.java) ?: return false
        StickyCommands.economy = rsp.provider
        return StickyCommands.economy != null
    }

    fun setupLuckperms(): Boolean {
        val provider = Bukkit.getServicesManager().getRegistration(
            LuckPerms::class.java
        )
        return if (provider != null) {
            StickyCommands.perms = provider.provider
            true
        } else {
            false
        }
    }

    fun setupConfig(): Boolean {
        try {
            if (!plugin.dataFolder.exists()) {
                plugin.logger.info("Error: No folder was found! Creating...")
                if (!plugin.dataFolder.mkdirs()) {
                    plugin.logger.info("Error: Unable to create data folder, are your file permissions correct?")
                    return false
                }
                plugin.saveResource("config.yml", false)
                plugin.logger.info("The folder was created successfully!")
            }
            if (!File(plugin.dataFolder.absolutePath + "config.yml").exists()) {
                plugin.saveResource("config.yml", false)
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}
