/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.tasks

import java.util.TimerTask

/**
 * A task that can be scheduled for one-time or repeated execution by a Timer.
 * <p>
 * A timer task is not reusable. Once a task has been scheduled for execution on a Timer or cancelled, subsequent attempts to schedule it for execution will throw IllegalStateException.
 * <p>
 * Extended for containing the `delay` and `period` of the task
 */
abstract class StickyTask(val delay: Long, val period: Long) : TimerTask()
