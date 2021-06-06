package com.dumbdogdiner.stickycommands.commands;

import com.dumbdogdiner.stickyapi.bukkit.util.SoundUtil;

import com.dumbdogdiner.stickyapi.common.chat.ChatMessage;
import com.dumbdogdiner.stickyapi.common.translation.LocaleProvider;
import com.dumbdogdiner.stickyapi.common.util.StringUtil;
import com.dumbdogdiner.stickycommands.StickyCommands;
import com.dumbdogdiner.stickycommands.objects.Listing;
import com.dumbdogdiner.stickycommands.objects.Market;
import com.dumbdogdiner.stickycommands.utils.Constants;
import com.dumbdogdiner.stickycommands.utils.InventoryUtil;
import com.dumbdogdiner.stickycommands.utils.ItemWorths;
import com.dumbdogdiner.stickycommands.utils.VariableUtils;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import dev.jorel.commandapi.annotations.Permission;
import dev.jorel.commandapi.annotations.Subcommand;
import dev.jorel.commandapi.annotations.arguments.AIntegerArgument;
import dev.jorel.commandapi.annotations.arguments.ALiteralArgument;
import dev.jorel.commandapi.annotations.arguments.APlayerArgument;
import org.bukkit.entity.Player;

import java.util.List;

@Command("sell")
public class SellCommand {
    static StickyCommands instance = StickyCommands.getInstance();
    static Market market = StickyCommands.getMarket();
    static LocaleProvider locale = instance.getLocaleProvider();

    @Default
    public static void sell(Player player) {
        var vars = locale.newVariables();
        VariableUtils.withPlayer(vars, player, false);
        player.sendMessage(locale.translate(Constants.LanguagePaths.SELL_MUST_CONFIRM, vars));
    }

    @Subcommand("confirm")
    public static void sellConfirm(Player player) {
        execute(player, false);
    }

    @Subcommand("inventory")
    @Permission(Constants.Permissions.SELL_INVENTORY)
    public static void sellInventory(Player player) {
        var vars = locale.newVariables();
        VariableUtils.withPlayer(vars, player, false);
        player.sendMessage(locale.translate(Constants.LanguagePaths.SELL_MUST_CONFIRM, vars));
    }

    @Subcommand("inventory")
    @Permission(Constants.Permissions.SELL_INVENTORY)
    public static void sellInventoryConfirm(Player player, @ALiteralArgument("confirm")String conf) {
        execute(player, true);
    }

    @Subcommand("hand")
    @Permission(Constants.Permissions.SELL_HAND)
    public static void sellHand(Player player, @ALiteralArgument("confirm") String confirm) {
        var vars = locale.newVariables();
        StickyCommands.getInstance().getLogger().severe("CONFIRM="+confirm);
        VariableUtils.withPlayer(vars, player, false);
        if(confirm.equals("confirm"))
            execute(player, false);
        else
        player.sendMessage(locale.translate(Constants.LanguagePaths.SELL_MUST_CONFIRM, vars));
    }

//    @Subcommand("hand")
//    @Permission(Constants.Permissions.SELL_HAND)
//    public static void sellHandConfirm(Player player) {
//
//    }

    @Subcommand("log")
    @Permission(Constants.Permissions.SELL_LOG)
    public static void sellLog(Player player) {
        executeLog(player, 1, null);
    }

    @Subcommand("log")
    @Permission(Constants.Permissions.SELL_LOG)
    public static void sellLogPage(Player player, @AIntegerArgument int page) {
        executeLog(player, page, null);
    }

    @Subcommand("log")
    @Permission(Constants.Permissions.SELL_LOG)
    public static void sellLogPlayer(Player sender, @APlayerArgument Player player) {
        executeLog(sender, 1, player);
    }

    @Subcommand("log")
    @Permission(Constants.Permissions.SELL_LOG)
    public static void sellLogPlayer(Player sender, @APlayerArgument Player player, @AIntegerArgument int page) {
        executeLog(sender, page, player);
    }

    // Execute etc can be combines/refactored, will do later (after worth)
    // preferably let's just get the refactor done first ^^

    private static boolean execute(Player player, boolean inventory) {
        var vars = locale.newVariables();
        VariableUtils.withPlayer(vars, player, false);

        var stack = player.getInventory().getItemInMainHand();
        if (ItemWorths.isIllegal(stack)) {
            player.sendMessage(locale.translate(Constants.LanguagePaths.CANNOT_SELL, vars));
            SoundUtil.sendError(player);
            return false;
        }

        int amountToSell;
        if (inventory) {
            amountToSell = InventoryUtil.count(player.getInventory(), stack.getType());
        } else {
            amountToSell = stack.getAmount();
        }
        var listing = new Listing(market, player, stack.getType(), ItemWorths.get(stack.getType()), amountToSell);
        VariableUtils.withListing(vars, listing, player.getInventory());

        if (instance.getConfig().getBoolean("auto-sell", true) && listing.getSeller().isOnline()) {
            InventoryUtil.removeItems(player.getInventory(), listing.getMaterial(), listing.getQuantity());
            instance.getEconomy().depositPlayer(player, listing.getPrice());
        }
        vars.put("amount", String.valueOf(listing.getQuantity()));
        vars.put("item", StringUtil.capitaliseSentence(listing.getMaterial().toString().replace("_", " ")));
        vars.put("worth", String.valueOf(listing.getPrice()));
        player.sendMessage(locale.translate(Constants.LanguagePaths.SELL_MESSAGE, vars));
        listing.list();
        SoundUtil.sendSuccess(player);
        return true;
    }

    private static boolean executeLog(Player sender, int page, Player player) {
        var vars = locale.newVariables();
        VariableUtils.withPlayer(vars, sender, false);

        List<Listing> listings;
        if (player == null) {
            listings = market.getListings(Listing.SortBy.DATE_DESCENDING, page, 8);
        } else {
            listings = market.getListingsOfPlayer(player, Listing.SortBy.DATE_DESCENDING, page, 8);
        }
        sender.sendMessage(locale.translate(Constants.LanguagePaths.SELL_LOG_MESSAGE, vars));

        for (var listing : listings) {
            VariableUtils.withListing(vars, listing);
            sender.spigot().sendMessage(new ChatMessage(locale.translate(Constants.LanguagePaths.SELL_LOG_LOG, vars))
                    .setHoverMessage(
                            locale.translate(Constants.LanguagePaths.SELL_LOG_LOG_HOVER, vars)).getComponent());
        }

        var tmpPages = market.getListingCount() / 8.0;
        double pages;
        // ew but it works??
        if (tmpPages > Math.round(tmpPages)) {
            pages = Math.round(tmpPages + 1);
        } else {
            pages = tmpPages;
        }

        if (listings.size() < 1 || page > pages) {
            sender.sendMessage(locale.translate(Constants.LanguagePaths.SELL_LOG_NO_SALES, vars));
            SoundUtil.sendSuccess(sender);
            return true;
        }

        vars.put("current", String.valueOf(page));
        vars.put("total", String.valueOf((int) page));
        sender.sendMessage(locale.translate(Constants.LanguagePaths.SELL_LOG_PAGINATOR, vars));
        SoundUtil.sendSuccess(sender);
        return true;
    }
}