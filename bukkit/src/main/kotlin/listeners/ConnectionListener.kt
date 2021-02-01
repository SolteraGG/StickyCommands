/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.listeners

import com.dumbdogdiner.stickycommands.StickyCommands
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerQuitEvent

class ConnectionListener : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        StickyCommands._playerStateManager.createPlayerState(event.player)
    }

    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        StickyCommands._playerStateManager.removePlayerState(event.player)
        StickyCommands.instance.powertoolManager.remove(event.player)
    }

    @EventHandler
    fun onKick(event: PlayerKickEvent) {
        StickyCommands._playerStateManager.removePlayerState(event.player)
        StickyCommands.instance.powertoolManager.remove(event.player)
    }
}
