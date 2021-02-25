package com.dumbdogdiner.stickycommands.commands

import com.dumbdogdiner.stickycommands.StickyCommands
import com.dumbdogdiner.stickycommands.util.Constants
import com.dumbdogdiner.stickycommands.util.Variables
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.entity.Player

val sbackCommand = commandStub("sback", Constants.Permissions.BACK)
    .executesPlayer(PlayerCommandExecutor { player, _ ->
        if (sback(player)) { return@PlayerCommandExecutor }
        player.sendMessage(locale.translate(Constants.LanguagePaths.SBACK_NON_EXISTENT_LOCATION, Variables()))
    })

val sbackOtherCommand = commandStub("sback", Constants.Permissions.BACK_OTHERS)
    .withArguments(PlayerArgument("player"))
    .executesPlayer(PlayerCommandExecutor { player, args ->
        if (sback(args[0] as Player)) { return@PlayerCommandExecutor }
        player.sendMessage(locale.translate(Constants.LanguagePaths.SBACK_OTHER_NON_EXISTENT_LOCATION, Variables()))
    })

private fun sback(sender: Player): Boolean {
    // TODO: ideally getting particular bits should be in postgres handler not like this!
    val to = StickyCommands.plugin.postgresHandler.getUserLocation(sender.uniqueId) ?: return false
    sender.teleport(to)
    return true
}
