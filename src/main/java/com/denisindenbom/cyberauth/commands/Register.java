package com.denisindenbom.cyberauth.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.denisindenbom.cyberauth.CyberAuth;
import com.denisindenbom.cyberauth.messagesender.MessageSender;
import com.denisindenbom.cyberauth.user.User;

import org.jetbrains.annotations.NotNull;


public class Register implements CommandExecutor
{
    private final CyberAuth plugin;

    private final MessageSender messageSender = new MessageSender();

    private final FileConfiguration messages;

    private final long minPasswordLength;
    private final long maxPasswordLength;

    public Register(CyberAuth plugin, FileConfiguration messages, long minPasswordLength, long maxPasswordLength)
    {
        this.plugin = plugin;

        this.messages = messages;

        this.minPasswordLength = minPasswordLength;
        this.maxPasswordLength = maxPasswordLength;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label,
                             String @NotNull [] args)
    {
        Player player = (Player) sender;
        // check for the presence of all arguments
        if (args.length < 2)
        {
            this.messageSender.sendMessage(sender, this.messages.getString("error.arguments"));
            return false;
        }
        // check that the passwords match
        if (!args[0].equals(args[1]))
        {
            this.messageSender.sendMessage(sender, this.messages.getString("error.set_password"));
            return false;
        }

        char[] password = args[0].toCharArray();

        // check password length
        if (password.length <= this.minPasswordLength)
        {
            this.messageSender.sendMessage(sender, this.messages.getString("error.short_password"), "{%min_password_length%}", ""+this.minPasswordLength);
            return true;
        }
        if (password.length >= this.maxPasswordLength)
        {
            this.messageSender.sendMessage(sender, this.messages.getString("error.long_password"), "{%max_password_length%}", ""+this.maxPasswordLength);
            return true;
        }

        // check that the user is not registered yet
        if (!this.plugin.getAuthDB().userExists(player.getName()))
        {
            String passwordHash = this.plugin.getPasswordAuth().hash(password);
            User user = new User(player.getName(), passwordHash);

            // add user
            boolean result = this.plugin.getAuthDB().addUser(user);

            if (result) this.messageSender.sendMessage(sender, this.messages.getString("registration.registered"));
            else this.messageSender.sendMessage(sender, this.messages.getString("error.registration"));
        }
        else this.messageSender.sendMessage(sender, this.messages.getString("error.registered"));

        return true;
    }
}
