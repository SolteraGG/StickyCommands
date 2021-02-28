/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.commands

import com.dumbdogdiner.stickyapi.bukkit.util.SoundUtil
import com.dumbdogdiner.stickycommands.util.Constants
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.entity.Player

object sBackCommand {
    fun register() {
        sBackSingle.register()
        sBackOther.register()
    }
}

private val sBackSingle = commandStub("sback", Constants.Permissions.SBACK)
    .executesPlayer(PlayerCommandExecutor { player, _ ->
        val location = postgresHandler.getLocation(player.uniqueId)
        if (location == null) {
            player.sendMessage(locale.translate(Constants.LanguagePaths.BACK_NO_PREVIOUS, playerVariables(player)))
            return@PlayerCommandExecutor SoundUtil.sendError(player)
        }

        player.teleport(location)
        player.sendMessage(locale.translate(Constants.LanguagePaths.BACK_TELEPORTED, playerVariables(player)))
        return@PlayerCommandExecutor SoundUtil.sendSuccess(player)
    })

private val sBackOther = commandStub("sback", Constants.Permissions.SBACK_OTHER)
    .withArguments(PlayerArgument("player"))
    .executesPlayer(PlayerCommandExecutor { player, args ->
        val target = args[0] as Player
        val location = postgresHandler.getLocation(target.uniqueId)
        val variables = playerVariables(player)
        variables.putAll(playerVariables(target, true))

        if (location == null) {
            player.sendMessage(locale.translate(Constants.LanguagePaths.BACK_NO_PREVIOUS, variables))
            return@PlayerCommandExecutor SoundUtil.sendError(player)
        }

        target.teleport(location)
        target.sendMessage(locale.translate(Constants.LanguagePaths.BACK_TELEPORTED, variables))
        player.sendMessage(locale.translate(Constants.LanguagePaths.BACK_TELEPORTED_OTHER, variables))
        return@PlayerCommandExecutor SoundUtil.sendSuccess(player)
    })
