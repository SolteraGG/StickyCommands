package com.dumbdogdiner.stickycommands.objects;/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

// I realize passing in a market is a little clunky but I cannot think of a better solution for now.
public class Listing {
    private final Market market;

    public enum SortBy {
        PRICE_ASCENDING,
        PRICE_DESCENDING,
        DATE_ASCENDING,
        DATE_DESCENDING,
        QUANTITY,
        ITEM
    }

    @Getter @NotNull
    public OfflinePlayer seller;

    @Getter @NotNull
    public Material material;

    @Getter
    public double price;

    @Getter
    public int quantity;

    @Nullable @Getter @Setter
    public OfflinePlayer buyer;

    @Getter
    public int id;

    @Getter
    @NotNull
    public Date listedAt;

    @Getter
    @Setter
    @Nullable
    public Date purchasedAt;

    /**
     * Create a new listing
     *
     * @param market   the market we care about
     * @param id       of the listing, if null it will be auto generated
     * @param player   that listed this item
     * @param material to list
     * @param price    to list at
     * @param quantity of items to be listed
     * @param buyer    of this listing
     * @param listedAt time of listing
     */
    public Listing(@NotNull Market market, int id, @NotNull OfflinePlayer player, @NotNull Material material, double price, int quantity, @Nullable OfflinePlayer buyer, @NotNull Date listedAt) {
        this(market, player, material, price, quantity);

        this.buyer = buyer;
        this.id =
                Objects.requireNonNullElseGet(id, () -> market.latestId() + 1);
        this.listedAt = listedAt;
    }

    /**
     * Create a new listing
     *
     * @param seller   that listed this item
     * @param material to list
     * @param price    to list at
     * @param quantity of items to be listed
     */
    public Listing(@NotNull Market market, @NotNull OfflinePlayer seller, @NotNull Material material, double price, int quantity) {
        this.seller = seller;
        this.material = material;
        this.market = market;


        this.price = BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP).doubleValue();
        /* The old way because eww
        // FIXME SURELY there is a better way to do this, like, for real. Come on, *zachy*
        this.price = (double) Math.round((price * quantity) * (long) Math.pow(10, 2)) /
                        (long) Math.pow(10, 2); // Prevents dumb computer from printing dumb 9.9999999999999999999999999991

         */
        this.quantity = quantity;
        this.listedAt = Date.from(Instant.now());
    }

    /**
     * List this listing on the market
     */
    public void list() {
        market.add(this);
    }
}
