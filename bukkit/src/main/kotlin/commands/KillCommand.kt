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
import dev.jorel.commandapi.arguments.EntitySelectorArgument
import dev.jorel.commandapi.executors.CommandExecutor
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object killCommand {
    fun register() {
        CommandAPI.unregister("kill") // unregister minecraft's /kill command
        killCommandWithArgument.register()
        killCommandWithoutArgument.register()
    }
}

private val killCommandWithArgument = commandStub("kill", Constants.Permissions.KILL)
    .withArguments(EntitySelectorArgument("entities", EntitySelectorArgument.EntitySelector.MANY_ENTITIES))
    .executes(CommandExecutor { sender, args ->
        val entities = args[0] as Collection<Entity>
        val vars = Variables().withSender(sender).get()
        if (entities.size == 1 && entities.firstOrNull() != null && entities.first() is Player) {
            val target = entities.first() as Player
            vars.putAll(Variables().withPlayer(target, true).get())
            when (true) {
                (target != sender && target.hasPermission(Constants.Permissions.KILL_IMMUNE)) -> {
                    sender.sendMessage(locale.translate(Constants.LanguagePaths.KILL_IMMUNE, vars))
                    SoundUtil.send(sender, NotificationType.ERROR)
                    return@CommandExecutor
                }
                (target != sender) -> {
                    sender.sendMessage(locale.translate(Constants.LanguagePaths.YOU_KILLED, vars))
                    target.sendMessage(locale.translate(Constants.LanguagePaths.YOU_WERE_KILLED, vars))
                }
                (target == sender) -> sender.sendMessage(locale.translate(Constants.LanguagePaths.SUICIDE, vars))
            }
            target.health = 0.0
        } else {
            entities.forEach loop@{
                if (it is LivingEntity) {
                    if (it.hasPermission(Constants.Permissions.KILL_IMMUNE)) return@loop
                    it.health = 0.0
                    it.sendMessage(locale.translate(Constants.LanguagePaths.YOU_WERE_KILLED, vars))
                } else {
                    it.remove()
                }
            }
            vars["amount"] = entities.size.toString()
            sender.sendMessage(locale.translate(Constants.LanguagePaths.YOU_KILLED_ENTITIES, vars))
    }

    SoundUtil.send(sender, NotificationType.SUCCESS)
    })

private val killCommandWithoutArgument = commandStub("kill", Constants.Permissions.KILL)
    .executesPlayer(PlayerCommandExecutor { sender, _ ->
        sender.health = 0.0
        sender.sendMessage(locale.translate(Constants.LanguagePaths.SUICIDE, Variables().withPlayer(sender, false).get()))
        SoundUtil.sendSuccess(sender)
    })
