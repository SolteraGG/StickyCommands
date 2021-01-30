/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.api.player;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface PlayerStateManager {
    /**
     * Get a player's current {@link PlayerState}
     * @return {@link PlayerState}
     */
    @NotNull
    public PlayerState getPlayerState(@NotNull Player player);

    /**
     * Get the Afk state of a {@link Player}
     * @return {@link Boolean}
     */
    @NotNull
    default Boolean isPlayerAfk(@NotNull Player player) {
        return getPlayerState(player).isAfk();
    }

    /**
     * Checks if a given player is hidden, vanished, staffvanished, or fakeleaved
     * @return Whether the user is hidden.
     */
    @NotNull
    default Boolean isPlayerHidden(@NotNull Player player) {
        return getPlayerState(player).isHidden();
    }
}
