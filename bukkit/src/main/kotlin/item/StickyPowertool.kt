/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.item

import com.dumbdogdiner.stickycommands.api.item.Powertool
import org.bukkit.Material
import org.bukkit.entity.Player

class StickyPowertool(
    private val player: Player,
    private val material: Material,
    private var command: String,
    private var enabled: Boolean
) : Powertool {

    override fun getPlayer(): Player {
        return this.player
    }

    override fun getMaterial(): Material {
        return this.material
    }

    override fun getCommand(): String {
        return this.command
    }

    override fun setCommand(command: String) {
        this.command = command
    }

    override fun isEnabled(): Boolean {
        return this.enabled
    }

    override fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
    }

    fun execute() {
        if (this.enabled) {
            if (this.command.startsWith("c:")) {
                this.player.chat(this.command.replaceFirst("c:", ""))
            } else {
                this.player.performCommand(command)
            }
        }
    }
}
