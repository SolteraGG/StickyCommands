package com.dumbdogdiner.stickycommands.api.player;

import org.bukkit.entity.Player;

public interface PlayerStateManager {

	/**
	 * Get a player's current {@link PlayerState}
	 * @return {@link PlayerState}
	 */
	public PlayerState getPlayerState(Player player);

	/**
	 * Get the Afk state of a {@link Player}
	 * @return {@link Boolean}
	 */
	public default Boolean isPlayerAfk(Player player) {
		return getPlayerState(player).isAfk();
	}
}
