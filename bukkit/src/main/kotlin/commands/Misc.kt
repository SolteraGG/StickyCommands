/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.commands

import com.dumbdogdiner.stickycommands.StickyCommands
import com.dumbdogdiner.stickycommands.util.Constants
import com.dumbdogdiner.stickycommands.util.Variables
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.CustomArgument.CustomArgumentException
import dev.jorel.commandapi.arguments.CustomArgument.CustomArgumentParser
import dev.jorel.commandapi.arguments.CustomArgument.MessageBuilder
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal val locale = StickyCommands.localeProvider!!
internal val worthTable = StickyCommands.plugin.worthTable
internal val market = StickyCommands.plugin.market
internal val postgresHandler = StickyCommands.plugin.postgresHandler
internal val plugin = StickyCommands.plugin

internal fun playerVariables(player: Player, target: Boolean) = Variables().withPlayer(player, false).get()
internal fun playerVariables(player: Player) = playerVariables(player, false)

internal fun commandStub(name: String, permission: String): CommandAPICommand = CommandAPICommand(name).withPermission(permission)

internal fun commandArgument(node: String?): Argument {
    return CustomArgument(node, CustomArgumentParser { input: String ->
        val command = Bukkit.getCommandMap().getCommand(input)
        if (command == null) {
            throw CustomArgumentException(MessageBuilder("Unknown command: ").appendArgInput())
        } else {
            return@CustomArgumentParser command
        }
    }).overrideSuggestions { sender: CommandSender ->
        Bukkit.getCommandMap().knownCommands.filter { sender.hasPermission(it.value.permission ?: Constants.Permissions.POWERTOOL_VIEW_ALL_COMMANDS) }.entries.map { it.key }.toTypedArray()
    }
}
