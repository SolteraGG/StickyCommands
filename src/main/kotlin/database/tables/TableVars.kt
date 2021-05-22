package com.dumbdogdiner.stickycommands.database.tables

import com.dumbdogdiner.stickycommands.StickyCommands
import com.dumbdogdiner.stickycommands.utils.Constants

internal val server = StickyCommands.getInstance().config.getString(Constants.SettingsPaths.SERVER, "unknown")!!
internal val prefix = StickyCommands.getInstance().config.getString(Constants.SettingsPaths.DATABASE_TABLE_PREFIX, "stickycommands_")!!