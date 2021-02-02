/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.listeners

import com.dumbdogdiner.stickycommands.StickyCommands
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class PowertoolListener : Listener {
    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val player = event.player
        val item = player.inventory.itemInMainHand
        val powertool = StickyCommands.plugin.powertoolManager.getPowerTool(player, item.type)

        if (item.type == Material.AIR || (powertool == null || !powertool.isEnabled))
            return

        // Make sure we don't trigger when a player steps on a pressure plate or similar
        if (!(event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK || event.action == Action.LEFT_CLICK_BLOCK || event.action == Action.LEFT_CLICK_AIR))
            return

        event.isCancelled = true
        powertool.execute()
    }
}
