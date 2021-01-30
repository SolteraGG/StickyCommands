/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.api.economy;

import lombok.Getter;
import org.bukkit.Material;

public class Listing {
    @Getter
    private Material material;

    @Getter
    private Double price;

    public Listing(Material material, Double price) {}
}
