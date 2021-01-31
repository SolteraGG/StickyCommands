/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.listeners

import com.dumbdogdiner.stickycommands.StickyCommands
import kotlin.math.floor
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerToggleFlightEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.event.player.PlayerToggleSprintEvent

class AfkEventListener : Listener {

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        val player = event.player
        val from = event.from
        val to = event.to
        val hasMoved = (floor(to.x) != floor(from.x) || floor(to.y) != floor(from.y) || floor(to.z) != floor(from.z))

        if (hasMoved && (player.world.getBlockAt(to).type != Material.WATER) &&
            (!player.isSwimming || !player.isInsideVehicle || !player.isGliding || !player.isInBubbleColumn) &&
            from.block.type != Material.WATER &&
            (!nearbyContainsPlayer(player))) {
            // Reset their AFK status
            checkAfk(event)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onToggleFlight(event: PlayerToggleFlightEvent) {
        checkAfk(event)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onToggleSneak(event: PlayerToggleSneakEvent) {
        checkAfk(event)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onToggleSprint(event: PlayerToggleSprintEvent) {
        checkAfk(event)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onChat(event: AsyncPlayerChatEvent) {
        checkAfk(event)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onInteract(event: PlayerInteractEvent) {
        checkAfk(event)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onRespawn(event: PlayerRespawnEvent) {
        checkAfk(event)
    }

    private fun checkAfk(event: PlayerEvent) {
        val state = StickyCommands.instance.playerStateManager.getPlayerState(event.player)
        state.resetAfkTime()
        if (state.isAfk) {
            state.setAfk(false, true)
        }
    }

    private fun nearbyContainsPlayer(player: Player): Boolean {
        for (entity in player.world.getNearbyEntities(player.location, 1.0, 1.0, 1.0)) {
            // We need to check if something like a sheep or villager moved the player! So we can just use LivingEntity
            if (entity is LivingEntity && entity != player) return true
        }
        return false
    }
}
