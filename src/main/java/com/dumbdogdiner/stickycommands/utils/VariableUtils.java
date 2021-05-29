package com.dumbdogdiner.stickycommands.utils;

import com.dumbdogdiner.stickyapi.common.util.MathUtil;
import com.dumbdogdiner.stickyapi.common.util.StringUtil;
import com.dumbdogdiner.stickyapi.common.util.TimeUtil;
import com.dumbdogdiner.stickycommands.database.PostgresHandler;
import com.dumbdogdiner.stickycommands.objects.Listing;
import com.dumbdogdiner.stickycommands.StickyCommands;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.util.Map;

public class VariableUtils {
    /**
     * Get placeholders for a player object
     */
    public static void withPlayer(Map<String, String> variables, Player target, boolean isTarget) {
        var prefix = isTarget ? "target" : "player";
        var targetLocation = target.getLocation();
        withOfflinePlayer(variables, target, isTarget);
        variables.put(prefix, target.getName());
        variables.put(prefix + "_uuid", target.getUniqueId().toString());
        variables.put(prefix + "_exp", String.valueOf(target.getExp()));
        variables.put(prefix + "_level", String.valueOf(target.getLevel()));
        variables.put(prefix + "_all_exp", String.valueOf(target.getTotalExperience()));
        variables.put(prefix + "_world", target.getWorld().getName());
        variables.put(prefix + "_health", String.valueOf(target.getHealth()));
        variables.put(prefix + "_hunger", String.valueOf(target.getFoodLevel()));
        variables.put(prefix + "_saturation", String.valueOf(target.getSaturation()));
        variables.put(prefix + "_exhaustion", String.valueOf(target.getExhaustion()));
        variables.put(prefix + "_location", targetLocation.getX() + ", " + targetLocation.getY() + ", " + targetLocation.getZ());
        variables.put(prefix + "_location_x", String.valueOf(target.getLocation().getX()));
        variables.put(prefix + "_location_y", String.valueOf(target.getLocation().getY()));
        variables.put(prefix + "_location_z", String.valueOf(target.getLocation().getZ()));
        variables.put(prefix + "_fly_speed", String.valueOf(target.getFlySpeed()));
        variables.put(prefix + "_walk_speed", String.valueOf(target.getWalkSpeed()));
        variables.put(prefix + "_flying", String.valueOf(target.isFlying()));
        variables.put(prefix + "_speed", String.valueOf(target.isFlying() ? target.getFlySpeed() : target.getWalkSpeed()));
        variables.put(prefix + "_allow_flight", String.valueOf(target.getAllowFlight()));
    }

    /**
     * Get placeholders for an player object
     */
    public static void withOfflinePlayer(Map<String, String> variables, OfflinePlayer target, boolean isTarget) {
        var prefix = isTarget ? "target" : "player";
        var name = target.getName();
        if (name == null) {
            name = "unknown";
        }
        variables.put(prefix, name);
        variables.put(prefix + "_uuid", target.getUniqueId().toString());
        variables.putAll(StickyCommands.getDatabaseHandler().getUserInfo(target.getUniqueId(), isTarget));
    }

    /**
     * Get placeholders for a listing object
     */
    public static void withListing(Map<String, String> variables, Listing listing) {
        variables.put("worth", String.valueOf(listing.getPrice()));
        variables.put("amount", String.valueOf(listing.getQuantity()));
        variables.put("item", StringUtil.capitaliseSentence(listing.getMaterial().toString().replace("_", " ")));
        variables.put("item_enum", listing.getMaterial().toString());
        variables.put("date", String.valueOf(listing.getListedAt().getTime()));
        variables.put("log_player", String.valueOf(listing.getSeller().getName()));
        variables.put("saleid", String.valueOf(listing.getId()));
        variables.put("amount", String.valueOf(listing.getQuantity()));
        variables.put("price", String.valueOf(listing.getPrice()));
        variables.put("short_date", TimeUtil.significantDurationString(System.currentTimeMillis() - listing.getListedAt().getTime() / 1000L)); // dumb but whatever
        variables.put("date_duration", TimeUtil.expirationTime(System.currentTimeMillis() - listing.getListedAt().getTime() / 1000L));
    }

    /**
     * Get placeholders for a listing object
     */
    public static void withListing(Map<String, String> variables, Listing listing, PlayerInventory inventory) {
        variables.put("single_worth", String.valueOf(listing.getPrice() / listing.getQuantity()));
        variables.put("hand_worth", String.valueOf((listing.getPrice() / listing.getQuantity()) * inventory.getItemInMainHand().getAmount()));
        variables.put("inventory_worth", String.valueOf((listing.getPrice() / listing.getQuantity()) * InventoryUtil.count(inventory, listing.getMaterial())));
        withListing(variables, listing);
    }

    /**
     * Get placeholders for a location object
     */
    public static void withLocation(Map<String, String> variables, Location location) {
        double x = MathUtil.round(location.getX(), 2);
        double y = MathUtil.round(location.getY(), 2);
        double z = MathUtil.round(location.getZ(), 2);
        variables.put("location", x + ", " + y + ", " + z);
        variables.put("location_x", String.valueOf(x));
        variables.put("location_y", String.valueOf(y));
        variables.put("location_z", String.valueOf(z));
        variables.put("world", location.getWorld().getName());
        variables.put("pitch", String.valueOf(location.getPitch()));
        variables.put("yaw", String.valueOf(location.getYaw()));
    }

    public static void withSender(Map<String, String> variables, CommandSender sender) {
        variables.put("name", sender.getName());
    }
}
