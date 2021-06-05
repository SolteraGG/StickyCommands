package com.dumbdogdiner.stickycommands.commands;

import com.dumbdogdiner.stickyapi.bukkit.util.SoundUtil;
import com.dumbdogdiner.stickyapi.common.translation.LocaleProvider;
import com.dumbdogdiner.stickycommands.StickyCommands;
import com.dumbdogdiner.stickycommands.utils.Constants;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import dev.jorel.commandapi.annotations.Permission;
import dev.jorel.commandapi.annotations.Subcommand;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

@Command("hat")
@Permission(Constants.Permissions.HAT)
public class HatCommand {
    private static final LocaleProvider locale = StickyCommands.getInstance().getLocaleProvider();

    /**
     * Swap the item in the player's main hand with the item in the helmet slot
     *
     * @param player Player who ran the command
     */
    @Default
    public static void hat(Player player) {
        swapHelmet(player, EquipmentSlot.HAND);
    }

    /**
     * Swap the item in the player's offhand with the item in the helmet slot
     *
     * @param player Player who ran the command
     */
    @Subcommand("offhand")
    public static void offhand(Player player) {
        swapHelmet(player, EquipmentSlot.OFF_HAND);
    }

    /**
     * Swap the item in the player's helmet slot with another slot
     *
     * @param player The player to work with
     * @param slot   Which slot to swap with
     */
    public static void swapHelmet(Player player, EquipmentSlot slot) {
        PlayerInventory inventory = player.getInventory();
        ItemStack hat = inventory.getHelmet();
        ItemStack held = inventory.getItem(slot);
        inventory.setHelmet(held);
        inventory.setItem(slot, hat);
        if (held == null || held.getType().equals(Material.AIR)) {
            player.sendMessage(locale.translate("hat.remove-hat", LocaleProvider.newVariables()));
            SoundUtil.sendSuccess(player);
        } else {
            player.sendMessage(locale.translate("hat.new-hat", LocaleProvider.newVariables()));
            SoundUtil.sendSuccess(player);
        }
    }

    /**
     * Remove a hat from a player
     *
     * @param player Player who ran the command
     */
    @Subcommand("remove")
    public static void remove(Player player) {
        PlayerInventory inventory = player.getInventory();
        ItemStack hat = inventory.getHelmet();

        if (hat == null || hat.getType().equals(Material.AIR)) {
            // No point in going further if they werent wearing anything
            player.sendMessage(locale.translate("hat.no-hat", LocaleProvider.newVariables()));
            SoundUtil.sendError(player);
            return;
        }
        // Remove the hat from their head
        inventory.setHelmet(null);


        if (inventory.getItemInMainHand().getType().equals(Material.AIR)) {
            // Put it in their hand if their hand is free
            inventory.setItemInMainHand(hat);
        } else if (inventory.firstEmpty() != -1) {
            // Inventory isn't full, put the hat in the inventory
            var overflow = inventory.addItem(hat);
            if (overflow.size() > 0) {
                // This shouldn't happen but we can deal with any weird illegal stacks at this time
                // We just drop the overflow
                player.getWorld().dropItemNaturally(player.getLocation(), hat);
            }
        } else {
            // Inventory is full
            if (inventory.getItemInOffHand().getType().equals(Material.AIR)) {
                // Put it in their offhand if we can
                inventory.setItemInOffHand(hat);
            } else {
                // We don't have anywhere else to put it so we have to drop it on the ground
                Location l = player.getLocation().clone();
                // Not sure if this does something useful or not
                l.add(l.getDirection());
                l.getWorld().dropItemNaturally(l, hat);
            }
        }
        player.sendMessage(locale.translate("hat.remove-hat", LocaleProvider.newVariables()));
        SoundUtil.sendSuccess(player);
    }



    

/*
    private final TreeMap<String, String> variables = locale.newVariables();
    private static final  String HAT_PERMISSION = "stickycommands.hat";
    private static final String HAT_REMOVE_FLAG = "remove";
    private static final String HAT_OFFHAND_FLAG = "offhand";

    *//**
     * Create a new command for the associated plugin
     *//*
    public HatCommand(Plugin owner) {
        super("hat", owner);
        setPermission(HAT_PERMISSION);
        setDescription("Wear an item as a hat (Swaps your helmet with what's in your offhand)");
        variables.put("syntax", "/hat [remove | offhand]");
    }

    @Override
    public ExitCode executeCommand(CommandSender sender, String commandLabel, String[] args) {
        if(!(sender instanceof Player)){
            return ExitCode.EXIT_MUST_BE_PLAYER;
        } else if(sender.hasPermission(HAT_PERMISSION)){
            Arguments arguments = new Arguments(args);
            arguments.optionalFlag(HAT_REMOVE_FLAG);
            arguments.optionalFlag(HAT_OFFHAND_FLAG);

            boolean useOffhand = arguments.getFlag(HAT_OFFHAND_FLAG);

            PlayerInventory inv = ((Player) sender).getInventory();
            ItemStack handItem = useOffhand ? inv.getItemInOffHand() : inv.getItemInMainHand();

            ItemStack oldHelmet = inv.getHelmet();
            if(arguments.getFlag(HAT_REMOVE_FLAG)){
                sender.sendMessage("Remove is not specifically implemented, rerun with an empty hand instead.");
                return ExitCode.EXIT_SUCCESS;
            }

            inv.setHelmet(handItem);

            if(useOffhand){
                inv.setItemInOffHand(oldHelmet);
            } else {
                inv.setItemInMainHand(oldHelmet);
            }


        *//*
            if(false){
                // See if mainhand is free
                if(handItem.getType().isAir()){
                    // See if offhand is free
                    inv.setItemInMainHand(oldHelmet);
                    inv.setHelmet(null);
                } else {
                    ItemStack offHandItem = inv.getItemInOffHand();
                    if(offHandItem.getType().isAir()){

                    }
                }

            } else {
                inv.setItemInMainHand(oldHelmet);
                inv.setHelmet(handItem);
            }*//*
            return ExitCode.EXIT_SUCCESS;
        } else {
            return ExitCode.EXIT_PERMISSION_DENIED;
        }
    }*/
}
