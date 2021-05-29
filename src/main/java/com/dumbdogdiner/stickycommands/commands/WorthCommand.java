package com.dumbdogdiner.stickycommands.commands;

import com.dumbdogdiner.stickyapi.bukkit.util.SoundUtil;
import com.dumbdogdiner.stickyapi.common.translation.LocaleProvider;
import com.dumbdogdiner.stickyapi.common.util.NumberUtil;
import com.dumbdogdiner.stickycommands.StickyCommands;
import com.dumbdogdiner.stickycommands.utils.Constants;
import com.dumbdogdiner.stickycommands.utils.ItemWorths;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import dev.jorel.commandapi.annotations.Permission;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

@Command(Constants.Commands.WORTH)
@Permission(Constants.Permissions.WORTH)
public class WorthCommand {
    private static final StickyCommands instance = StickyCommands.getInstance();
    private static final LocaleProvider locale = instance.getLocaleProvider();
    private static final Logger logger = instance.getLogger();

    @Default
    public static void worth(Player player){
        ItemStack item =  player.getInventory().getItemInMainHand();
        Material type = item.getType();


        if(type.isAir()) {
            SoundUtil.sendError(player);
            player.sendMessage(locale.translate("sell.cannot-sell", locale.newVariables()));
        }

        boolean isDamagable = item.getItemMeta() instanceof Damageable;

        double inventoryWorth;
        double singleUndamagedWorth = ItemWorths.get(type);
        double handWorth = ItemWorths.getWorthOfItems(Collections.singletonList(item));

        List<ItemStack> inventoryItems = new ArrayList<>();
        for(ItemStack itemStack : player.getInventory().getContents()){
            if(itemStack == null)
                continue;
            if(ItemWorths.isIllegal(itemStack))
                logger.severe(MessageFormat.format("Player {0} (UUID: {1}) has an illegal item, {2}", player.getName(), player.getUniqueId().toString(), itemStack.getType().toString()));
            if(itemStack.getType().equals(type))
                inventoryItems.add(itemStack);
        }
        inventoryWorth = ItemWorths.getWorthOfItems(inventoryItems);


        var vars = locale.newVariables();
        vars.put("item", item.getI18NDisplayName());
        vars.put("single_worth", NumberUtil.formatPrice(singleUndamagedWorth));
        vars.put("hand_worth", NumberUtil.formatPrice(handWorth));
        vars.put("inventory_worth", NumberUtil.formatPrice(inventoryWorth));
        if(singleUndamagedWorth == 0){
            SoundUtil.sendError(player);
            player.sendMessage(locale.translate("sell.cannot-sell", vars));
        } else if(singleUndamagedWorth < 0 || inventoryWorth < 0 || handWorth < 0){
            SoundUtil.sendError(player);
            player.sendMessage(locale.translate("sell.bad-worth", vars));
        } else if(isDamagable){
            SoundUtil.sendSuccess(player);
            player.sendMessage(locale.translate("sell.damagable-worth", vars));
        } else {
            SoundUtil.sendSuccess(player);
            player.sendMessage(locale.translate("sell.worth-message", vars));
        }
    }
}
