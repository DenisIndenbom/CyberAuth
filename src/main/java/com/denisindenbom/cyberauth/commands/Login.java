package com.denisindenbom.cyberauth.commands;

import org.bukkit.configuration.file.FileConfiguration;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

import com.denisindenbom.cyberauth.CyberAuth;
import com.denisindenbom.cyberauth.formattext.FormatText;
import com.denisindenbom.cyberauth.messagesender.MessageSender;

import com.denisindenbom.cyberauth.user.User;

import org.jetbrains.annotations.NotNull;

public class Login implements CommandExecutor
{
    private final CyberAuth plugin;

    private final MessageSender messageSender = new MessageSender();
    private final FormatText formatText = new FormatText();

    private final FileConfiguration messages;
    private final boolean kick;


    public Login(CyberAuth plugin, FileConfiguration messages, boolean kick)
    {
        this.plugin = plugin;
        this.messages = messages;
        this.kick = kick;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label,
                             String @NotNull [] args)
    {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (args.length == 0)
        {
            this.messageSender.sendMessage(sender, this.messages.getString("error.arguments"));
            return false;
        }
        if (!this.plugin.getAuthDB().userExists(player.getName()))
        {
            this.messageSender.sendMessage(sender, this.messages.getString("error.not_registered"));
            return true;
        }
        if (this.plugin.getAuthManager().userExists(player.getName()))
        {
            this.messageSender.sendMessage(sender, this.messages.getString("error.logged_in"));
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

        // we return the lost air during login
        player.setRemainingAir(player.getMaximumAir());

        return true;
    }
}
