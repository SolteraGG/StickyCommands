/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.commands

import com.dumbdogdiner.stickyapi.bukkit.util.SoundUtil
import com.dumbdogdiner.stickycommands.util.Constants
import com.dumbdogdiner.stickycommands.util.Variables
import dev.jorel.commandapi.arguments.LocationArgument
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.Location
import org.bukkit.entity.Player

object smiteCommand {
    fun register() {
        smitePlayerCommand.register()
        smiteBlockCommand.register()
        smiteWhereLooking.register()
    }
}

private val smitePlayerCommand = commandStub("smite", Constants.Permissions.SMITE)
    .withArguments(PlayerArgument("player"))
    .executesPlayer(PlayerCommandExecutor { player, args ->
        smite(player, args[0] as Player, null)
    })

private val smiteBlockCommand = commandStub("smite", Constants.Permissions.SMITE)
    .withArguments(LocationArgument("location"))
    .executesPlayer(PlayerCommandExecutor { player, args ->
        smite(player, null, args[0] as Location)
    })

private val smiteWhereLooking = commandStub("smite", Constants.Permissions.SMITE)
    .executesPlayer(PlayerCommandExecutor { player, _ ->
        smite(player, null, player.getTargetBlock(null, Constants.SMITE_TARGET_RANGE).location)
    })

private fun smite(sender: Player, target: Player?, location: Location?) {
    val variables = Variables()
    val vars = variables.withPlayer(sender, false).get()
    val realLocation = target?.location ?: location!!
    target?.let { variables.withPlayer(it, true).get() }?.let { vars.putAll(it) }
    vars.putAll(variables.withLocation(realLocation).get())
    strikeLightning(realLocation)

    when (true) {
        (target != null && target === sender) -> sender.sendMessage(locale.translate(Constants.LanguagePaths.SMITE_YOURSELF, vars))
        (target != null && target != sender) -> {
            if (target.hasPermission(Constants.Permissions.SMITE_IMMUNE)) {
                sender.sendMessage(locale.translate(Constants.LanguagePaths.SMITE_IMMUNE, vars))
                SoundUtil.sendError(sender)
                return
            }
            sender.sendMessage(locale.translate(Constants.LanguagePaths.SMITE_OTHER, vars))
            target.sendMessage(locale.translate(Constants.LanguagePaths.SMITE_MESSAGE, vars))
        }
        (target == null && location != null) -> sender.sendMessage(locale.translate(Constants.LanguagePaths.SMITE_BLOCK, vars))
    }
    SoundUtil.sendSuccess(sender)
}

private fun strikeLightning(location: Location) {
    location.world.strikeLightningEffect(location)
    location.world.createExplosion(location, Constants.SMITE_EXPLOSION_STRENGTH, false, false)
}
