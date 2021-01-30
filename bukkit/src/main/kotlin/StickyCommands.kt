/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands

import com.dumbdogdiner.stickyapi.bukkit.util.StartupUtil
import com.dumbdogdiner.stickyapi.common.translation.LocaleProvider
import com.dumbdogdiner.stickycommands.api.StickyCommands
import com.dumbdogdiner.stickycommands.api.player.PlayerStateManager
import com.dumbdogdiner.stickycommands.player.StickyPlayerStateManager
import com.dumbdogdiner.stickycommands.util.StickyPlaceholders
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
    }

    private val playerStateManager = StickyPlayerStateManager()

    override fun onLoad() {
        instance = this
    }

    override fun onEnable() {
        if (!StartupUtil.setupConfig(this))
            return

        localeProvider = StartupUtil.setupLocale(this, localeProvider)
        if (localeProvider == null)
            return

        if (!setupPlaceholders())
            getLogger().severe("PlaceholderAPI is not availible, is it installed?")

        if (!setupEconomy())
            getLogger().severe("Disabled economy commands due to no Vault dependency found!")

        if (!setupLuckperms())
            getLogger().severe("Disabled group listing/luckperms dependant features due to no Luckperms dependency found!")

        if (!setupStaffFacilities())
            getLogger().severe("StaffFacilities not found, disabling integration")
    }

    override fun onDisable() {
        reloadConfig()
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
        return this.playerStateManager
    }
}
