package com.denisindenbom.cyberauth;

import org.apache.logging.log4j.LogManager;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;

import com.denisindenbom.cyberauth.listeners.PlayerListener;

import com.denisindenbom.cyberauth.user.UserAuthManager;
import com.denisindenbom.cyberauth.database.CyberAuthDB;
import com.denisindenbom.cyberauth.passwordauth.PasswordAuth;
import com.denisindenbom.cyberauth.commands.*;

import com.denisindenbom.cyberauth.logfilter.ConsoleFilter;

import java.io.File;
import java.sql.SQLException;
import java.util.Objects;

import org.apache.logging.log4j.core.Logger;

public class CyberAuth extends JavaPlugin
{
    private FileConfiguration messages;

    private UserAuthManager authManager;
    private CyberAuthDB authDB;
    private PasswordAuth passwordAuth;

    private PlayerListener playerListener;

    @Override
    public void onLoad()
    {
        Logger logger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
        logger.addFilter(new ConsoleFilter());
    }

    @Override
    public void onEnable()
    {
        try
        {
            this.loadPlugin();
            this.getLogger().info("Plugin is enable!");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            this.getLogger().warning("CyberAuth is not enable! Plugin don't work! Please, check file config.yml!");
        }
    }

    @Override
    public void onDisable()
    {
        this.disablePlugin();
    }

    public void loadPlugin()
    {
        // save default configs
        this.saveDefaultConfig();
        this.saveDefaultMessages();

        this.loadMessages();
        // load db
        try
        {
            this.authDB = new CyberAuthDB(getDataFolder().getPath() + "/" + "CyberAuth.db");
            this.authDB.createDefaultDB();
        }
        catch (SQLException e)
        {
            getLogger().warning("Failed to load database! Please, check file config.yml or delete CyberAuth.db");
            return;
        }

        // init auth manager
        this.authManager = new UserAuthManager();

        // create password authentication with a hashing cost of 18
        this.passwordAuth = new PasswordAuth(18);

        long minPasswordLength = this.getConfig().getLong("min_password_length");
        long maxPasswordLength = this.getConfig().getLong("max_password_length");

        boolean kick = this.getConfig().getBoolean("kick");
        long authTime = this.getConfig().getLong("auth_time");

        // register commands executors
        Objects.requireNonNull(this.getCommand("login")).setExecutor(
                new Login(this, this.messages, this.getConfig().getBoolean("kick")));
        Objects.requireNonNull(this.getCommand("register")).setExecutor(
                new Register(this, this.messages, minPasswordLength, maxPasswordLength));
        Objects.requireNonNull(this.getCommand("change_password")).setExecutor(
                new ChangePassword(this, this.messages, minPasswordLength, maxPasswordLength));
        Objects.requireNonNull(this.getCommand("logout")).setExecutor(
                new Logout(this, kick, authTime));
        Objects.requireNonNull(this.getCommand("reload_cyberauth")).setExecutor(new Reload(this, this.messages));
        Objects.requireNonNull(this.getCommand("removeuser")).setExecutor(new Remove(this, this.messages));

        // create player listener
        this.playerListener = new PlayerListener(this, this.messages, kick, authTime);
        // register player listener
        this.getServer().getPluginManager().registerEvents(this.playerListener, this);
    }

    public void reloadPlugin()
    {
        // reload config
        this.reloadConfig();
        // disable plugin
        this.disablePlugin();
        // load plugin
        this.loadPlugin();
    }

    public UserAuthManager getAuthManager()
    {return this.authManager;}

    public PasswordAuth getPasswordAuth()
    {return this.passwordAuth;}

    public CyberAuthDB getAuthDB()
    {return this.authDB;}

    public PlayerListener getPlayerListener()
    {return this.playerListener;}

    private void disablePlugin()
    {
        try
        {
            // disable database
            this.authDB.disable();
        }
        catch (SQLException e)
        {
            getLogger().warning("Failed to close database connection!");
        }

        HandlerList.unregisterAll(this.playerListener);
        this.playerListener = null;
    }

    private void saveDefaultMessages()
    {
        File messagesFile = new File(getDataFolder(), "messages.yml");

        if (!messagesFile.exists()) saveResource("messages.yml", false);
    }

    private void loadMessages()
    {
        File messagesFile = new File(getDataFolder(), "messages.yml");

        this.messages = new YamlConfiguration();
        try
        {
            this.messages.load(messagesFile);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
