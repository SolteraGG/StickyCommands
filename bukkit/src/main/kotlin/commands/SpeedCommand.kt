/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.commands

import com.dumbdogdiner.stickyapi.bukkit.util.SoundUtil
import com.dumbdogdiner.stickycommands.StickyCommands
import com.dumbdogdiner.stickycommands.util.Constants
import com.dumbdogdiner.stickycommands.util.Variables
import dev.jorel.commandapi.arguments.IntegerArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor

val speedCommand = commandStub("speed", Constants.Permissions.SPEED)
    .withArguments(IntegerArgument("range", 1, 10))
    .executesPlayer(PlayerCommandExecutor { sender, args ->
        val speed = (args[0] as Int).toFloat() // You can't cast an Int to a Float????
        val vars = Variables().withPlayer(sender, false).get()
        val state = StickyCommands.plugin.playerStateManager.getPlayerState(sender)
        state.setSpeed(speed / 10)

        vars["player_speed"] = "$speed"
        vars["player_flying"] = "$sender.isFlying"
        sender.sendMessage(locale.translate(Constants.LanguagePaths.SPEED_MESSAGE, vars))
        SoundUtil.sendSuccess(sender)
    })
