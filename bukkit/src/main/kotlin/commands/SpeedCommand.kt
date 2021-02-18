/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.commands

import com.dumbdogdiner.stickyapi.common.arguments.Arguments
import com.dumbdogdiner.stickyapi.common.command.ExitCode
import com.dumbdogdiner.stickycommands.StickyCommands
import com.dumbdogdiner.stickycommands.util.Constants
import com.dumbdogdiner.stickycommands.util.Variables
import java.util.HashMap
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

val speedCommand = commandStub("speed", Constants.Descriptions.SPEED, Constants.Permissions.SPEED)
    .onExecute { sender: CommandSender, args: Arguments, vars: HashMap<String, String> ->
        args.requiredString("speed")
        sender as Player
        vars.putAll(Variables().withPlayer(sender, false).get())
        val flying = sender.isFlying

        var speed: Float
        if (!args.exists("speed")) {
            speed = if (flying) Constants.DEFAULT_FLYING_SPEED else Constants.DEFAULT_WALKING_SPEED
        }

        if (!args.getString("speed").matches("""\d*\.?\d+""".toRegex())) {
            return@onExecute ExitCode.EXIT_INVALID_SYNTAX
        } else {
            speed = args.getString("speed").toFloat()
            speed /= if (speed > 10 || speed <= 0) return@onExecute ExitCode.EXIT_INVALID_SYNTAX else 10f
        }

        val state = StickyCommands.plugin.playerStateManager.getPlayerState(sender)
        state.setSpeed(speed)

        vars["speed"] = if (args.exists("speed")) args.getString("speed") else "1"
        vars["speed_type"] = if (sender.isFlying) "fly" else "walk"
        sender.sendMessage(locale.translate(Constants.LanguagePaths.SPEED_MESSAGE, vars))
        ExitCode.EXIT_SUCCESS
    }
    .onTabComplete { _, _, _ ->
        // I wanted to use intrange but I couldn't think of a nice way to make them all strings
        // so we'll go with this ig... If someone knows a *pretty* way of doing what I want
        // please show me - zachery
        return@onTabComplete listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
    }
