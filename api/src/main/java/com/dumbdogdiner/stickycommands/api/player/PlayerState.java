package com.dumbdogdiner.stickycommands.api.player;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * An interface representing an implementation of a player's plugin state.
 */
public interface PlayerState {
	/**
	 * Get the player this state manager is for.
	 * @return {@link Player}
	 */
	@NotNull
	public Player getPlayer();

	/**
	 * Get the AFK state of this player.
	 * @return a {@link Boolean} determining if this player is afk
 */
	@NotNull
	public Boolean isAfk();

	/**
	 * Set the AFK state of this player.
	 * @param isAfk Whether this player is AFK
	 */
	public void setAfk(@NotNull Boolean isAfk);

	/**
	 * Get the state of this player's fly mode.
	 * @return a {@link Boolean} determining if the mode is enabled
	 */
	@NotNull
	public Boolean hasFlyModeEnabled();

	/**
	 * Set the fly state of this player.
	 * @param flyEnabled A boolean determining if this player has fly mode enabled.
	 */
	public void setFlyModeEnabled(@NotNull Boolean flyEnabled);

	/**
	 * Get the speed of the target type for this player.
	 * @param type The target speed type
	 * @return a {@link Float} determining the fly speed of this player in blocks per second.
	 */
	@NotNull
	public Float getSpeed(@NotNull SpeedType type);

	/**
	 * Set the fly or walking speed of this player
	 * @param type The target speed type
	 */
	public void setSpeed(@NotNull SpeedType type, @NotNull Float speed);

	/**
	 * Get the walk speed for this player.
	 * @return a {@link Float} determining the walk speed of this player in blocks per second.
	 */
	@NotNull
	public default Float getWalkSpeed() {
		return this.getSpeed(SpeedType.WALK);
	}

	/**
	 * Get the fly speed for this player.
	 * @return a {@link Float} determining the fly speed of this player in blocks per second.
	 */
	@NotNull
	public default Float getFlySpeed() {
		return this.getSpeed(SpeedType.FLY);
	}

}
