/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.api.economy;

import com.dumbdogdiner.stickycommands.api.StickyCommands;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Listing {
    private final Market market = StickyCommands.getService().getMarket();

    public enum SortBy {
        PRICE_ASCENDING,
        PRICE_DESCENDING,
        DATE_ASCENDING,
        DATE_DESCENDING,
        QUANTITY,
        ITEM,
    }

    @Getter
    @NotNull
    public OfflinePlayer seller;

    @Getter
    @NotNull
    public Material material;

    @Getter
    @NotNull
    public Double price;

    @Getter
    @NotNull
    public Integer quantity;

    @Getter
    @Setter
    @Nullable
    public OfflinePlayer buyer;

    @Getter
    @NotNull
    public Integer id;

    public Listing(
        @NotNull OfflinePlayer player,
        @NotNull Material material,
        @NotNull Double price,
        @NotNull Integer quantity,
        @Nullable OfflinePlayer buyer
    ) {
        this.seller = player;
        this.material = material;
        this.price = price * quantity;
        this.quantity = quantity;
        this.buyer = buyer;
        this.id = market.latestId()+1;
    }

    /**
     * List this listing on the market
     */
    public void list() {
        market.add(this);
    }
}
