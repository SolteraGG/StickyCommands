/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.commands

import com.dumbdogdiner.stickyapi.bukkit.command.BukkitCommandBuilder
import com.dumbdogdiner.stickyapi.common.arguments.Arguments
import com.dumbdogdiner.stickyapi.common.command.ExitCode
import com.dumbdogdiner.stickycommands.StickyCommands
import com.dumbdogdiner.stickycommands.util.Constants
import com.google.common.collect.ImmutableList
import java.util.HashMap
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object AfkCommand {
    private val locale = StickyCommands.localeProvider!!
    val command = BukkitCommandBuilder("afk")
        .description(Constants.Descriptions.AFK)
        .permission(Constants.Permissions.AFK)
        .requiresPlayer()
        .playSound()

        .onExecute { sender: CommandSender, _: Arguments?, _: HashMap<String, String>? ->
            val player = sender as Player
            val state = StickyCommands.plugin.playerStateManager.getPlayerState(player)
            state.setAfk(!state.isAfk, true)
            ExitCode.EXIT_SUCCESS
        }

        .onError { exitCode, sender, _, vars ->
            when (exitCode) {
                ExitCode.EXIT_PERMISSION_DENIED -> sender.sendMessage(locale.translate(Constants.LanguagePaths.NO_PERMISSION, vars))
                ExitCode.EXIT_MUST_BE_PLAYER -> sender.sendMessage(locale.translate(Constants.LanguagePaths.MUST_BE_PLAYER, vars))
                ExitCode.EXIT_ERROR -> sender.sendMessage(locale.translate(Constants.LanguagePaths.SERVER_ERROR, vars))
                else -> ""
            }
        }

        .onTabComplete { _, _, _ ->
            ImmutableList.of("")
        }
}
