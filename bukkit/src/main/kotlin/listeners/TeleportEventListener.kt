package com.dumbdogdiner.stickycommands.listeners

import com.dumbdogdiner.stickycommands.WithPlugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent

class TeleportEventListener : Listener, WithPlugin {

    @EventHandler
    fun onTeleport(event: PlayerTeleportEvent) {
        plugin.postgresHandler.updateUser(event.player, false)
    }
}