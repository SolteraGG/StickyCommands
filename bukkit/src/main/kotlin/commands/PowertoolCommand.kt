/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.commands

import com.dumbdogdiner.stickyapi.bukkit.util.SoundUtil
import com.dumbdogdiner.stickycommands.StickyCommands
import com.dumbdogdiner.stickycommands.item.StickyPowertool
import com.dumbdogdiner.stickycommands.util.Constants
import com.dumbdogdiner.stickycommands.util.Variables
import dev.jorel.commandapi.arguments.GreedyStringArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import java.util.HashMap
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player

val powertoolCommand = commandStub("powertool", Constants.Permissions.POWERTOOL)
    .withArguments(GreedyStringArgument("command"))
    .executesPlayer(PlayerCommandExecutor { sender, args ->
        val vars = Variables().withPlayer(sender, false).get()
        val command = args.joinToString(" ")

        // can't assign your hand...
        if (bindingAir(sender, vars))
            return@PlayerCommandExecutor

        vars["command"] = command
        val powertool = StickyPowertool(sender, sender.inventory.itemInMainHand.type, command, true)
        StickyCommands.plugin.powertoolManager.add(powertool)

        sender.sendMessage(locale.translate(Constants.LanguagePaths.POWERTOOL_ASSIGNED, vars))
        SoundUtil.sendSuccess(sender)
    })
    .withSubcommand(
        commandStub("clear", Constants.Permissions.POWERTOOL_CLEAR)
            .executesPlayer(PlayerCommandExecutor { sender, _ ->
                val vars = Variables().withPlayer(sender, false).get()
                val player = sender as Player
                clearTool(player, vars)
                SoundUtil.sendSuccess(sender)
        })
    )
    .withSubcommand(
        commandStub("toggle", Constants.Permissions.POWERTOOL_TOGGLE)
        .executesPlayer(PlayerCommandExecutor { sender, _ ->
            val vars = Variables().withPlayer(sender, false).get()
            // can't assign your hand...
            if (bindingAir(sender, vars))
                return@PlayerCommandExecutor

            val powertool = StickyCommands.plugin.powertoolManager.getPowerTool(sender, sender.inventory.itemInMainHand.type)
            if (powertool == null) {
                sender.sendMessage(locale.translate(Constants.LanguagePaths.NO_POWERTOOL, vars))
                return@PlayerCommandExecutor
            } else {
                powertool.isEnabled = !powertool.isEnabled
                vars["toggled"] = (powertool.isEnabled).toString()

                sender.sendMessage(locale.translate(Constants.LanguagePaths.POWERTOOL_TOGGLED, vars))
                SoundUtil.sendSuccess(sender)
            }
        })
    )

private fun bindingAir(sender: Player, vars: HashMap<String, String>): Boolean {
    if (sender.inventory.itemInMainHand.type == Material.AIR) {
        sender.sendMessage(locale.translate(Constants.LanguagePaths.POWERTOOL_CANNOT_BIND_AIR, vars))
        SoundUtil.sendError(sender)
        return true
    }
    return false
}

private fun clearTool(sender: Player, vars: HashMap<String, String>) {
    vars["syntax"] = "/powertool [command/clear/toggle]"
    vars.putAll(Variables().withPlayer(sender, false).get())
    if (sender.inventory.itemInMainHand.type == Material.AIR) {
        // TODO: Move to messages
        sender.sendMessage(locale.translate(Constants.LanguagePaths.PREFIX, vars) + ChatColor.RED + "You do not have a powertool in your hand!")
        SoundUtil.sendError(sender)
        return
    }

    val powertool = StickyCommands.plugin.powertoolManager.getPowerTool(sender, sender.inventory.itemInMainHand.type)
    if (powertool != null) {
        StickyCommands.plugin.powertoolManager.remove(powertool)
    }
    sender.sendMessage(locale.translate(Constants.LanguagePaths.POWERTOOL_CLEARED, vars))
    return
}
