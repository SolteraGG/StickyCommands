/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.managers

import com.dumbdogdiner.stickycommands.api.item.Powertool
import com.dumbdogdiner.stickycommands.api.managers.PowertoolManager
import org.bukkit.Material
import org.bukkit.entity.Player

// There might be a better way of handling this
// but oh well
class StickyPowertoolManager : PowertoolManager {
    private val powertools = mutableSetOf<Powertool>()

    /**
     * Get a powertool of a player by material
     * @param player to get powertool of
     * @param type of powertool to get
     */
    override fun getPowerTool(player: Player, type: Material): Powertool? {
        for (powertool in powertools) {
            if (powertool.player == player && powertool.material == type) {
                return powertool
            }
        }
        return null
    }

    /**
     * Get all current powertools
     */
    override fun getPowertools(): Set<Powertool> {
        return this.powertools
    }

    /**
     * Add a powertool to the manager
     */
    override fun add(powertool: Powertool) {
        this.powertools.add(powertool)
    }

    /**
     * Remove a powertool from the manager
     */
    override fun remove(powertool: Powertool) {
        this.powertools.remove(powertool)
    }

    /**
     * Remove a powertool from a player
     */
    override fun remove(player: Player) {
        powertools.forEach { if (it.player == player) this.powertools.remove(it) }
    }
}
