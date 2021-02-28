/*
 * Copyright (c) 2020-2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.api;

import com.dumbdogdiner.stickycommands.api.economy.Market;
import com.dumbdogdiner.stickycommands.api.managers.PlayerStateManager;
import com.dumbdogdiner.stickycommands.api.managers.PowertoolManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a generic implementation of the StickyCommandsAPI.
 */
public interface StickyCommands {
    /**
     * Get the running version of the API.
     *
     * @return {@link String}
     */
    static String getVersion() {
        return StickyCommands.class.getPackage().getImplementationVersion();
    }

    /**
     * Register the API service.
     *
     * @param plugin  The plugin registering the service
     * @param service The plugin's implementation of the service
     */
    static void registerService(JavaPlugin plugin, StickyCommands service) {
        Bukkit
            .getServicesManager()
            .register(
                StickyCommands.class,
                service,
                plugin,
                ServicePriority.Lowest
            );
    }

    /**
     * Fetch the instantiated chat service object.
     *
     * @return {@link StickyCommands}
     */
    @NotNull
    static StickyCommands getService() {
        var provider = Bukkit
            .getServicesManager()
            .getRegistration(StickyCommands.class);
        if (provider == null) {
            throw new RuntimeException(
                "Cannot access API service - has not been registered!"
            );
        }
        return provider.getProvider();
    }

    /**
     * Return a reference to the plugin providing the StickyCommandsAPI implementation.
     *
     * @return {@link Plugin}
     */
    Plugin getProvider();

    /**
     * Get the player state manager instance
     */
    PlayerStateManager getPlayerStateManager();

    /**
     * Get the powertool manager instance
     */
    PowertoolManager getPowertoolManager();

    /**
     * Get the Market instance
     */
    Market getMarket();
}
