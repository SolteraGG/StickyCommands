/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.commands

import com.dumbdogdiner.stickyapi.bukkit.util.SoundUtil
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import java.util.Base64
import kotlin.random.Random
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

val boopCommand = CommandAPICommand("boop")
    .withArguments(PlayerArgument("player"))
    .executesPlayer(PlayerCommandExecutor { player, args ->
        val isSkye = player.uniqueId.toString() == "ec294b17-377d-4bc5-80ee-fa0c56de77b9"
        val target = args[0] as Player
        val message = "${ChatColor.LIGHT_PURPLE}${ChatColor.ITALIC}${player.name}: *${if (isSkye && target.uniqueId.toString() == "55c83658-6d34-44e0-8294-7412b10e1de1") "loves" else "boops"} ${target.name}* ${ChatColor.RED}❤"
        target.sendMessage(message)
        player.sendMessage(message)
        target.playSound(target.location, Sound.ENTITY_FOX_AMBIENT, 1f, 1f)
        SoundUtil.sendSuccess(player)
        // <3
        if (isSkye && Random.nextDouble(0.0, 1.0) <= 0.01) {
            val entity = player.world.spawnEntity(target.location, EntityType.FOX)
            entity.customName = ChatColor.translateAlternateColorCodes('&', String(Base64.getDecoder().decode("aWx5IHNreWU=")))
            SoundUtil.queueSound(target, Sound.ENTITY_FOX_SLEEP, 1f, 1f, 0L)
            SoundUtil.queueSound(target, Sound.ENTITY_FOX_SNIFF, 1f, 1f, 500L)
        }
    })
