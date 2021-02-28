/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.item

import com.dumbdogdiner.stickycommands.api.item.Powertool
import com.dumbdogdiner.stickycommands.api.item.PowertoolBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

// snep
class StickyPowertoolBuilder(
    private var command: String,
    private val material: Material

) : PowertoolBuilder() {

    override fun setCommand(command: String): PowertoolBuilder {
        this.command = command
        return this
    }

    override fun give(target: Player): Powertool {
        val powertool = StickyPowertool(target, this.material, this.command, true)
        target.inventory.addItem(ItemStack(this.material))

        return powertool
    }
}
