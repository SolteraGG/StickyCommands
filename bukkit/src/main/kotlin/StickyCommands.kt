/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands

import com.dumbdogdiner.stickyapi.bukkit.util.StartupUtil
import com.dumbdogdiner.stickyapi.common.translation.LocaleProvider
import com.dumbdogdiner.stickycommands.api.StickyCommands
import com.dumbdogdiner.stickycommands.api.economy.Market
import com.dumbdogdiner.stickycommands.api.managers.PowertoolManager
import com.dumbdogdiner.stickycommands.commands.AfkCommand
import com.dumbdogdiner.stickycommands.commands.PowertoolCommand
import com.dumbdogdiner.stickycommands.commands.SellCommand
import com.dumbdogdiner.stickycommands.economy.StickyMarket
import com.dumbdogdiner.stickycommands.listeners.AfkEventListener
import com.dumbdogdiner.stickycommands.listeners.ConnectionListener
import com.dumbdogdiner.stickycommands.listeners.PowertoolListener
import com.dumbdogdiner.stickycommands.managers.StickyPlayerStateManager
import com.dumbdogdiner.stickycommands.managers.StickyPowertoolManager
import com.dumbdogdiner.stickycommands.models.Listings
import com.dumbdogdiner.stickycommands.models.Transactions
import com.dumbdogdiner.stickycommands.models.Users
import com.dumbdogdiner.stickycommands.timers.AfkTimer
import com.dumbdogdiner.stickycommands.util.ExposedLogger
import com.dumbdogdiner.stickycommands.util.StickyPlaceholders
import com.dumbdogdiner.stickycommands.util.WorthTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.util.Timer
import kr.entree.spigradle.annotations.PluginMain
import net.luckperms.api.LuckPerms
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.RegisteredServiceProvider
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

@PluginMain
class StickyCommands : JavaPlugin(), StickyCommands {
    companion object {
        lateinit var plugin: com.dumbdogdiner.stickycommands.StickyCommands
        var economy: Economy? = null
        var localeProvider: LocaleProvider? = null
        var perms: LuckPerms? = null
        var staffFacilitiesEnabled = false
    }
    private val _playerStateManager = StickyPlayerStateManager()
    private val _powertoolManager = StickyPowertoolManager()
    lateinit var _market: Market
    lateinit var afkTimer: AfkTimer
    lateinit var db: Database
    lateinit var worthTable: WorthTable

    override fun onLoad() {
        plugin = this
        afkTimer = AfkTimer()
        _market = StickyMarket()
        worthTable = WorthTable()
    }

    override fun onEnable() {
        StickyCommands.registerService(this, this)

        if (!StartupUtil.setupConfig(this))
            return

        localeProvider = StartupUtil.setupLocale(this, localeProvider)
        if (localeProvider == null)
            return

        if (!setupDatabase())
            return

        if (!setupPlaceholders())
            logger.severe("PlaceholderAPI is not available, is it installed?")

        if (!setupEconomy())
            logger.severe("Disabled economy commands due to no Vault dependency found!")

        if (!setupLuckperms())
            logger.severe("Disabled group listing/LuckPerms dependant features due to no LuckPerms dependency found!")

        if (!setupStaffFacilities())
            logger.severe("StaffFacilities not found, disabling integration")

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
        SellCommand.command.register(this)
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

    private fun setupDatabase(): Boolean {
        var success = true
        this.logger.info("[SQL] Checking SQL database has been set up correctly...")

        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:postgresql://${
                config.getString("database.host")
            }:${
                config.getInt("database.port")
            }/${
                config.getString("database.database")
            }?sslmode=disable"

            driverClassName = "com.dumbdogdiner.stickycommands.libs.org.postgresql.Driver"
            username = config.getString("database.username", "postgres")!!
            password = config.getString("database.password")!!
            maximumPoolSize = 2
        }

        val dataSource = HikariDataSource(config)
        this.db = Database.connect(dataSource)

        transaction(this.db) {
            try {
                addLogger(ExposedLogger())
                SchemaUtils.createMissingTablesAndColumns(Transactions)
                SchemaUtils.createMissingTablesAndColumns(Users)
                SchemaUtils.createMissingTablesAndColumns(Listings)
            } catch (e: Exception) {
                logger.warning("[SQL] Failed to connect to SQL database - invalid connection info/database not up")
                success = false
            }
        }
        return success
    }

    /*

    */
    override fun getProvider(): Plugin {
        return this
    }

    override fun getPlayerStateManager(): StickyPlayerStateManager {
        return _playerStateManager
    }
    override fun getPowertoolManager(): PowertoolManager {
        return _powertoolManager
    }

    override fun getMarket(): Market {
        return _market
    }
}
