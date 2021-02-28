/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.listeners

import com.dumbdogdiner.stickycommands.WithPlugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent

class TeleportEventListener : Listener, WithPlugin {

    @EventHandler
    fun onTeleport(event: PlayerTeleportEvent) {
        plugin.postgresHandler.updateLocation(event.player)
    }
}
