package com.dumbdogdiner.stickycommands.commands;

import com.dumbdogdiner.stickyapi.bukkit.command.AsyncCommand;
import com.dumbdogdiner.stickyapi.bukkit.command.ExitCode;
import com.dumbdogdiner.stickyapi.common.arguments.Arguments;
import com.dumbdogdiner.stickyapi.common.translation.LocaleProvider;
import com.dumbdogdiner.stickycommands.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.TreeMap;

public class Smite extends AsyncCommand {
    private enum SmiteStatus {
        NO_PLAYER,
        PLAYER_IMMUNE,
        SMITTEN
    }

    private final LocaleProvider locale = Main.getPlugin(Main.class).getLocaleProvider();
    TreeMap<String, String> variables = locale.newVariables();
    private static final String PERMISSION_USE = "stickycommands.smite";
    private static final String PERMISSION_IMMUNE = "stickycommands.smite.immune";

    private static final float EXPLOSION_STRENGTH = 2.5F;


    public Smite(Plugin owner) {
        super("smite", owner);
        setPermission(PERMISSION_USE);
        setDescription("Smite a player, block, or yourself...");
        setAliases(Arrays.asList("strike", "lightningify"));
        variables.put("syntax", "/smite [player | me | " + ChatColor.BOLD + "target" + ChatColor.RESET + "]");
    }

    @Override
    public ExitCode executeCommand(CommandSender sender, String commandLabel, String[] args) {
        // Should really be in some sort of util function
        if (!sender.hasPermission(PERMISSION_USE))
            return ExitCode.EXIT_PERMISSION_DENIED.setMessage(locale.translate("no-permission", variables));
        Arguments a = new Arguments(args);
        a.optionalString("smitetarget", "target");

        String smiteTarget = a.get("smitetarget").toLowerCase();
        boolean isConsole = !(sender instanceof Player);

        if (smiteTarget.equals("me") || smiteTarget.equals("target")) {
            if (isConsole) {
                return ExitCode.EXIT_MUST_BE_PLAYER.setMessage(locale.translate("must-be-player", variables));
            } else {
                if (smiteTarget.equals("me")) {
                    smiteMe((Player) sender);
                    return ExitCode.EXIT_SUCCESS;
                } else { // MUST be target
                    // TODO: Allow targetting an entity rather than a block
                    Location toStrike = ((Player) sender).getTargetBlock(null, 5).getLocation();
                    World strikeWorld = ((Player) sender).getWorld();
                    variables.put("X", Integer.toString(toStrike.getBlockX()));
                    variables.put("Y", Integer.toString(toStrike.getBlockY()));
                    variables.put("Z", Integer.toString(toStrike.getBlockZ()));
                    variables.put("WORLD", strikeWorld.toString());
                    lightningOnCoord(toStrike, strikeWorld);
                    return ExitCode.EXIT_SUCCESS.setMessage(locale.translate("smite-block", variables));
                }
            }
        } else {
            switch (smitePlayer(smiteTarget)) {
                case SMITTEN:
                    return ExitCode.EXIT_SUCCESS.setMessage(locale.translate("smite-other-player-success", variables));
                case NO_PLAYER:
                    return ExitCode.EXIT_INVALID_SYNTAX.setMessage(locale.translate("smite-not-a-player", variables));
                case PLAYER_IMMUNE:
                    return ExitCode.EXIT_PERMISSION_DENIED.setMessage(locale.translate("smite-immune", variables));
            }
        }

        // SHOULD NOT REACH HERE
        return ExitCode.EXIT_INVALID_STATE;
    }

    /**
     * Smite a player
     *
     * @param playerName player to smite
     * @return if player exists (and was smitten) or not
     */
    private SmiteStatus smitePlayer(String playerName) {
        Player player = Bukkit.getPlayer(playerName);
        variables.put("player", playerName);
        if (player == null)
            return SmiteStatus.NO_PLAYER;
        if (player.hasPermission(PERMISSION_IMMUNE))
            return SmiteStatus.PLAYER_IMMUNE;
        lightningOnCoord(player.getLocation(), player.getWorld());
        player.sendMessage(locale.translate("smite-message", variables));
        return SmiteStatus.SMITTEN;
    }

    /**
     * Smite the sender
     *
     * @param sender command sender
     */
    private void smiteMe(Player sender) {
        lightningOnCoord(sender.getLocation(), sender.getWorld());
    }

    /**
     * Strike lightning on a given block
     *
     * @param location Location to strike
     * @param world    world of the location
     */

    private void lightningOnCoord(Location location, World world) {
        LightningStrike strike = world.strikeLightning(location);

        world.createExplosion(location, EXPLOSION_STRENGTH, false, false, strike);
    }
}
