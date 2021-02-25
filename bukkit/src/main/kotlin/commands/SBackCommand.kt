package com.dumbdogdiner.stickycommands.commands

import com.dumbdogdiner.stickycommands.StickyCommands
import com.dumbdogdiner.stickycommands.util.Constants
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.entity.Player

val sbackCommand = commandStub("sback", Constants.Permissions.BACK)
    .executesPlayer(PlayerCommandExecutor { player, _ ->
        sback(player)
    })

val sbackOtherCommand = commandStub("sback", Constants.Permissions.BACK_OTHERS)
    .withArguments(PlayerArgument("player"))
    .executesPlayer(PlayerCommandExecutor { _, args ->
        sback(args[0] as Player)
    })

private fun sback(sender: Player) {
    // TODO: ideally getting particular bits should be in postgres handler not like this!
    val to = StickyCommands.plugin.postgresHandler.getUserLocation(sender.uniqueId) ?: return
    sender.teleport(to)
}
