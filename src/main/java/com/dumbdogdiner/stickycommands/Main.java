package com.dumbdogdiner.stickycommands; // package owo

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.dumbdogdiner.stickycommands.commands.Jump;
import com.dumbdogdiner.stickycommands.commands.Kill;
import com.dumbdogdiner.stickycommands.utils.Database;
import com.ristexsoftware.knappy.Knappy;
import com.ristexsoftware.knappy.bukkit.command.AsyncCommand;
import com.ristexsoftware.knappy.cache.Cache;
import com.ristexsoftware.knappy.translation.LocaleProvider;
import com.ristexsoftware.knappy.util.ReflectionUtil;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin {

    /**
     * The singleton instance of the plugin.
     */
    @Getter
    private static Main instance;

    /**
     * Thread pool for the execution of asynchronous tasks.
     */
    @Getter
    private ExecutorService pool = Executors.newFixedThreadPool(3);

    /**
     * Cache of all online users.
     */
    @Getter
    private Cache<User> onlineUserCache = new Cache<>(User.class);

    /**
     * The current vault economy instance.
     */
    @Getter
    private Economy economy = null;

    @Getter
    private LocaleProvider localeProvider;

    /**
     * The database connected
     */
    @Getter
    Database database;

    List<Command> commandList = new ArrayList<Command>();

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        if (!setupConfig())
        return;
        
        if (!setupLocale())
        return;

        if (!setupEconomy())
            getLogger().severe("Disabled economy commands due to no Vault dependency found!");

        // this.database = new Database();
        // database.createMissingTables();

        // Set our thread pool
        Knappy.setPool(pool);

        // Register currently online users - in case of a reload.
        // (stop reloading spigot, please.)
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.onlineUserCache.put(User.fromPlayer(player));
        }

        if (!registerCommands())
            return;

        getLogger().info("StickyCommands started successfully!");
    }

    @Override
    public void onDisable() {
        saveConfig(); // Save our config
        // database.terminate(); // Terminate our database connection
    }

    /**
     * Setup the vault economy instance.
     */
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        
        this.economy = rsp.getProvider();
        return economy != null;
    }

    
    /**
     * Setup the configuration files
     */
    boolean setupConfig() {
        // Creating config folder, and adding config to it.
        if (!this.getDataFolder().exists()) {
            // Sticky~
            getLogger().severe("Error: No folder for StickyCommands was found! Creating...");
            this.getDataFolder().mkdirs();
            this.saveDefaultConfig();
            getLogger().severe("Please configure StickyCommands and restart the server! :)");
            return false;
        }
        
        if (!(new File(this.getDataFolder(), "config.yml").exists())) {
            this.saveDefaultConfig();
            getLogger().severe("Please configure StickyCommands and restart the server! :)");
            // They're not gonna have their database setup, just exit. It stops us from
            // having errors.
            return false;
        }
        return true;
    }
    
    /**
     * Setup the locale provider and default variables
     */
    boolean setupLocale() {
        this.localeProvider = new LocaleProvider(new File(getDataFolder(), "locale"));
        
        int loadedLocales = this.localeProvider.loadAllLocales();
        Boolean localeEnabled = this.localeProvider.setDefaultLocale("messages.en_us");
        
        if (!localeEnabled) {
            getLogger()
            .severe("Failed to configure default locale file - perhaps you deleted it? Will create a new one.");
            // This is horrible and needs to be improved
            try {
                this.localeProvider.writeLocaleStream(getResource("messages.en_us.yml"), "messages.en_us.yml", true);
            } catch (Exception e) {
                e.printStackTrace();
                getLogger().severe("Something went horribly wrong while saving the default locale.");
                return false;
            }
            
            this.localeProvider.loadAllLocales();
            this.localeProvider.setDefaultLocale("messages.en_us");
        } else
        getLogger().info("Loaded " + String.valueOf(loadedLocales) + " localizations");
        
        this.localeProvider.registerDefaultTranslation("prefix", "prefix", "[dddMC]");
        this.localeProvider.registerDefaultTranslation("network-name", "networkName", "Dumb Dog Diner");
        this.localeProvider.registerDefaultTranslation("website", "website", "dumbdogdiner.com");
        this.localeProvider.registerDefaultTranslation("server-error", "serverError", "The server encountered an error!");
        this.localeProvider.registerDefaultTranslation("invalid-syntax", "invalidSyntax", "&cInvalid Syntax!");
        
        return true;
    }

    /**
     * Register all the commands!
     */
    boolean registerCommands() {
        // Register economy based commands only if the economy provider is not null.
        if (economy != null) {

        }
        commandList.add(new Kill(this));
        commandList.add(new Jump(this));

        CommandMap cmap = ReflectionUtil.getProtectedValue(Bukkit.getServer(), "commandMap");
        cmap.registerAll(this.getName().toLowerCase(), commandList);
        return true;
    }

    // Before you get mad, just remember this knob named md_5 couldn't help but make Bukkit the worst Minecraft API
    // and while making it, didn't add a way of getting the server's TPS without NMS or reflection.
    // Special thanks to this guy who saved me all of 5 minutes! https://gist.github.com/vemacs/6a345b2f9822b79a9a7f
    
    private static Object minecraftServer;
    private static Field recentTps; 

    /**
     * Get the server's recent TPS
     * @return {@link java.lang.Double} The server TPS in the last 15 minutes (1m, 5m, 15m)
     */
    public double[] getRecentTps() {
        try {
            if (minecraftServer == null) {
                Server server = Bukkit.getServer();
                Field consoleField = server.getClass().getDeclaredField("console");
                consoleField.setAccessible(true);
                minecraftServer = consoleField.get(server);
            }
            if (recentTps == null) {
                recentTps = minecraftServer.getClass().getSuperclass().getDeclaredField("recentTps");
                recentTps.setAccessible(true);
            }
            return (double[]) recentTps.get(minecraftServer);
        } catch (IllegalAccessException | NoSuchFieldException ignored) {
        }
        return new double[] {0, 0, 0}; // If there's an issue, let's make it known.
    }
}
