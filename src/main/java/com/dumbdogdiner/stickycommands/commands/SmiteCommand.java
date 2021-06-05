package com.dumbdogdiner.stickycommands.commands;

import com.dumbdogdiner.stickyapi.bukkit.command.ExitCode;
import com.dumbdogdiner.stickyapi.common.arguments.Arguments;
import com.dumbdogdiner.stickyapi.common.translation.LocaleProvider;
import com.dumbdogdiner.stickycommands.StickyCommands;

import com.dumbdogdiner.stickycommands.utils.Constants;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.annotations.Alias;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import dev.jorel.commandapi.annotations.Permission;
import dev.jorel.commandapi.annotations.Subcommand;
import dev.jorel.commandapi.annotations.arguments.ALocationArgument;
import dev.jorel.commandapi.annotations.arguments.APlayerArgument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandExecutor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

@Command(Constants.Commands.SMITE)
@Alias({"strike", "lightningify"})
@Permission(Constants.Permissions.SMITE)
public class SmiteCommand {
    private static final LocaleProvider locale = StickyCommands.getPlugin(StickyCommands.class).getLocaleProvider();
    // I really hate doing it this way, but commandapi is dumb and doesnt have
    // the necessary annotation support to do this in any remotely nicer way :(
    // I don't even know if this will work?

    private static final CommandAPICommand smite = new CommandAPICommand(Constants.Commands.SMITE)
            .withPermission(Constants.Permissions.SMITE)
            .executesConsole((sender, args) -> {
                sender.sendMessage(locale.translate("must-be-player", LocaleProvider.newVariables()));
            })
            .withAliases("strike", "lightningify")

            // Smite group
            .withSubcommand(
                    new CommandAPICommand("group")
                            .withArguments(List.of(new MultiLiteralArgument(getGroupsList().toArray(new String[0]))))
                    .executes(SmiteCommand::smiteGroup)
                    .withPermission(Constants.Permissions.SMITE)
            );

    public static void smiteGroup(CommandSender sender, Object [] args) {
        String group = "group." + args[0];
        var vars = LocaleProvider.newVariables();
        vars.put("is_group", String.valueOf(true));
        for(Player target : Bukkit.getOnlinePlayers()){
            if(target.hasPermission("group." + group))
                smitePlayer(sender, target);
        }
    }


    public static void register(){
        CommandAPI.registerCommand(SmiteCommand.class);
        smite.register();
    }



    // TODO Move to StickyAPI, where its better suited?
    public static List<String> getGroupsList() {
        LuckPerms perms = StickyCommands.getInstance().getPerms();
        List<String> returnList = new ArrayList<>();
        if (perms != null) {
            for (Group group : perms.getGroupManager().getLoadedGroups()) {
                returnList.add(group.getName().toLowerCase());
            }
        } else {
            StickyCommands.getInstance().getLogger().severe("Dependency LuckPerms not found!");
        }
        return returnList;
    }
//
//    @Override
//    public ExitCode executeCommand(CommandSender sender, String commandLabel, String[] args) {
//        // Should really be in some sort of util function
//        if (!sender.hasPermission(SMITE_PERMISSION_USE))
//            return ExitCode.EXIT_PERMISSION_DENIED.setMessage(locale.translate("no-permission", vars));
//        Arguments a = new Arguments(Arrays.asList(args));
//        a.optionalFlag("group", "group");
//        a.optionalString("smitetarget", "target");
//
//        String smiteTarget = a.get("smitetarget").toLowerCase();
//        boolean isConsole = !(sender instanceof Player);
//
//        if (a.getFlag("group")) {
//            vars.put("player", a.get("smitetarget"));
//            List<String> groupList = getGroupsList();
//            if (groupList.contains(smiteTarget)) {
//
//                // noinspection unchecked
//                Map<String, String> tempvars = (TreeMap<String, String>) vars.clone();
//                tempvars.remove("player");
//                tempvars.put("player", "%s");
//                tempvars.put("is_group", "true"); // lol
//                for (Player player : Bukkit.getOnlinePlayers()) {
//                    if (player.hasPermission("group." + smiteTarget)) {
//                        if (smitePlayer(player)) {
//
//                            sender.sendMessage(
//                                    String.format(locale.translate("smite.smite-other-player-success", tempvars),
//                                            player.getDisplayName()));
//                        } else {
//                            sender.sendMessage(String.format(locale.translate("not-online-player", tempvars),
//                                    player.getDisplayName()));
//                        }
//                    }
//                }
//                sender.sendMessage(locale.translate("smite.smite-other-player-success", vars));
//                return ExitCode.EXIT_SUCCESS;
//            } else {
//                vars.put("group", vars.get("player"));
//                return ExitCode.EXIT_INVALID_SYNTAX.setMessage(locale.translate("invalid-group", vars));
//            }
//        } else {
//            vars.put("player", a.get("smitetarget"));
//            if (smiteTarget.equals("me") || smiteTarget.equals("target")) {
//                if (isConsole) {
//                    return ExitCode.EXIT_MUST_BE_PLAYER.setMessage(locale.translate("must-be-player", vars));
//
//                } else {
//                    if (smiteTarget.equals("me")) {
//                        smiteMe((Player) sender);
//                        sender.sendMessage(locale.translate("smite.yourself", vars));
//                        return ExitCode.EXIT_SUCCESS;
//                    } else { // MUST be target
//                        // TODO: Allow targetting an entity rather than a block
//                        Location toStrike = ((Player) sender).getTargetBlock(null, Constants.SMITE_TARGET_RANGE).getLocation();
//                        World strikeWorld = ((Player) sender).getWorld();
//                        vars.put("X", Integer.toString(toStrike.getBlockX()));
//                        vars.put("Y", Integer.toString(toStrike.getBlockY()));
//                        vars.put("Z", Integer.toString(toStrike.getBlockZ()));
//                        vars.put("WORLD", strikeWorld.getName());
//                        smiteLocation(toStrike, strikeWorld);
//                        sender.sendMessage(locale.translate("smite.smite-block", vars));
//                        return ExitCode.EXIT_SUCCESS;
//                    }
//                }
//            } else {
//
//                if (smitePlayer(smiteTarget)) {
//                    sender.sendMessage(locale.translate("smite.smite-other-player-success", vars));
//                    return ExitCode.EXIT_SUCCESS;
//                } else {
//
//                    return ExitCode.EXIT_INVALID_SYNTAX.setMessage(locale.translate("not-online-player", vars));
//
//                }
//            }
//        }
//    }


