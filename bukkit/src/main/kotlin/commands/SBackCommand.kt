/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.commands

import com.dumbdogdiner.stickyapi.bukkit.util.SoundUtil
import com.dumbdogdiner.stickyapi.common.util.NotificationType
import com.dumbdogdiner.stickycommands.util.Constants
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.executors.CommandExecutor
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
            player.chat("/spawn") // Requested by Stixil
            return@PlayerCommandExecutor SoundUtil.sendError(player)
        }

        player.teleport(location)
        player.sendMessage(locale.translate(Constants.LanguagePaths.BACK_TELEPORTED, playerVariables(player)))
        return@PlayerCommandExecutor SoundUtil.sendSuccess(player)
    })

private val sBackOther = commandStub("sback", Constants.Permissions.SBACK_OTHER)
    .withArguments(PlayerArgument("player"))
    .executes(CommandExecutor { player, args ->
        val target = args[0] as Player
        val location = postgresHandler.getLocation(target.uniqueId)
        val variables = playerVariables(target, true)

        if (location == null) {
            player.sendMessage(locale.translate(Constants.LanguagePaths.BACK_NO_PREVIOUS, variables))
            SoundUtil.send(player, NotificationType.ERROR)
            target.chat("/spawn") // Requested by Stixil
            return@CommandExecutor
        }

        target.teleport(location)
        target.sendMessage(locale.translate(Constants.LanguagePaths.BACK_TELEPORTED, variables))
        player.sendMessage(locale.translate(Constants.LanguagePaths.BACK_TELEPORTED_OTHER, variables))
        SoundUtil.send(player, NotificationType.SUCCESS)
    })
