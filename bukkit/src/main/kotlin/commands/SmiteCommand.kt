/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.commands

import com.dumbdogdiner.stickyapi.bukkit.util.SoundUtil
import com.dumbdogdiner.stickycommands.util.Constants
import com.dumbdogdiner.stickycommands.util.Variables
import dev.jorel.commandapi.arguments.EntitySelectorArgument
import dev.jorel.commandapi.arguments.LocationArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object smiteCommand {
    fun register() {
        smitePlayerCommand.register()
        smiteBlockCommand.register()
        smiteWhereLooking.register()
    }
}

private val smitePlayerCommand = commandStub("smite", Constants.Permissions.SMITE)
    .withArguments(EntitySelectorArgument("entities", EntitySelectorArgument.EntitySelector.MANY_PLAYERS))
    .executesPlayer(PlayerCommandExecutor { player, args ->
        val entities = args[0] as Collection<Entity>
        if (entities.size == 1 && entities.firstOrNull() != null && entities.first() is Player) {
            smitePlayer(player, entities.first() as Player)
        } else {
            smiteEntities(player, entities)
        }
    })

private val smiteBlockCommand = commandStub("smite", Constants.Permissions.SMITE)
    .withArguments(LocationArgument("location"))
    .executesPlayer(PlayerCommandExecutor { player, args ->
        smiteLocation(player, args[0] as Location)
    })

private val smiteWhereLooking = commandStub("smite", Constants.Permissions.SMITE)
    .executesPlayer(PlayerCommandExecutor { player, _ ->
        smiteLocation(player, player.getTargetBlock(null, Constants.SMITE_TARGET_RANGE).location)
    })

private fun smitePlayer(sender: Player, target: Player) {
    val vars = Variables().withPlayer(sender, false).get()
    vars.putAll(Variables().withPlayer(target, true).get())
    when (true) {
        (target != sender && target.hasPermission(Constants.Permissions.SMITE_IMMUNE)) -> {
            sender.sendMessage(locale.translate(Constants.LanguagePaths.SMITE_IMMUNE, vars))
            SoundUtil.sendError(sender)
            return
        }
        (target != sender) -> {
            sender.sendMessage(locale.translate(Constants.LanguagePaths.SMITE_OTHER, vars))
            target.sendMessage(locale.translate(Constants.LanguagePaths.SMITE_MESSAGE, vars))
        }
        (target == sender) -> sender.sendMessage(locale.translate(Constants.LanguagePaths.SMITE_YOURSELF, vars))
    }
    strikeLightning(target.location)
    SoundUtil.sendSuccess(sender)
}

private fun smiteEntities(sender: Player, entities: Collection<Entity>) {
    val vars = Variables().withPlayer(sender, false).get()
        entities.forEach loop@{
            if (it is LivingEntity) {
                if (it.hasPermission(Constants.Permissions.SMITE_IMMUNE)) return@loop
                it.sendMessage(locale.translate(Constants.LanguagePaths.SMITE_MESSAGE, vars))
            }
            strikeLightning(it.location)
        }
        vars["amount"] = entities.size.toString()
        sender.sendMessage(locale.translate(Constants.LanguagePaths.SMITE_ENTITIES, vars))
        SoundUtil.sendSuccess(sender)
}

private fun smiteLocation(sender: Player, location: Location) {
    val vars = Variables().withPlayer(sender, false).get()
    vars.putAll(Variables().withLocation(location).get())
    strikeLightning(location)
    sender.sendMessage(locale.translate(Constants.LanguagePaths.SMITE_BLOCK, vars))
    SoundUtil.sendSuccess(sender)
}

private fun strikeLightning(location: Location) {
    location.world.strikeLightningEffect(location)
    location.world.createExplosion(location, Constants.SMITE_EXPLOSION_STRENGTH, false, false)
}
