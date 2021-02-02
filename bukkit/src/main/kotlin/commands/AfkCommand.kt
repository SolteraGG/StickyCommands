/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.commands

import com.dumbdogdiner.stickyapi.bukkit.command.BukkitCommandBuilder
import com.dumbdogdiner.stickyapi.common.arguments.Arguments
import com.dumbdogdiner.stickyapi.common.command.ExitCode
import com.dumbdogdiner.stickycommands.StickyCommands
import com.google.common.collect.ImmutableList
import java.util.HashMap
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object AfkCommand {
    private val locale = StickyCommands.localeProvider!!
    val command = BukkitCommandBuilder("afk")
        .description("Let the server know you're afk!")
        .permission("stickycommands.afk")
        .requiresPlayer()
        .playSound()

        .onExecute { sender: CommandSender, _: Arguments?, _: HashMap<String, String>? ->
            val player = sender as Player
            val state = StickyCommands.plugin.playerStateManager.getPlayerState(player)
            state.setAfk(!state.isAfk, true)
            ExitCode.EXIT_SUCCESS
        }

        .onError { exitCode, _, _, vars ->
            when (exitCode) {
                ExitCode.EXIT_PERMISSION_DENIED -> locale.translate("no-permission", vars)
                ExitCode.EXIT_MUST_BE_PLAYER -> locale.translate("must-be-player", vars)
                ExitCode.EXIT_ERROR -> locale.translate("server-error", vars)
                ExitCode.EXIT_INVALID_SYNTAX -> locale.translate("invalid-syntax", vars)
            }
        }

        .onTabComplete { _, _, _ ->
            ImmutableList.of("")
        }
}
