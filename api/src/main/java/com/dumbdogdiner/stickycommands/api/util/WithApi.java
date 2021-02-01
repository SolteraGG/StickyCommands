/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.api.util;

import com.dumbdogdiner.stickycommands.api.StickyCommands;
import com.dumbdogdiner.stickycommands.api.managers.PlayerStateManager;
import org.jetbrains.annotations.NotNull;

public interface WithApi {
    default @NotNull StickyCommands getStickyCommands() {
        return StickyCommands.getService();
    }

    default @NotNull PlayerStateManager getPlayerStateManager() {
        return this.getStickyCommands().getPlayerStateManager();
    }
}
