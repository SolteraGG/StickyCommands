/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.commands

import com.dumbdogdiner.stickyapi.common.command.ExitCode
import com.dumbdogdiner.stickycommands.StickyCommands
import com.dumbdogdiner.stickycommands.util.Constants
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.CustomArgument.CustomArgumentException
import dev.jorel.commandapi.arguments.CustomArgument.CustomArgumentParser
import dev.jorel.commandapi.arguments.CustomArgument.MessageBuilder
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

internal val locale = StickyCommands.localeProvider!!
internal val worthTable = StickyCommands.plugin.worthTable
internal val market = StickyCommands.plugin.market
internal val postgresHandler = StickyCommands.plugin.postgresHandler
internal val plugin = StickyCommands.plugin

internal fun printError(exitCode: ExitCode, sender: CommandSender, vars: HashMap<String, String>) {
    when (exitCode) {
        ExitCode.EXIT_PERMISSION_DENIED -> sender.sendMessage(locale.translate(Constants.LanguagePaths.NO_PERMISSION, vars))
        ExitCode.EXIT_MUST_BE_PLAYER -> sender.sendMessage(locale.translate(Constants.LanguagePaths.MUST_BE_PLAYER, vars))
        ExitCode.EXIT_ERROR -> sender.sendMessage(locale.translate(Constants.LanguagePaths.SERVER_ERROR, vars))
        ExitCode.EXIT_INVALID_SYNTAX -> sender.sendMessage(locale.translate(Constants.LanguagePaths.INVALID_SYNTAX, vars))
        else -> ""
    }
}

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
        Bukkit.getCommandMap().knownCommands.filter { sender.hasPermission(it.value.permission.toString()) }.entries.map { it.key }.toTypedArray()
    }
}
