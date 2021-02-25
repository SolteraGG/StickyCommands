package com.dumbdogdiner.stickycommands.listeners

import com.dumbdogdiner.stickycommands.WithPlugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerTeleportEvent

class WorldChangeListener : Listener, WithPlugin{

    @EventHandler
    fun onWorldChange(event : PlayerChangedWorldEvent){
        plugin.postgresHandler.updateUserLastWorld(event.player, event.from)
    }
}