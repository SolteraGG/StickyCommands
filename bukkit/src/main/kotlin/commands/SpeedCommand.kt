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

val speedCommand =
        commandStub("speed", Constants.Descriptions.SPEED, Constants.Permissions.SPEED)
                .requiresPlayer()
                .onExecute { sender: CommandSender, args: Arguments, vars: HashMap<String, String>
                    ->
                    vars["syntax"] = ("/speed [1-10]")
                    args.requiredString("speed")
                    sender as Player
                    vars.putAll(Variables().withPlayer(sender, false).get())
                    val flying = sender.isFlying

                    var speed: Float =
                            if (!args.exists("speed")) 1f
                            else if (!args.getString("speed").matches("""\d*\.?\d+""".toRegex()))
                                    return@onExecute ExitCode.EXIT_INVALID_SYNTAX
                            else args.getString("speed").toFloat()
                    speed /=
                            if (speed > 10 || speed <= 0)
                                    return@onExecute ExitCode.EXIT_INVALID_SYNTAX
                            else 10f

                    val state = StickyCommands.plugin.playerStateManager.getPlayerState(sender)
                    state.setSpeed(speed)

                    vars["player_speed"] =
                            if (args.exists("speed")) args.getString("speed") else "1"
                    vars["player_flying"] = sender.isFlying.toString()
                    sender.sendMessage(
                            locale.translate(Constants.LanguagePaths.SPEED_MESSAGE, vars))
                    ExitCode.EXIT_SUCCESS
                }
                .onTabComplete { _, _, args ->
                    return@onTabComplete (1..10).map(Int::toString).filter {
                        it.startsWith(args.rawArgs[0], true)
                    }
                }

    /*
       KEY = "hello"
       VALIE = "world"
    */
