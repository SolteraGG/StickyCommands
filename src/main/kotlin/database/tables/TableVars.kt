package com.dumbdogdiner.stickycommands.database.tables

import com.dumbdogdiner.stickycommands.StickyCommands
import com.dumbdogdiner.stickycommands.utils.Constants

// i hate this but its the least nasty way to do it for now :(
// top level definitions suck ass, so lets limit their reach
internal val server = StickyCommands.getInstance().config.getString(Constants.SettingsPaths.SERVER, "unknown")!!
internal val prefix = StickyCommands.getInstance().config.getString(Constants.SettingsPaths.DATABASE_TABLE_PREFIX, "stickycommands_")!!