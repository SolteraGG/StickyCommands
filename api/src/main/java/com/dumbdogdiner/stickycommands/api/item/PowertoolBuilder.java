package com.dumbdogdiner.stickycommands.api.item;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class for the creation of player power tools.
 */
public abstract class PowertoolBuilder implements Cloneable {
	private ItemStack item;

	/**
	 * Create a powertool using the given item stack.
	 * @param item The target item stack
	 */
	public PowertoolBuilder(ItemStack item) {
		this.item = item;
	}

	/**
	 * Create a powertool using the given material.
	 * @param material The target material
	 */
	public PowertoolBuilder(Material material) {
		this(new ItemStack(material));
	}

	/**
	 * Set the command of the powertool.
	 * @param command The command to run
	 * @return The {@link PowertoolBuilder}
	 */
	public abstract PowertoolBuilder setCommand(@NotNull String command);

	/**
	 * Give the target player the constructed powertool.
	 * @param target The target player
	 * @return A {@link Powertool} object
	 */
	@NotNull
	public abstract Powertool give(@NotNull Player target);
}
