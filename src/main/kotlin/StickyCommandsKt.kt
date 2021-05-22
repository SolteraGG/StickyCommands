package com.dumbdogdiner.stickycommands

interface StickyCommandsKt {
    val plugin : StickyCommands
        get() = StickyCommands.getInstance()

    val logger
        get() = this.plugin.logger

    val config
        get() = this.plugin.config
    val databaseHandler
        get() = StickyCommands.getDatabaseHandler()//this.plugin.databaseHandler
}
