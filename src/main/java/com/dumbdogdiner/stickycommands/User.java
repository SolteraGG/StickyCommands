package com.dumbdogdiner.stickycommands;

import java.util.UUID;

import com.ristexsoftware.knappy.cache.Cacheable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import lombok.Getter;
import lombok.Setter;

public class User implements Cacheable {

    /**
     * The username of the user.
     */
    @Getter
    @Setter
    private String username;

    /**
     * The UUID of the user.
     */
    @Getter
    @Setter
    private UUID uniqueId;

    /**
     * Whether or not this user is AFK.
     */
    @Getter
    @Setter
    private boolean afk;

    public User(String username, UUID uniqueId) {
        this.username = username;
        this.uniqueId = uniqueId;
    }

    /**
     * Get the {@link org.bukkit.entity.Player} object from this user
     */
    public Player getPlayer() {
        return Bukkit.getPlayer(this.uniqueId);
    }

    /**
     * Create a new user object from a player object.
     */
    public static User fromPlayer(Player player) {
        return new User(player.getName(), player.getUniqueId());
    }

    // public User(Player player) {
    //     this(player.getName(), player.getUniqueId());
    // }

    public String getKey() {
        return this.uniqueId.toString();
    }

    public void setFlySpeed(Float speed) {
        // Make sure shit doesn't break
        Bukkit.getPlayer(this.uniqueId).setFlySpeed(speed);
        Main.getInstance().getDatabase().setSpeed(this.uniqueId, speed, 1);
    }

    public void setWalkSpeed(Float speed) {
        try {
            Bukkit.getPlayer(this.uniqueId).setWalkSpeed(speed);
            Main.getInstance().getDatabase().setSpeed(this.uniqueId, speed, 0);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}