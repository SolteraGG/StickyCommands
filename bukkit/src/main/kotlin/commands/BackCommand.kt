package com.dumbdogdiner.stickycommands.commands

import com.dumbdogdiner.stickyapi.bukkit.util.SoundUtil
import com.dumbdogdiner.stickycommands.StickyCommands
import com.dumbdogdiner.stickycommands.util.Constants
import dev.jorel.commandapi.executors.PlayerCommandExecutor

val backCommand = commandStub("", Constants.Permissions.BACK)
    .executesPlayer(PlayerCommandExecutor { player, _ ->

        SoundUtil.sendSuccess(player)
    })
