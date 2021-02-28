/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.api.item;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
     * @return The {@link Material} this powertool uses
     */
    @NotNull
    public Material getMaterial();

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

    /**
     * Execute the powertool's command
     */
    public void execute();
}
