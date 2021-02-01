/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands

import com.dumbdogdiner.stickyapi.bukkit.util.StartupUtil
import com.dumbdogdiner.stickyapi.common.translation.LocaleProvider
import com.dumbdogdiner.stickycommands.api.StickyCommands
import com.dumbdogdiner.stickycommands.api.managers.PlayerStateManager
import com.dumbdogdiner.stickycommands.api.managers.PowertoolManager
import com.dumbdogdiner.stickycommands.commands.AfkCommand
import com.dumbdogdiner.stickycommands.commands.PowertoolCommand
import com.dumbdogdiner.stickycommands.listeners.AfkEventListener
import com.dumbdogdiner.stickycommands.listeners.ConnectionListener
import com.dumbdogdiner.stickycommands.listeners.PowertoolListener
import com.dumbdogdiner.stickycommands.managers.StickyPlayerStateManager
import com.dumbdogdiner.stickycommands.managers.StickyPowertoolManager
import com.dumbdogdiner.stickycommands.timers.AfkTimer
import com.dumbdogdiner.stickycommands.util.StickyPlaceholders
import java.util.Timer
import kr.entree.spigradle.annotations.PluginMain
import net.luckperms.api.LuckPerms
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.RegisteredServiceProvider
import org.bukkit.plugin.java.JavaPlugin

@PluginMain
class StickyCommands : JavaPlugin(), StickyCommands {
    companion object {
        lateinit var instance: com.dumbdogdiner.stickycommands.StickyCommands
        var economy: Economy? = null
        var localeProvider: LocaleProvider? = null
        var perms: LuckPerms? = null
        var staffFacilitiesEnabled = false
        val _playerStateManager = StickyPlayerStateManager()
        val _powertoolManager = StickyPowertoolManager()
    }
    lateinit var afkTimer: AfkTimer

    override fun onLoad() {
        instance = this
        afkTimer = AfkTimer()
    }

    override fun onEnable() {
        if (!StartupUtil.setupConfig(this))
            return

        localeProvider = StartupUtil.setupLocale(this, localeProvider)
        if (localeProvider == null)
            return

        if (!setupPlaceholders())
            getLogger().severe("PlaceholderAPI is not available, is it installed?")

        if (!setupEconomy())
            getLogger().severe("Disabled economy commands due to no Vault dependency found!")

        if (!setupLuckperms())
            getLogger().severe("Disabled group listing/LuckPerms dependant features due to no LuckPerms dependency found!")

        if (!setupStaffFacilities())
            getLogger().severe("StaffFacilities not found, disabling integration")

        registerCommands()
        registerListeners()
        registerTimers()
    }

    override fun onDisable() {
        reloadConfig()
        afkTimer.cancel()
    }

    private fun registerListeners() {
        server.pluginManager.registerEvents(AfkEventListener(), this)
        server.pluginManager.registerEvents(ConnectionListener(), this)
        server.pluginManager.registerEvents(PowertoolListener(), this)
    }

    private fun registerCommands() {
        AfkCommand.command.register(this)
        PowertoolCommand.command.register(this)
    }

    private fun registerTimers() {
        Timer().scheduleAtFixedRate(afkTimer, 1000L, 1000L)
    }

    /*
        Setup utils
    */
    private fun setupStaffFacilities(): Boolean {
        staffFacilitiesEnabled = Bukkit.getPluginManager().getPlugin("StaffFacilities") != null
        return staffFacilitiesEnabled
    }

    private fun setupPlaceholders(): Boolean {
        return if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            Bukkit.getLogger().info("Registering PlaceholderAPI placeholders")

            StickyPlaceholders.instance.register()
            true
        } else false
    }

    private fun setupEconomy(): Boolean {
        if (server.pluginManager.getPlugin("Vault") == null) {
            return false
        }
        val rsp: RegisteredServiceProvider<Economy> = server.servicesManager.getRegistration(Economy::class.java)
            ?: return false
        economy = rsp.provider
        return economy != null
    }

    private fun setupLuckperms(): Boolean {
        val provider = Bukkit.getServicesManager().getRegistration(
            LuckPerms::class.java
        )
        return if (provider != null) {
            perms = provider.provider
            true
        } else {
            false
        }
    }

    /*

    */
    override fun getProvider(): Plugin {
        return this
    }

    override fun getPlayerStateManager(): PlayerStateManager {
        return _playerStateManager
    }
    override fun getPowertoolManager(): PowertoolManager {
        return _powertoolManager
    }
}
