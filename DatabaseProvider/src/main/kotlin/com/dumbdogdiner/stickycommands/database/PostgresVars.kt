package com.dumbdogdiner.stickycommands.database

import org.bukkit.configuration.file.FileConfiguration
import java.util.logging.Logger

data class PostgresVars(val config : FileConfiguration,val logger : Logger)
