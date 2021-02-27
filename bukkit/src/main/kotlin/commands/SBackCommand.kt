/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.commands

import com.dumbdogdiner.stickyapi.bukkit.util.SoundUtil
import com.dumbdogdiner.stickycommands.util.Constants
import dev.jorel.commandapi.executors.PlayerCommandExecutor

val sBackCommand = commandStub("sback", Constants.Permissions.SBACK)
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
