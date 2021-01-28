package com.dumbdogdiner.stickycommands.api.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

/**
 * A powertool owned by a particular player.
 */
public interface Powertool {
	/**
	 * Get the player who owns this powertool.
	 * @return The {@link Player} who owns this powertool
	 */
	@NotNull
	public Player getPlayer();

	/**
	 * Get the itemstack this powertool uses.
	 * @return The {@link ItemStack} this powertool uses
	 */
	@NotNull
	public ItemStack getItemStack();

	/**
	 * Get the powertool's item meta.
	 * @return The {@link ItemMeta} of this powertool
	 */
	@NotNull
	public ItemMeta getItemMeta();

	/**
	 * Set the powertool's item meta.
	 * @param meta The new item meta
	 */
	public void setItemMeta(@NotNull ItemMeta meta);

	/**
	 * Update the powertool's item meta.
	 * @param updater The updater function to apply
	 */
	public default void updateItemMeta(@NotNull Function<ItemMeta, ItemMeta> updater) {
		this.setItemMeta(updater.apply(this.getItemMeta()));
	}

	/**
	 * Get the description of this powertool, if it exists.
	 * @return A {@link String} containing the item's description
	 */
	@Nullable
	public default String getDescription() {
		if (this.getItemMeta().getLore() == null || this.getItemMeta().getLore().isEmpty()) {
			return null;
		}
		return this.getItemMeta().getLore().get(0);
	}

	/**
	 * Update the description of this powertool.
	 * @param description The new description
	 */
	public default void setDescription(@NotNull String description) {
		this.updateItemMeta((meta) -> {
			meta.setLore(List.of(description));
			return meta;
		});
	}

	/**
	 * Get the command this powertool executes.
	 * @return A {@String} with the executed command
	 */
	@NotNull
	public String getCommand();

	/**
	 * Set the command this powertool executes.
	 * @param command The command to execute
	 */
	public void setCommand(@NotNull String command);

	/**
	 * Get whether this powertool is enabled.
	 * @return A {@link Boolean} determining if this powertool is enabled
	 */
	@NotNull
	public Boolean isEnabled();

	/**
	 * Set the enable state of this powertool.
	 * @param enabled Whether this powertool is enabled.
	 */
	public void setEnabled(@NotNull Boolean enabled);
}
