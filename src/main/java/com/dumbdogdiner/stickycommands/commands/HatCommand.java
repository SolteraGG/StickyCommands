package com.dumbdogdiner.stickycommands.commands;

import com.dumbdogdiner.stickyapi.bukkit.command.AsyncCommand;
import com.dumbdogdiner.stickyapi.bukkit.command.ExitCode;
import com.dumbdogdiner.stickyapi.bukkit.command.StickyPluginCommand;
import com.dumbdogdiner.stickyapi.common.arguments.Arguments;
import com.dumbdogdiner.stickyapi.common.command.ExitCode;
import com.dumbdogdiner.stickyapi.common.translation.LocaleProvider;
import com.dumbdogdiner.stickycommands.StickyCommands;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HatCommand extends StickyPluginCommand {
    private static final LocaleProvider locale = StickyCommands.getInstance().getLocaleProvider();
    private static final  String HAT_PERMISSION = "stickycommands.hat";
    private static final String HAT_REMOVE_FLAG = "remove";
    private static final String HAT_OFFHAND_FLAG = "offhand";


    /**
     * Create a new command for the associated plugin
     */
    public HatCommand() {
        super("hat", null, StickyCommands.getInstance());
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


        /*
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
            }*/
            return ExitCode.EXIT_SUCCESS;
        } else {
            return ExitCode.EXIT_PERMISSION_DENIED;
        }
    }

    @Override
    public ExitCode execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull Arguments arguments, @NotNull Map<String, String> map) {
        return null;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) throws IllegalArgumentException, CommandException {
        return null;
    }
}
