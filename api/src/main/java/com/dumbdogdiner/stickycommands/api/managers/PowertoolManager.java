/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.api.managers;

import com.dumbdogdiner.stickycommands.api.item.Powertool;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An interface representing an implementation of the powertool manager
 */
public interface PowertoolManager {
    /**
     * Get a powertool from the active powertools
     * @param player that owns the powertool
     * @param type of material
     * @return a {@link Powertool} belonging to a player
     */
    @Nullable
    public Powertool getPowerTool(
        @NotNull Player player,
        @NotNull Material type
    );

    /**
     * Get the set of existing powertools
     * @return a {@link Set<Powertool>} of powertools
     */
    @NotNull
    public Set<Powertool> getPowertools();

    /**
     * Add a powertool to the set of powertools
     * @param powertool
     */
    public void add(@NotNull Powertool powertool);

    /**
     * Remove a powertool from the set of powertools
     * @param powertool to remove
     */
    public void remove(@NotNull Powertool powertool);

    /**
     * Remove all powertools beloinging to a player from the set of powertools
     * @param player with powertools to remove
     */
    public void remove(@NotNull Player player);
}
