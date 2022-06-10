package com.denisindenbom.cyberauth;

import org.apache.logging.log4j.LogManager;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;

import com.denisindenbom.cyberauth.listeners.PlayerListener;

import com.denisindenbom.cyberauth.managers.UserAuthManager;
import com.denisindenbom.cyberauth.database.CyberAuthDB;
import com.denisindenbom.cyberauth.passwordauth.PasswordAuth;
import com.denisindenbom.cyberauth.commands.*;

import com.denisindenbom.cyberauth.logfilter.ConsoleFilter;

import java.io.File;
import java.sql.SQLException;

import org.apache.logging.log4j.core.Logger;

public class CyberAuth extends JavaPlugin
{
    private FileConfiguration messagesConfig;

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
            this.getLogger().warning("CyberAuth is not running! Plugin don't work! Please, check file config.yml!");
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
        this.saveDefaultMessagesConfig();

        this.loadMessagesConfig();
        // load db
        try
        {
            this.authDB = new CyberAuthDB(this.getDataFolder().getPath() + "/" + "CyberAuth.db");
            this.authDB.createDefaultDB();
        }
        catch (SQLException e)
        {
            this.getLogger().warning("Failed to load database! Please, check file config.yml or delete CyberAuth.db");
            return;
        }

        // init auth manager
        this.authManager = new UserAuthManager();

        // create password authentication with a hashing cost of 18
        this.passwordAuth = new PasswordAuth(18);

        long minPasswordLength = this.getConfig().getLong("min_password_length");
        long maxPasswordLength = this.getConfig().getLong("max_password_length");

        boolean kick = this.getConfig().getBoolean("kick_for_wrong_password");
        long authTime = this.getConfig().getLong("auth_time");

        // register commands executors
        this.getCommand("login").setExecutor(new Login(this, kick));
        this.getCommand("register").setExecutor(new Register(this, minPasswordLength, maxPasswordLength));
        this.getCommand("change_password").setExecutor(new ChangePassword(this, minPasswordLength, maxPasswordLength));
        this.getCommand("logout").setExecutor(new Logout(this, authTime));
        this.getCommand("reload_cyberauth").setExecutor(new Reload(this));
        this.getCommand("remove_user").setExecutor(new Remove(this));

        // create player listener
        this.playerListener = new PlayerListener(this, authTime);
        // register player listener
        this.getServer().getPluginManager().registerEvents(this.playerListener, this);
    }

    private void disablePlugin()
    {
        try
        {
            // disable database
            this.authDB.disable();
        }
        catch (SQLException e)
        {
            this.getLogger().warning("Failed to close database connection!");
        }

        HandlerList.unregisterAll(this.playerListener);
        this.playerListener = null;
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

    public FileConfiguration getMessagesConfig()
    {return this.messagesConfig;}

    private void saveDefaultMessagesConfig()
    {
        File messagesFile = new File(getDataFolder(), "messages.yml");

        if (!messagesFile.exists()) saveResource("messages.yml", false);
    }

    private void loadMessagesConfig()
    {
        File messagesFile = new File(getDataFolder(), "messages.yml");

        this.messagesConfig = new YamlConfiguration();
        try
        {
            this.messagesConfig.load(messagesFile);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
