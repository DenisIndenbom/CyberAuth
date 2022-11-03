package com.denisindenbom.cyberauth.commands;

import org.bukkit.configuration.file.FileConfiguration;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

import com.denisindenbom.cyberauth.CyberAuth;
import com.denisindenbom.cyberauth.utils.FormatText;
import com.denisindenbom.cyberauth.utils.MessageSender;

import com.denisindenbom.cyberauth.user.User;

import org.jetbrains.annotations.NotNull;

public class Login implements CommandExecutor
{
    private final CyberAuth plugin;
    private final FileConfiguration messages;
    private final boolean kick;

    private final MessageSender messageSender = new MessageSender();
    private final FormatText formatText = new FormatText();

    public Login(CyberAuth plugin, boolean kick)
    {
        this.plugin = plugin;
        this.kick = kick;

        this.messages = this.plugin.getMessagesConfig();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label,
                             String @NotNull [] args)
    {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (this.plugin.getAuthManager().userExists(player.getName()))
        {
            this.messageSender.sendMessage(sender, this.messages.getString("error.logged_in"));
            return true;
        }
        if (!this.plugin.getAuthDB().userExists(player.getName()))
        {
            this.messageSender.sendMessage(sender, this.messages.getString("error.not_registered"));
            return true;
        }
        if (args.length == 0)
        {
            this.messageSender.sendMessage(sender, this.messages.getString("error.arguments"));
            return false;
        }

        User user = this.plugin.getAuthDB().getUser(player.getName());

        char[] password = args[0].toCharArray();

        boolean result = this.plugin.getPasswordAuth().authenticate(password, user.getPasswordHash());

        if (!result)
        {
            String wrongPassword = this.messages.getString("error.wrong_password");

            this.messageSender.sendMessage(sender, wrongPassword);
            if (this.kick) player.kickPlayer(this.formatText.format(wrongPassword));

            return true;
        }

        // adding a user to the list of registered
        this.plugin.getAuthManager().addUser(user);
        // send message
        this.messageSender.sendMessage(sender, this.messages.getString("login.logged_in"));
        this.messageSender.sendMessage(sender, this.messages.getString("welcome"), "{%username%}", user.getName());

        // log the user's login to the console
        this.plugin.getLogger().info(player.getName() + " logged in!");

        return true;
    }
}
