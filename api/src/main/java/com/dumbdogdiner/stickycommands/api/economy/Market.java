/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.api.economy;

import java.util.List;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public interface Market {
    @Getter
    public final int PAGE_MAX = 10;

    public Integer latestId();

    /**
     * Get the number of listings on the market
     * @return The number of listings as a {@link Long}
     */
    @NotNull
    public Long getListingCount();

    /**
     * Get all current listings
     * @return {@link List<Listing>} of all listings
     */
    @NotNull
    public List<Listing> getListings(
        @NotNull Listing.SortBy sortBy,
        @NotNull Integer page,
        @NotNull Integer pageSize
    );

    /**
     * Get a list of listings
     * @param sortBy
     * @return {@link List<Listing>} of listings
     */
    @NotNull
    default List<Listing> getListings(@NotNull Listing.SortBy sortBy) {
        return getListings(sortBy, 1, PAGE_MAX);
    }

    /**
     * Get a list of listings
     * @return {@link List<Listing>} of listings
     */
    @NotNull
    default List<Listing> getListings() {
        return getListings(Listing.SortBy.DATE_ASCENDING, 1, PAGE_MAX);
    }

    /**
     * Get a list of listings of a specific material
     * @param material of the listing
     * @param sortBy
     * @param page to get
     * @param pageSize per page
     * @return {@link List<Listing>} of listings
     */
    @NotNull
    public List<Listing> getListingsOfType(
        @NotNull Material material,
        @NotNull Listing.SortBy sortBy,
        @NotNull Integer page,
        @NotNull Integer pageSize
    );

    /**
     * Get a list of listings of a specific material
     * @param material of the listing
     * @return {@link List<Listing>} of listings
     */
    @NotNull
    default List<Listing> getListingsOfType(
        @NotNull Material material,
        @NotNull Integer page
    ) {
        return getListingsOfType(
            material,
            Listing.SortBy.DATE_ASCENDING,
            page,
            PAGE_MAX
        );
    }

    /**
     * Get a list of listings of a specific material
     * @param material of the listing
     * @return {@link List<Listing>} of listings
     */
    @NotNull
    default List<Listing> getListingsOfType(@NotNull Material material) {
        return getListingsOfType(
            material,
            Listing.SortBy.DATE_ASCENDING,
            1,
            PAGE_MAX
        );
    }

    /**
     * Get the listings that a player has listed
     * @param player to get listings of
     * @param sortBy
     * @param page to get
     * @param pageSize per page
     * @return {@link List<Listing>} of listings belonging to a player
     */
    public List<Listing> getListingsOfPlayer(
        @NotNull OfflinePlayer player,
        @NotNull Listing.SortBy sortBy,
        @NotNull Integer page,
        @NotNull Integer pageSize
    );

    /**
     * Get the listings that a player has listed
     * @param player to get listings of
     * @param page to get
     * @return {@link List<Listing>} of listings belonging to a player
     */
    @NotNull
    default List<Listing> getListingsOfOfflinePlayer(
        @NotNull OfflinePlayer player,
        @NotNull Integer page
    ) {
        return getListingsOfPlayer(
            player,
            Listing.SortBy.DATE_ASCENDING,
            page,
            PAGE_MAX
        );
    }

    /**
     * Get the listings that a player has listed
     * @param player to get listings of
     * @return {@link List<Listing>} of listings belonging to a player
     */
    @NotNull
    default List<Listing> getListingsOfOfflinePlayer(
        @NotNull OfflinePlayer player
    ) {
        return getListingsOfPlayer(
            player,
            Listing.SortBy.DATE_ASCENDING,
            1,
            PAGE_MAX
        );
    }

    /**
     * Add a listing to the market
     * @param listing to list
     */
    public void add(@NotNull Listing listing);

    /**
     * Remove a listing from the market
     */
    public void remove(@NotNull Listing listing);
}
