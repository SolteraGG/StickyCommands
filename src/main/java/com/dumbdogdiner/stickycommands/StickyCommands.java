package com.dumbdogdiner.stickycommands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import com.dumbdogdiner.stickycommands.commands.*;
import com.dumbdogdiner.stickycommands.database.PostgresHandler;
import com.dumbdogdiner.stickycommands.objects.Market;
import com.dumbdogdiner.stickycommands.listeners.PlayerInteractionListener;
import com.dumbdogdiner.stickycommands.listeners.PlayerJoinListener;
import com.dumbdogdiner.stickycommands.runnables.AfkTimeRunnable;
import com.dumbdogdiner.stickycommands.listeners.AfkEventListener;
import com.dumbdogdiner.stickycommands.utils.Item;
import com.dumbdogdiner.stickyapi.StickyAPI;
import com.dumbdogdiner.stickyapi.bukkit.util.CommandUtil;
import com.dumbdogdiner.stickyapi.bukkit.util.StartupUtil;
import com.dumbdogdiner.stickyapi.common.translation.LocaleProvider;
import com.dumbdogdiner.stickyapi.common.util.TimeUtil;

import dev.jorel.commandapi.CommandAPI;
import kr.entree.spigradle.annotations.PluginMain;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;


// I really, ***REALLY*** didn't want this to be in kotlin but i might not have a choice....... Which is dumb, and stupid. AND it makes some bits of the interfaecee thing even dumber
// but maybe i can make some stuff work half decent with some jvmstatics but it involves some messy companion objects.

// Alternatively i can do various levels of fuckery
// but then we return to the multiple different systems that I greatly hate
// but maybe i can do a couple modules or something. Or even make, as far as things care, a kotlin class for these particular getters. i leit dont fucking know
@PluginMain
public class StickyCommands extends JavaPlugin {

    /**
     * The singleton instance of the plugin.
     */
    @Getter
    private static StickyCommands instance;

    @Getter
    private static Logger logger;

    @Getter
    protected static Boolean enabled = false;

    @Getter
    private static boolean staffFacilitiesEnabled;


    /**
     * Thread pool for the execution of asynchronous tasks.
     */
    @Getter
    protected static ExecutorService pool = Executors.newFixedThreadPool(3);

    /**
     * Cache of all online users.
     */
    @Getter
    protected static HashMap<UUID, User> onlineUserCache = new HashMap<UUID, User>();


    /**
     * AFK TimerTask that tracks how long a player has been AFK
     */
    @Getter
    protected static Timer afkRunnable = new Timer();


    /**
     * The server's uptime in seconds
     */
    @Getter
    protected static Long upTime = TimeUtil.getUnixTime();

    /**
     * The current vault com.dumbdogdiner.stickycommands.economy instance.
     */
    @Getter
    private static Economy economy = null;

    @Getter
    private static LocaleProvider localeProvider;


    /**
     * The LuckPerms API instance
     */
    @Getter
    private static LuckPerms perms;

    /**
     * The com.dumbdogdiner.stickycommands.database connected
     */
    @Getter
    private static PostgresHandler databaseHandler;


    /**
     * The market
     */
    @Getter
    private static Market market;


    @Override
    public void onLoad() {
        enabled = true;
        instance = this;
        logger = super.getLogger();
        // Set our thread pool
        StickyAPI.setPool(pool);
        new Item();
        // onlineUserCache.setMaxSize(1000);
    }

    @Override
    public void onEnable() {
        if (!StartupUtil.setupConfig(this))
            return;

        localeProvider = StartupUtil.setupLocale(this, localeProvider);
        if (localeProvider == null) {
            logger.severe("Could not setup locales! Fuck this shit, I'm out!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        databaseHandler = new PostgresHandler(getConfig(), getLogger());
        if (!databaseHandler.init()) {
            logger.severe("Database is buggered, I can't deal with this, goodbye!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        // I think this is done in postgreshandler
//        com.dumbdogdiner.stickycommands.database.createMissingTables();

        if (!setupPAPI())
            getLogger().severe("PlaceholderAPI is not available, is it installed?");

        if (!setupEconomy())
            getLogger().severe("Disabled com.dumbdogdiner.stickycommands.economy commands due to no Vault dependency found!");
        else
            market = new Market(databaseHandler);

        if (!setupLuckperms())
            getLogger().severe("Disabled group listing/luckperms dependant features due to no Luckperms dependency found!");

        if (!checkStaffFacilities())
            getLogger().severe("StaffFacilities not found, disabling integration");


        // Register currently online users - in case of a reload.
        // (stop reloading spigot, please.)
        for (Player player : Bukkit.getOnlinePlayers()) {
            onlineUserCache.put(player.getUniqueId(), new User(player));
        }

        if (!registerEvents())
            return;

        if (!registerCommands())
            return;

        afkRunnable.scheduleAtFixedRate(new AfkTimeRunnable(), 1000L, 1000L); // We must run this every ONE second!

        getLogger().info("StickyCommands started successfully!");
    }

    private boolean checkStaffFacilities() {
        return staffFacilitiesEnabled = Bukkit.getPluginManager().getPlugin("StaffFacilities") != null;
    }


    private boolean setupPAPI() {
        // Register PAPI support if present
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            Bukkit.getLogger().info("Registering PlaceholderAPI placeholders");

            StickyCommandsPlaceholder.getInstance().register();
            return true;
        } else {
            return false;
        }

    }

    private boolean setupLuckperms() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            perms = provider.getProvider();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onDisable() {
        reloadConfig(); // Save our config
        // no longer needed??? com.dumbdogdiner.stickycommands.database.terminate(); // Terminate our com.dumbdogdiner.stickycommands.database connection
        afkRunnable.cancel(); // Stop our AFK runnable
        enabled = false;
    }

    /**
     * Setup the vault com.dumbdogdiner.stickycommands.economy instance.
     */
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    /**
     * Register all the commands!
     */
    boolean registerCommands() {
//        List<Command> commandList = new ArrayList<Command>();
        // Register com.dumbdogdiner.stickycommands.economy based commands only if the com.dumbdogdiner.stickycommands.economy provider is not null.
//        if (economy != null) {
//            commandList.add(new SellCommand(this));
//            commandList.add(new WorthCommand(this));
//        }
//
//        commandList.add(new SpeedCommand(this));
//        commandList.add(new SeenCommand(this));
//
//        commandList.add(new KillCommand(this));
//        commandList.add(new JumpCommand(this));
//        commandList.add(new MemoryCommand(this));
//        commandList.add(new TopCommand(this));
//        commandList.add(new PowerToolCommand(this));
        CommandAPI.registerCommand(AfkCommand.class);
//        commandList.add(new PlayerTimeCommand(this));
//        commandList.add(new SmiteCommand(this));
//        commandList.add(new HatCommand(this));
//
//        CommandUtil.registerCommands(getServer(), commandList);
        return true;
    }

    /**
     * Register all of our events
     */
    boolean registerEvents() {
        getServer().getPluginManager().registerEvents(new PlayerInteractionListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new AfkEventListener(), this);
        return true;
    }

    /**
     * Get an online user
     *
     * @param uuid the UUID of the user to lookup
     *
     * @return The user if found, otherwise null
     */
    public User getOnlineUser(UUID uuid) {
        for (User user : getOnlineUserCache().values()) {
            if (user.getUniqueId().equals(uuid))
                return user;
        }
        return null;
    }
}
