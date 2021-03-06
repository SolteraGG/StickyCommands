/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands

import org.bukkit.Bukkit
import org.bukkit.event.Event

interface WithPlugin {
    val plugin
        get() = StickyCommands.plugin

    /**
     * The plugin logger
     */
    val logger
        get() = this.plugin.logger

    /**
     * The plugin config.
     */
    val config
        get() = this.plugin.config

    /**
     * Short-hand to quickly call a bukkit event.
     */
    fun callBukkitEvent(event: Event) { Bukkit.getServer().pluginManager.callEvent(event) }
}
