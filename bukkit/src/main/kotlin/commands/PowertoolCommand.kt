/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.commands

import com.dumbdogdiner.stickyapi.common.command.ExitCode
import com.dumbdogdiner.stickycommands.StickyCommands
import com.dumbdogdiner.stickycommands.item.StickyPowertool
import com.dumbdogdiner.stickycommands.util.Constants
import com.dumbdogdiner.stickycommands.util.Variables
import java.util.HashMap
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player

val powertoolCommand = commandStub("powertool", Constants.Descriptions.POWERTOOL, Constants.Permissions.POWERTOOL)
    .requiresPlayer()

    .onExecute { sender, args, vars -> sender as Player
        args.optionalSentence("command")

        // If there are no arguments, we assume they are trying to clear their powertool
        if (!args.exists("command"))
            clearTool(sender, vars)
        // Otherwise assign a command
        else {

            // can't assign your hand...
            if (bindingAir(sender, vars))
                return@onExecute ExitCode.EXIT_EXPECTED_ERROR

            vars["command"] = args.getString("command")
            val powertool = StickyPowertool(sender, sender.inventory.itemInMainHand.type, args.getString("command"), true)
            StickyCommands.plugin.powertoolManager.add(powertool)

            sender.sendMessage(locale.translate(Constants.LanguagePaths.POWERTOOL_ASSIGNED, vars))
            ExitCode.EXIT_SUCCESS
        }
    }
    .subCommand(
        commandStub("clear", Constants.Descriptions.POWERTOOL_CLEAR, Constants.Permissions.POWERTOOL_CLEAR)
            .requiresPlayer()
            .onExecute { sender, _, vars ->

                val player = sender as Player
                clearTool(player, vars)
            }
    )
    .subCommand(
        commandStub("toggle", Constants.Descriptions.POWERTOOL_TOGGLE, Constants.Permissions.POWERTOOL_TOGGLE)
        .requiresPlayer()
        .onExecute { sender, _, vars ->
            sender as Player
            // can't assign your hand...
            if (bindingAir(sender, vars))
                return@onExecute ExitCode.EXIT_EXPECTED_ERROR

            val powertool = StickyCommands.plugin.powertoolManager.getPowerTool(sender, sender.inventory.itemInMainHand.type)
            if (powertool == null) {
                sender.sendMessage(locale.translate(Constants.LanguagePaths.NO_POWERTOOL, vars))
                return@onExecute ExitCode.EXIT_EXPECTED_ERROR
            } else {
                powertool.isEnabled = !powertool.isEnabled
                vars["toggled"] = (powertool.isEnabled).toString()

                sender.sendMessage(locale.translate(Constants.LanguagePaths.POWERTOOL_TOGGLED, vars))
                ExitCode.EXIT_SUCCESS
            }
        }
    )

    .onTabComplete { sender, _, args ->
        val list = mutableListOf<String>()
        for (command in Bukkit.getCommandMap().knownCommands) {
            if (sender.hasPermission(command.value.permission ?: Constants.Permissions.POWERTOOL_VIEW_ALL_COMMANDS) && command.value.name.startsWith(args.rawArgs[0], true)) {
                list.add(command.value.name)
            }
        }
        return@onTabComplete if (args.rawArgs.size > 1) listOf() else list
    }

private fun bindingAir(sender: Player, vars: HashMap<String, String>): Boolean {
    if (sender.inventory.itemInMainHand.type == Material.AIR) {
        sender.sendMessage(locale.translate(Constants.LanguagePaths.POWERTOOL_CANNOT_BIND_AIR, vars))
        return true
    }
    return false
}

private fun clearTool(player: Player, vars: HashMap<String, String>): ExitCode {
    vars["syntax"] = "/powertool [command/clear/toggle]"
    vars.putAll(Variables().withPlayer(player, false).get())
    if (player.inventory.itemInMainHand.type == Material.AIR) {
        // TODO: Move to messages
        player.sendMessage(locale.translate(Constants.LanguagePaths.PREFIX, vars) + ChatColor.RED + "You do not have a powertool in your hand!")
        return ExitCode.EXIT_EXPECTED_ERROR
    }

    val powertool = StickyCommands.plugin.powertoolManager.getPowerTool(player, player.inventory.itemInMainHand.type)
    if (powertool != null) {
        StickyCommands.plugin.powertoolManager.remove(powertool)
    }
    player.sendMessage(locale.translate(Constants.LanguagePaths.POWERTOOL_CLEARED, vars))
    return ExitCode.EXIT_SUCCESS
}