    /**
     * Smite a player
     *
     * @param target player to smite
     *
     * @return if player exists (and was smitten) or not
     */
    @Default
    public static boolean smitePlayer(CommandSender me, @APlayerArgument Player target) {
        if (target == null)
            return false;
        var vars = LocaleProvider.newVariables();
        vars.put("PLAYER", target.getName());
        if(target.hasPermission(Constants.Permissions.SMITE_IMMUNE)) {
            me.sendMessage(locale.translate(Constants.Messages.SMITE.SMITE_IMMUNE, vars));
        } else {
            smiteLocation(target.getLocation());
            target.sendMessage(locale.translate(Constants.Messages.SMITE.SMITE_MESSAGE, vars));
            me.sendMessage(locale.translate(Constants.Messages.SMITE.SMITE_OTHER_PLAYER_SUCCESS, vars));
        }
        return true;
    }

    /**
     * Smite the sender
     *
     * @param sender command sender
     */
    @Subcommand("me")
    public static void smiteMe(Player sender) {
        var vars = LocaleProvider.newVariables();
        smiteLocation(sender.getLocation());
        sender.sendMessage(locale.translate("smite.yourself", vars));
    }

    @Default
    public static void smiteBlock(CommandSender sender, @ALocationArgument Location toStrike){
        var vars = LocaleProvider.newVariables();
        vars.put("X", Integer.toString(toStrike.getBlockX()));
        vars.put("Y", Integer.toString(toStrike.getBlockY()));
        vars.put("Z", Integer.toString(toStrike.getBlockZ()));
        vars.put("WORLD", toStrike.getWorld().getName());
        smiteLocation(toStrike);
        sender.sendMessage(locale.translate("smite.smite-block", vars));
    }

    /**
     * Strike lightning on a given block
     *
     * @param location Location to strike
     */
    private static void smiteLocation(Location location) {
//        Bukkit.getScheduler().scheduleSyncDelayedTask(StickyCommands.getInstance(), new Runnable() {
//            @Override
//            public void run() {
                World w = location.getWorld();
                LightningStrike strike = w.strikeLightning(location);
                w.spawnParticle(Particle.SMOKE_LARGE, location, 2);
                w.createExplosion(location, (float) StickyCommands.getInstance().getConfig().getDouble(Constants.SettingsPaths.SMITE_EXPLOSION_STRENGTH, Constants.DEFAULT_SMITE_EXPLOSION_STRENGTH), false, false, strike);
//            }
//        }, 1L);
    }
}