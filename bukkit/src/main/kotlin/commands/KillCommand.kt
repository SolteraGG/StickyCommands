/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.commands

import com.dumbdogdiner.stickyapi.bukkit.util.SoundUtil
import com.dumbdogdiner.stickyapi.common.util.NotificationType
import com.dumbdogdiner.stickycommands.util.Constants
import com.dumbdogdiner.stickycommands.util.Variables
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.executors.CommandExecutor
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.entity.Player

object killCommand {
    fun register() {
        CommandAPI.unregister("kill") // unregister minecraft's /kill command
        killCommandWithArgument.register()
        killCommandWithoutArgument.register()
    }
}

private val killCommandWithArgument = commandStub("kill", Constants.Permissions.KILL)
    .withArguments(PlayerArgument("player"))
    .executes(CommandExecutor { sender, args ->
        val target = args[0] as Player
        val message = if (target === sender) Constants.LanguagePaths.SUICIDE else Constants.LanguagePaths.YOU_KILLED
        target.health = 0.0
        sender.sendMessage(locale.translate(message, Variables().withPlayer(target, target !== sender).get()))
        if (target !== sender)
            target.sendMessage(locale.translate(Constants.LanguagePaths.YOU_WERE_KILLED, Variables().withPlayer(target, true).get()))
        SoundUtil.send(sender, NotificationType.SUCCESS)
    })

private val killCommandWithoutArgument = commandStub("kill", Constants.Permissions.KILL)
    .executesPlayer(PlayerCommandExecutor { sender, _ ->
        sender.health = 0.0
        sender.sendMessage(locale.translate(Constants.LanguagePaths.SUICIDE, Variables().withPlayer(sender, false).get()))
        SoundUtil.sendSuccess(sender)
    })
