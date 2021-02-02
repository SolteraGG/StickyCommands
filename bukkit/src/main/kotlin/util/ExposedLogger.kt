/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.util

import org.jetbrains.exposed.sql.SqlLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.statements.StatementContext
import org.jetbrains.exposed.sql.statements.expandArgs

/**
 * SQL Logger for Exposed.
 */
class ExposedLogger : WithPlugin, SqlLogger {
    override fun log(context: StatementContext, transaction: Transaction) {
        this.logger.fine(context.expandArgs(transaction))
    }
}
