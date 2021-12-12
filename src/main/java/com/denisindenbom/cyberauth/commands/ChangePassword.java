package com.denisindenbom.cyberauth.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.denisindenbom.cyberauth.CyberAuth;
import com.denisindenbom.cyberauth.messagesender.MessageSender;
import com.denisindenbom.cyberauth.user.User;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChangePassword implements CommandExecutor
{
    private final CyberAuth plugin;
    private final FileConfiguration messages;

    private final MessageSender messageSender = new MessageSender();

    private final long minPasswordLength;
    private final long maxPasswordLength;

    public ChangePassword(CyberAuth plugin, FileConfiguration messages, long minPasswordLength, long maxPasswordLength)
    {
        this.plugin = plugin;
        this.messages = messages;

        this.minPasswordLength = minPasswordLength;
        this.maxPasswordLength = maxPasswordLength;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label,
                             @NotNull String[] args)
    {
        // get player class
        Player player = (Player) sender;

        // check that the player is already logged in
        if (!this.plugin.getAuthManager().userIs(player.getName()))
        {
            this.messageSender.sendMessage(sender, this.messages.getString("error.not_logged_in"));
            return false;
        }
        if (args.length < 2)
        {
            this.messageSender.sendMessage(sender, this.messages.getString("error.arguments"));
            return false;
        }

        User user = this.plugin.getAuthDB().getUser(player.getName());

        char[] password = args[0].toCharArray();
        char[] newPassword = args[1].toCharArray();

        // check password length
        if (newPassword.length <= this.minPasswordLength)
        {
            this.messageSender.sendMessage(sender, this.messages.getString("error.short_password"), "{%min_password_length%}", ""+this.minPasswordLength);
            return true;
        }
        if (newPassword.length >= this.maxPasswordLength)
        {
            this.messageSender.sendMessage(sender, this.messages.getString("error.long_password"), "{%max_password_length%}", ""+this.maxPasswordLength);
            return true;
        }

        // check that the password is correct
        boolean result = this.plugin.getPasswordAuth().authenticate(password, user.getPasswordHash());

        if (!result)
        {
            // send message if password is wrong
            this.messageSender.sendMessage(sender, this.messages.getString("error.wrong_password"));
            return true;
        }

        String newPasswordHash = this.plugin.getPasswordAuth().hash(newPassword);

        // change password hash
        boolean changePasswordResult = this.plugin.getAuthDB().changePasswordHash(player.getName(), newPasswordHash);

        if (!changePasswordResult)
            this.messageSender.sendMessage(sender, this.messages.getString("error.change_password"));
        else this.messageSender.sendMessage(sender, "Password changed successfully!");

        return true;
    }
}
