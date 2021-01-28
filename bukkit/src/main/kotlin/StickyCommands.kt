/*
 * Copyright (c) 2020 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.myawesomeplugin

import com.dumbdogdiner.stickycommands.api.StickyCommandsAPI
import kr.entree.spigradle.annotations.PluginMain
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

@PluginMain
class StickyCommands : JavaPlugin(), StickyCommandsAPI {
    override fun getProvider(): Plugin {
        return this
    }
}
