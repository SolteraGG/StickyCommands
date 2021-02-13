/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.commands

import com.dumbdogdiner.stickyapi.bukkit.command.BukkitCommandBuilder
import com.dumbdogdiner.stickyapi.common.command.ExitCode
import com.dumbdogdiner.stickycommands.StickyCommands
import com.dumbdogdiner.stickycommands.item.StickyPowertool
import com.dumbdogdiner.stickycommands.util.Constants
import com.dumbdogdiner.stickycommands.util.Variables
import java.util.HashMap
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object PowertoolCommand {
    private val locale = StickyCommands.localeProvider!!
    val command = BukkitCommandBuilder("powertool")
            .description(Constants.Descriptions.POWERTOOL)
            .permission(Constants.Permissions.POWERTOOL)
            .requiresPlayer()
            .playSound()

        .onExecute { sender, args, vars ->
            val player = sender as Player
            args.optionalSentence("command")

            // If there are no arguments, we assume they are trying to clear their powertool
            if (!args.exists("command"))
                clearTool(player, vars)
            // Otherwise assign a command
            else {

                // can't assign your hand...
                if (player.inventory.itemInMainHand.type == Material.AIR) {
                    sender.sendMessage(locale.translate(Constants.LanguagePaths.POWERTOOL_CANNOT_BIND_AIR, vars))
                    ExitCode.EXIT_EXPECTED_ERROR
                }
                vars["command"] = args.getString("command")
                val powertool = StickyPowertool(player, player.inventory.itemInMainHand.type, args.getString("command"), true)
                StickyCommands.plugin.powertoolManager.add(powertool)

                sender.sendMessage(locale.translate(Constants.LanguagePaths.POWERTOOL_ASSIGNED, vars))
                ExitCode.EXIT_SUCCESS
            }
        }
        .onError { exitCode, sender, _, vars ->
            onError(sender, exitCode, vars)
        }

        .subCommand(
            BukkitCommandBuilder("clear")
                    .description(Constants.Descriptions.POWERTOOL_CLEAR)
                    .permission(Constants.Permissions.POWERTOOL_CLEAR)
                    .requiresPlayer()
                    .playSound()
                .onExecute { sender, _, vars ->

                    val player = sender as Player
                    clearTool(player, vars)
                }
                .onError { exitCode, sender, _, vars ->
                    onError(sender, exitCode, vars)
                }
        )
        .subCommand(
            BukkitCommandBuilder("toggle")
                .description(Constants.Descriptions.POWERTOOL_TOGGLE)
                .permission(Constants.Permissions.POWERTOOL_TOGGLE)
                .requiresPlayer()
                .playSound()

                .onExecute { sender, _, vars ->
                    val player = sender as Player
                    // can't assign your hand...
                    if (player.inventory.itemInMainHand.type == Material.AIR) {
                        sender.sendMessage(locale.translate(Constants.LanguagePaths.POWERTOOL_CANNOT_BIND_AIR, vars))
                        return@onExecute ExitCode.EXIT_EXPECTED_ERROR
                    }

                    val powertool = StickyCommands.plugin.powertoolManager.getPowerTool(player, player.inventory.itemInMainHand.type)
                    if (powertool == null) {
                        player.sendMessage(locale.translate(Constants.LanguagePaths.NO_POWERTOOL, vars))
                        return@onExecute ExitCode.EXIT_EXPECTED_ERROR
                    } else {
                        powertool.isEnabled = !powertool.isEnabled
                        vars["toggled"] = (powertool.isEnabled).toString()

                        sender.sendMessage(locale.translate(Constants.LanguagePaths.POWERTOOL_TOGGLED, vars))
                        ExitCode.EXIT_SUCCESS
                    }
                }
                .onError { exitCode, sender, _, vars ->
                    onError(sender, exitCode, vars)
                }
        )

        .onTabComplete { sender, _, _ ->
            val list = mutableListOf<String>()
            for (command in Bukkit.getCommandMap().knownCommands) {
                if (sender.hasPermission(command.value.permission ?: Constants.Permissions.POWERTOOL_VIEW_ALL_COMMANDS))
                    list.add(command.value.name)
            }
            list
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

    private fun onError(sender: CommandSender, exitCode: ExitCode, vars: HashMap<String, String>) {
        when (exitCode) {
            ExitCode.EXIT_PERMISSION_DENIED -> sender.sendMessage(locale.translate(Constants.LanguagePaths.NO_PERMISSION, vars))
            ExitCode.EXIT_MUST_BE_PLAYER -> sender.sendMessage(locale.translate(Constants.LanguagePaths.MUST_BE_PLAYER, vars))
            ExitCode.EXIT_ERROR -> sender.sendMessage(locale.translate(Constants.LanguagePaths.SERVER_ERROR, vars))
            else -> ""
        }
    }
}
