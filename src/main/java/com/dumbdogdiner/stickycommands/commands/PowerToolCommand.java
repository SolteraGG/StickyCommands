package com.dumbdogdiner.stickycommands.commands;

import com.dumbdogdiner.stickyapi.bukkit.util.SoundUtil;
import com.dumbdogdiner.stickyapi.common.translation.LocaleProvider;
import com.dumbdogdiner.stickycommands.StickyCommands;
import com.dumbdogdiner.stickycommands.utils.Constants;
import com.dumbdogdiner.stickycommands.utils.PowerTool;
import com.dumbdogdiner.stickycommands.utils.VariableUtils;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Permission;
import dev.jorel.commandapi.annotations.Subcommand;
import dev.jorel.commandapi.annotations.arguments.AGreedyStringArgument;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;

import java.util.Map;

@Command("powertool")
@Permission(Constants.Permissions.POWERTOOL)
public class PowerToolCommand {
    static StickyCommands instance = StickyCommands.getInstance();
    static LocaleProvider locale = instance.getLocaleProvider();

    public static void powertool(Player player, @AGreedyStringArgument String command) {
        var vars = locale.newVariables();
        VariableUtils.withPlayer(vars, player, false);
        // can't assign your hand...
        if (bindingAir(player, vars)) return;
        vars.put("command", command);
        var powertool = new PowerTool(player.getInventory().getItemInMainHand().getType(), command, player);
        instance.getOnlineUser(player.getUniqueId()).addPowerTool(powertool);

        player.sendMessage(locale.translate(Constants.LanguagePaths.POWERTOOL_ASSIGNED, vars));
        SoundUtil.sendSuccess(player);
    }

    @Subcommand("clear")
    @Permission(Constants.Permissions.POWERTOOL_CLEAR)
    public static void powertoolClear(Player player) {
        var vars = locale.newVariables();
        VariableUtils.withPlayer(vars, player, false);
        clearTool(player, vars);
        SoundUtil.sendSuccess(player);
    }

    @Subcommand("toggle")
    @Permission(Constants.Permissions.POWERTOOL_TOGGLE)
    public static void powertoolToggle(Player player) {
        var vars = locale.newVariables();
        VariableUtils.withPlayer(vars, player, false);
        // can't assign your hand...
        if (bindingAir(player, vars)) return;
        var powertool = instance.getOnlineUser(player.getUniqueId()).getPowerTools().get(player.getInventory().getItemInMainHand().getType());
        if (powertool == null) {
            player.sendMessage(locale.translate(Constants.LanguagePaths.NO_POWERTOOL, vars));
        } else {
            powertool.setEnabled(!powertool.isEnabled());
            vars.put("toggled", String.valueOf(powertool.isEnabled()));

            player.sendMessage(locale.translate(Constants.LanguagePaths.POWERTOOL_TOGGLED, vars));
            SoundUtil.sendSuccess(player);
        }
    }
    
    private static boolean bindingAir(Player player, Map<String, String> vars) {
        if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
            player.sendMessage(locale.translate(Constants.LanguagePaths.POWERTOOL_CANNOT_BIND_AIR, vars));
            SoundUtil.sendError(player);
            return true;
        }
        return false;
    }

    private static void clearTool(Player player, Map<String, String> vars) {
        vars.put("syntax", "/powertool [command/clear/toggle]");
        VariableUtils.withPlayer(vars, player, false);
        if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
            // TODO: Move to messages
            player.sendMessage(locale.translate(Constants.LanguagePaths.PREFIX, vars) + ChatColor.RED + "You do not have a powertool in your hand!");
            SoundUtil.sendError(player);
            return;
        }
        var user = instance.getOnlineUser(player.getUniqueId());
        var powertool = user.getPowerTools().get(player.getInventory().getItemInMainHand().getType());
        if (powertool != null) {
            user.removePowerTool(powertool.getItem());
        }
        player.sendMessage(locale.translate(Constants.LanguagePaths.POWERTOOL_CLEARED, vars));
    }
}