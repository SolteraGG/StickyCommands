package com.dumbdogdiner.stickycommands.item

import com.dumbdogdiner.stickycommands.api.item.Powertool
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import com.dumbdogdiner.stickycommands.api.item.PowertoolBuilder

// snep
class PowertoolBuilder(
    private var command: String,
    private val material: Material

): PowertoolBuilder() {

    override fun setCommand(command: String): PowertoolBuilder {
        this.command = command
        return this
    }

    override fun give(target: Player): Powertool {
        val powertool = com.dumbdogdiner.stickycommands.item.Powertool(target, this.material, this.command, true)
        target.inventory.addItem(ItemStack(this.material))

        return powertool
    }

}