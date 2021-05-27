/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.database

import org.jetbrains.exposed.sql.SqlLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.statements.StatementContext
import org.jetbrains.exposed.sql.statements.expandArgs
import java.util.logging.Logger

/**
 * SQL Logger for Exposed.
 */
class ExposedLogger (val logger : Logger): SqlLogger {
    override fun log(context: StatementContext, transaction: Transaction) {
        logger.fine(context.expandArgs(transaction))
    }
}
