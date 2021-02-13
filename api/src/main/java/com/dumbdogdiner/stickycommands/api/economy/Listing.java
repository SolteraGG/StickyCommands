/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.api.economy;

import com.dumbdogdiner.stickycommands.api.StickyCommands;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
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

    @Getter
    @NotNull
    public Date listedAt;

    @Getter
    @Setter
    @Nullable
    public Date purchasedAt;

    /**
     * Create a new listing
     * @param id of the listing, if null it will be auto generated
     * @param player that listed this item
     * @param material to list
     * @param price to list at
     * @param quantity of items to be listed
     * @param buyer of this listing
     * @param listedAt time of listing
     */
    public Listing(
        @Nullable Integer id,
        @NotNull OfflinePlayer player,
        @NotNull Material material,
        @NotNull Double price,
        @NotNull Integer quantity,
        @Nullable OfflinePlayer buyer,
        @NotNull Date listedAt
    ) {
        this.seller = player;
        this.material = material;
        this.price =
            (double) Math.round((price * quantity) * (long) Math.pow(10, 2)) /
            (long) Math.pow(10, 2); // Prevents dumb computer from printing dumb 9.9999999999999999999999999991
        this.quantity = quantity;
        this.buyer = buyer;
        this.id =
            Objects.requireNonNullElseGet(id, () -> market.latestId() + 1);
        this.listedAt = listedAt;
    }

    /**
     * Create a new listing
     * @param seller that listed this item
     * @param material to list
     * @param price to list at
     * @param quantity of items to be listed
     */
    public Listing(
        @NotNull OfflinePlayer seller,
        @NotNull Material material,
        @NotNull Double price,
        @NotNull Integer quantity
    ) {
        this(
            null,
            seller,
            material,
            price,
            quantity,
            null,
            Date.from(Instant.now())
        );
    }

    /**
     * List this listing on the market
     */
    public void list() {
        market.add(this);
    }
}
