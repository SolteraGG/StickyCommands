package com.dumbdogdiner.stickycommands.commands

import com.dumbdogdiner.stickyapi.bukkit.util.SoundUtil
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player

val boopCommand = commandStub("boop", "stickycommands.boop")
    .withArguments(PlayerArgument("player"))
    .executesPlayer(PlayerCommandExecutor { player, args ->
        val target = args[0] as Player
        val message = "${ChatColor.LIGHT_PURPLE}${ChatColor.ITALIC}${player.name}: *boops ${target.name}* ${ChatColor.RED}❤"
        target.sendMessage(message)
        player.sendMessage(message)
        target.playSound(target.location, Sound.ENTITY_FOX_AMBIENT, 1f, 1f)
        SoundUtil.sendSuccess(player)
    })
