package com.dumbdogdiner.stickycommands.api.economy;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

import java.util.Date;

/**
 * A transaction.
 */
public interface Transaction {
	/**
	 * Get the ID of this transaction.
	 * @return The {@link Integer} ID of this transaction
	 */
	public Integer getId();

	/**
	 * Get the player who performed this transaction.
	 * @return An {@link OfflinePlayer} object
	 */
	public OfflinePlayer getPlayer();

	/**
	 * Get the type of item involved in this transaction.
	 * @return A {@link Material} value
	 */
	public Material getItemType();

	/**
	 * Get the number of items sold in this transaction.
	 * @return {@link Integer}
	 */
	public Integer getItemQuantity();

	/**
	 * Get the sale price of the items when this transaction was made.
	 * @return {@link Integer}
	 */
	public Integer getSalePrice();

	/**
	 * Get the new balance of the player after this transaction was performed.
	 * @return {@link Double}
	 */
	public Double getNewBalance();

	/**
	 * Get the previous balance of the player before this transaction was performed.
	 * @return {@link Double}
	 */
	public default Double getPreviousBalance() {
		return this.getNewBalance() - this.getItemQuantity() * this.getSalePrice();
	}

	/**
	 * Get the date on which this transaction was performed.
	 * @return A {@link Date} object
	 */
	public Date getDate();
}
