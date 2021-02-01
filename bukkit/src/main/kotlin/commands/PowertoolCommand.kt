/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.commands

import com.dumbdogdiner.stickyapi.bukkit.command.BukkitCommandBuilder
import com.dumbdogdiner.stickyapi.common.command.ExitCode
import com.dumbdogdiner.stickycommands.StickyCommands
import com.dumbdogdiner.stickycommands.item.StickyPowertool
import com.dumbdogdiner.stickycommands.util.Variables
import java.util.HashMap
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player

object PowertoolCommand {
    private val locale = StickyCommands.localeProvider!!
    val command = BukkitCommandBuilder("powertool")
        .description("Bind an item to a command")
        .permission("stickycommands.powertool")
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
                    sender.sendMessage(locale.translate("powertool.cannot-bind-air", vars))
                    ExitCode.EXIT_EXPECTED_ERROR
                }
                vars["command"] = args.getString("command")
                val powertool = StickyPowertool(player, player.inventory.itemInMainHand.type, args.getString("command"), true)
                StickyCommands.instance.powertoolManager.add(powertool)

                sender.sendMessage(locale.translate("powertool.assigned", vars))
                ExitCode.EXIT_SUCCESS
            }
        }
        .onError { exitCode, _, _, vars ->
            onError(exitCode, vars)
        }

        .subCommand(
            BukkitCommandBuilder("clear")
                .description("Clear your item of a command")
                .permission("stickycommands.powertool.clear")
                .requiresPlayer()
                .playSound()

                .onExecute { sender, _, vars ->

                    val player = sender as Player
                    clearTool(player, vars)
                }
                .onError { exitCode, _, _, vars ->
                    onError(exitCode, vars)
                }
        )

        .subCommand(
            BukkitCommandBuilder("toggle")
                .description("Toggle your powertool")
                .permission("stickycommands.powertool.toggle")
                .requiresPlayer()
                .playSound()

                .onExecute { sender, _, vars ->
                    val player = sender as Player
                    // can't assign your hand...
                    if (player.inventory.itemInMainHand.type == Material.AIR) {
                        sender.sendMessage(locale.translate("powertool.cannot-bind-air", vars))
                        ExitCode.EXIT_EXPECTED_ERROR
                    }
                    val powertool = StickyCommands.instance.powertoolManager.getPowerTool(player, player.inventory.itemInMainHand.type)
                    if (powertool == null) {
                        player.sendMessage(locale.translate("powertool.no-powertool", vars))
                        ExitCode.EXIT_EXPECTED_ERROR
                    } else {
                        powertool.isEnabled = !powertool.isEnabled
                        vars["toggled"] = (powertool.isEnabled).toString()

                        sender.sendMessage(locale.translate("powertool.toggled", vars))
                        ExitCode.EXIT_SUCCESS
                    }
                }
                .onError { exitCode, _, _, vars ->
                    onError(exitCode, vars)
                }
        )

        .onTabComplete { sender, _, _ ->
            val list = mutableListOf<String>()
            for (command in Bukkit.getCommandMap().knownCommands) {
                if (sender.hasPermission(command.value.permission ?: "stickycommands.viewallcommands"))
                    list.add(command.value.name)
            }
            list
        }

    private fun clearTool(player: Player, vars: HashMap<String, String>): ExitCode {
        vars["syntax"] = "/powertool [command/clear/toggle]"
        vars.putAll(Variables(player, false).get())
        if (player.inventory.itemInMainHand.type == Material.AIR) {
            // TODO: Move to messages
            player.sendMessage(locale.translate("prefix", vars) + ChatColor.RED + "You do not have a powertool in your hand!")
            return ExitCode.EXIT_EXPECTED_ERROR
        }

        val powertool = StickyCommands.instance.powertoolManager.getPowerTool(player, player.inventory.itemInMainHand.type)
        if (powertool != null) {
            StickyCommands.instance.powertoolManager.remove(powertool)
        }
        player.sendMessage(locale.translate("powertool.cleared", vars))
        return ExitCode.EXIT_SUCCESS
    }

    private fun onError(exitCode: ExitCode, vars: HashMap<String, String>): String {
        return when (exitCode) {
            ExitCode.EXIT_PERMISSION_DENIED -> locale.translate("no-permission", vars)
            ExitCode.EXIT_MUST_BE_PLAYER -> locale.translate("must-be-player", vars)
            ExitCode.EXIT_ERROR -> locale.translate("server-error", vars)
            ExitCode.EXIT_INVALID_SYNTAX -> locale.translate("invalid-syntax", vars)
            else -> ""
        }
    }
}
