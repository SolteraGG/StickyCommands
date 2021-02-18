/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.tasks

import java.util.TimerTask


abstract class StickyTask(val delay: Long, val period: Long) : TimerTask()
