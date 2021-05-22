/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.database

import com.dumbdogdiner.stickycommands.StickyCommands
import com.dumbdogdiner.stickycommands.StickyCommandsKt
import org.jetbrains.exposed.sql.SqlLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.statements.StatementContext
import org.jetbrains.exposed.sql.statements.expandArgs

/**
 * SQL Logger for Exposed.
 */
class ExposedLogger : SqlLogger, StickyCommandsKt {
    override fun log(context: StatementContext, transaction: Transaction) {
        logger.fine(context.expandArgs(transaction))
    }
}
