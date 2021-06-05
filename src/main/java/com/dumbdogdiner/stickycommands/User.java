package com.dumbdogdiner.stickycommands;

import com.dumbdogdiner.stickyapi.common.cache.Cacheable;
import com.dumbdogdiner.stickycommands.database.tables.Users;
import com.dumbdogdiner.stickycommands.utils.PowerTool;
import com.dumbdogdiner.stickycommands.utils.SpeedType;
import lombok.Getter;
import lombok.Setter;
import me.xtomyserrax.StaffFacilities.SFAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

// TODO: add custom setters that update db on set

public class User implements Cacheable {


    @Getter @Setter
    private boolean firstJoinItemsGiven;

    @Getter @Setter
    private long firstSeen;

    @Getter @Setter
    private long lastSeen;

    /**
     * The username of the user.
     */
    @NotNull @Getter @Setter
    private String name;

    /**
     * The UUID of the user.
     */
    @NotNull @Setter @Getter
    private UUID uniqueId;

    /**
     * The list of powertools the user has
     */
    @Getter
    private HashMap<Material, PowerTool> powerTools = new HashMap<Material, PowerTool>();

    /**
     * Whether or not this user is AFK.
     * We need a CUSTOM setter.
     */
    @Getter
    private boolean afk = false;

    @Getter
    @NotNull
    private Integer afkTime = 0;



    public void setAfk(boolean AFKState) {
        if (!AFKState)
            afkTime = 0;
        if(this.afk == AFKState)
            return;
        afk = AFKState;
        Map<String, String> vars = new HashMap<>();
        vars.put("PLAYER", name);

        if (!isHidden()) {
            if (isAfk()) {
                Bukkit.broadcastMessage(StickyCommands.getInstance().getLocaleProvider().translate("afk.afk", vars));
            } else {
                Bukkit.broadcastMessage(
                        StickyCommands.getInstance().getLocaleProvider()
                                .translate("afk.not-afk", vars));
            }
        }
    }

    /**
     * Checks if a given player is hidden, vanished, staffvanished, or fakeleaved
     *
     * @return Whether the user is hidden.
     */
    public boolean isHidden() {
        if (StickyCommands.getInstance().isStaffFacilitiesEnabled()) {
            Player player = this.getPlayer();
            /*System.out.println(SFAPI.isPlayerFakeleaved(player));
            System.out.println(SFAPI.isPlayerStaffVanished(player));
            System.out.println(SFAPI.isPlayerVanished(player));
            System.out.println(isVanished()); */
            return SFAPI.isPlayerFakeleaved(player) ||
                    SFAPI.isPlayerStaffVanished(player) ||
                    SFAPI.isPlayerVanished(player) ||
                    isVanished();
        }
        return false;
    }

    /**
     * Checks if a given player is in a vanished state.
     *
     * @return Whether the user is vanished.
     */
    public boolean isVanished() {
        for (MetadataValue meta : getPlayer().getMetadata("vanished")) {
            if (meta.asBoolean()) {
                return true;
            }
        }

        return false;
    }


    public int incAfkTime() {
        return ++afkTime;
    }

    public void resetAfkTime() {
        afkTime = 0;
    }

    // I spent an hour trying to come up with a good solution to this weird problem where if you are being pushed by water, and on the corner water block, your from block is considered air and not water...
    // So, we need to keep a buffer of the last 3 blocks the player stood in, and if it contains water, we'll consider it as the water pushing them, since there's no event for
    // checking if a player is being pushed by water!
    @Getter
    @NotNull
    private ArrayList<Material> blockBuffer = new ArrayList<Material>();

    public User(@NotNull String username, @NotNull UUID uniqueId) {
        this.name = username;
        this.uniqueId = uniqueId;
        // FIXME get stuff from database here!
        StickyCommands.getDatabaseHandler().loadUser(this.uniqueId, row -> {
            this.firstJoinItemsGiven = row.get(Users.INSTANCE.getFirstJoinItemsGiven());
            this.firstSeen = row.get(Users.INSTANCE.getFirstSeen());
            this.lastSeen = row.get(Users.INSTANCE.getLastSeen());
        });
    }

    public User(Player player) {
        this(player.getName(), player.getUniqueId());
    }

    /**
     * Get the {@link Player} object from this user
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

    public String getKey() {
        return this.uniqueId.toString();
    }

    public void addPowerTool(PowerTool powerTool) {
        this.powerTools.put(powerTool.getItem(), powerTool);
    }

    public void removePowerTool(Material item) {
        for (PowerTool pt : this.powerTools.values().stream().collect(Collectors.toList())) {
            if (item == pt.getItem())
                this.powerTools.remove(pt.getItem());
        }
    }

    public void setSpeed(SpeedType type, float speed) {
        if (speed <= 0F)
            speed = 0.1F;

        else if (speed > 1F)
            speed = 1F;

        if (type == SpeedType.WALK)
            speed = (speed + 0.1F > 1F) ? speed : speed + 0.1F;

        Player p = getPlayer();
        assert p != null;

        switch (type) {
            case FLY:
                p.setFlySpeed(speed);
                StickyCommands.getDatabaseHandler().setSpeed(this.uniqueId, speed, true);
                break;
            case WALK:
                p.setWalkSpeed(speed);
                StickyCommands.getDatabaseHandler().setSpeed(this.uniqueId, speed, false);
                break;
        }
    }

    public void toggleAfk() {
        setAfk(!isAfk());
    }

}