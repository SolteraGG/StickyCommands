/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.commands

import com.dumbdogdiner.stickyapi.bukkit.command.BukkitCommandBuilder
import com.dumbdogdiner.stickyapi.common.command.ExitCode
import com.dumbdogdiner.stickycommands.StickyCommands
import com.dumbdogdiner.stickycommands.util.Constants
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

internal val locale = StickyCommands.localeProvider!!
internal val worthTable = StickyCommands.plugin.worthTable
internal val market = StickyCommands.plugin.market
internal val postgresHandler = StickyCommands.plugin.postgresHandler

internal fun printError(exitCode: ExitCode, sender: CommandSender, vars: HashMap<String, String>) {
    when (exitCode) {
        ExitCode.EXIT_PERMISSION_DENIED -> sender.sendMessage(locale.translate(Constants.LanguagePaths.NO_PERMISSION, vars))
        ExitCode.EXIT_MUST_BE_PLAYER -> sender.sendMessage(locale.translate(Constants.LanguagePaths.MUST_BE_PLAYER, vars))
        ExitCode.EXIT_ERROR -> sender.sendMessage(locale.translate(Constants.LanguagePaths.SERVER_ERROR, vars))
        ExitCode.EXIT_INVALID_SYNTAX -> sender.sendMessage(locale.translate(Constants.LanguagePaths.INVALID_SYNTAX, vars))
        else -> ""
    }
}

internal fun commandStub(name: String, description: String, permission: String): BukkitCommandBuilder {
    return BukkitCommandBuilder(name)
        .synchronous(false)
        .description(description)
        .permission(permission)
        .playSound()
        .onError { exitCode, sender, _, vars ->
            printError(exitCode, sender, vars)
        }
        .onExecute { _, _, _ -> ExitCode.EXIT_INVALID_SYNTAX }
        .onTabComplete { _, _, args ->
            val list = mutableListOf<String>()
            if (args.rawArgs.size == 1) {
                Bukkit.getOnlinePlayers().forEach {
                    if (it.name.startsWith(args.rawArgs[0], true))
                        list.add(it.name)
                }
            }
            return@onTabComplete list
        }
}
