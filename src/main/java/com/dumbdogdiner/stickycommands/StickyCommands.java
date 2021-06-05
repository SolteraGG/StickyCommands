package com.dumbdogdiner.stickycommands;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import com.dumbdogdiner.stickycommands.commands.*;
import com.dumbdogdiner.stickycommands.database.PostgresHandler;
import com.dumbdogdiner.stickycommands.managers.MedallionManager;
import com.dumbdogdiner.stickycommands.objects.Market;
import com.dumbdogdiner.stickycommands.listeners.PlayerInteractionListener;
import com.dumbdogdiner.stickycommands.listeners.PlayerJoinListener;
import com.dumbdogdiner.stickycommands.runnables.AfkTimeRunnable;
import com.dumbdogdiner.stickycommands.listeners.AfkEventListener;
import com.dumbdogdiner.stickyapi.StickyAPI;
import com.dumbdogdiner.stickyapi.bukkit.util.StartupUtil;
import com.dumbdogdiner.stickyapi.common.translation.LocaleProvider;
import com.dumbdogdiner.stickyapi.common.util.TimeUtil;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import kr.entree.spigradle.annotations.PluginMain;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import static dev.jorel.commandapi.CommandAPI.registerCommand;
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
    public static StickyCommands getInstance() {
        return StickyCommands.getPlugin(StickyCommands.class);
    }

    private Logger logger;

    @Getter
    protected static Boolean enabled = false;

    @Getter
    private boolean staffFacilitiesEnabled;


    /**
     * Thread pool for the execution of asynchronous tasks.
     */
    @Getter
    protected ExecutorService pool = Executors.newFixedThreadPool(3);

    /**
     * Cache of all online users.
     */
    @Getter
    protected static Map<UUID, User> onlineUserCache = new HashMap<>();


    /**
     * AFK TimerTask that tracks how long a player has been AFK
     */
    @Getter
    protected Timer afkRunnable = new Timer();


    /**
     * The server's uptime in seconds
     */
    @Getter
    protected Long upTime = TimeUtil.getUnixTime();

    /**
     * The current vault com.dumbdogdiner.stickycommands.economy instance.
     */
    @Getter
    private Economy economy = null;

    @Getter
    private LocaleProvider localeProvider;


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
        logger = super.getLogger();
        // Set our thread pool
        StickyAPI.setPool(pool);
        // onlineUserCache.setMaxSize(1000);
        CommandAPIConfig commandAPIConfig = new CommandAPIConfig();
        commandAPIConfig.setVerboseOutput(true);
        CommandAPI.onLoad(commandAPIConfig);
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

        // Setup commandAPI
        CommandAPI.onEnable(this);

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
        if(!CommandAPI.canRegister()){
            logger.severe("Cannot register commands, wtf??");
            return false;
        }
        registerCommand(AfkCommand.class);
        registerCommand(HatCommand.class);
        registerCommand(JumpCommand.class);
//        registerCommand(KillCommand.class);
        registerCommand(MainCommand.class);
        registerCommand(MakeANoteForMeToFixItLater.class);
        registerCommand(MemoryCommand.class);
        registerCommand(PowerToolCommand.class);
        registerCommand(RulebookCommand.class);
        registerCommand(WorthCommand.class);
        registerCommand(HatCommand.class);
        registerCommand(SellCommand.class);
        SmiteCommand.register();
        return true;
    }

    /**
     * Register all of our events
     */
    boolean registerEvents() {
        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new PlayerInteractionListener(), this);
        manager.registerEvents(new PlayerJoinListener(), this);
        manager.registerEvents(new AfkEventListener(), this);
        manager.registerEvents(new MedallionManager(this), this);
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
