package com.dumbdogdiner.stickycommands.database.tables

// i hate this but its the least nasty way to do it for now :(
// top level definitions suck ass, so lets limit their reach
lateinit var server : String //= StickyCommands.getInstance().config.getString(Constants.SettingsPaths.SERVER, "unknown")!!
lateinit var prefix : String //= StickyCommands.getInstance().config.getString(Constants.SettingsPaths.DATABASE_TABLE_PREFIX, "stickycommands_")!!