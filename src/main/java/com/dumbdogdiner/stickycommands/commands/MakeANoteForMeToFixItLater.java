package com.dumbdogdiner.stickycommands.commands;

import com.dumbdogdiner.stickyapi.bukkit.util.SoundUtil;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import dev.jorel.commandapi.annotations.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Objects;
import java.util.UUID;

@Command("makeanoteformetofixitlater")
@Permission("group.developer")
public class MakeANoteForMeToFixItLater {
    private static final UUID STIXIL = UUID.fromString("194391c2-6bf5-4c0a-bd95-c4fa9fa01112");

    @Default
    public static void makeANoteForMeToFixItLater(CommandSender sender) {
        if (Bukkit.getOfflinePlayer(STIXIL).isOnline()) {
            Player p = Bukkit.getPlayer(STIXIL);
            assert p != null;
            Location loc = p.getLocation();
            Fox f = (Fox) p.getWorld().spawnEntity(loc, EntityType.FOX);
            f.setSleeping(true);
            f.setAdult();
            f.setInvulnerable(true);
            f.setGlowing(true);
            f.setAI(false);
            f.setCanPickupItems(false);
            f.setFoxType(Fox.Type.SNOW);
            f.setCustomName("Stixil the lazy");

            Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
            FireworkMeta fwm = fw.getFireworkMeta();

            fwm.setPower(256);
            fwm.addEffect(FireworkEffect.builder().withFade(Color.RED, Color.ORANGE, Color.BLUE, Color.PURPLE).withTrail().flicker(true).build());

            fw.setFireworkMeta(fwm);
            p.setHealth(Objects.requireNonNull(p.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
            fw.detonate();
            p.setHealth(0);
            p.sendMessage("I'm sorry Stixil");
            SoundUtil.sendError(p);
            SoundUtil.sendInfo(p);
            SoundUtil.sendSuccess(p);
        }
    }
}
