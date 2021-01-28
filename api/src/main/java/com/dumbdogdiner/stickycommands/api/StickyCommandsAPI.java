/*
 * Copyright (c) 2020 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.api;

import com.dumbdogdiner.stickycommands.api.player.PlayerState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a generic implementation of the StickyCommandsAPI.
 */
public interface StickyCommandsAPI {
  /**
   * Register the API service.
   *
   * @param plugin  The plugin registering the service
   * @param service The plugin's implementation of the service
   */
  static void registerService(JavaPlugin plugin, StickyCommandsAPI service) {
    Bukkit
      .getServicesManager()
      .register(StickyCommandsAPI.class, service, plugin, ServicePriority.Lowest);
  }


  /**
   * Fetch the instantiated chat service object.
   *
   * @return {@link StickyCommandsAPI}
   */
  @NotNull
  static StickyCommandsAPI getService() {
    var provider = Bukkit
            .getServicesManager()
            .getRegistration(StickyCommandsAPI.class);
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
   * Get the player state for the target player.
   * @param target The target player
   * @return a {@link PlayerState} object for this player
   */
  PlayerState getState(Player target);
}
