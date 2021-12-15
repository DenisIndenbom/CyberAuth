package com.denisindenbom.cyberauth.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.denisindenbom.cyberauth.CyberAuth;
import com.denisindenbom.cyberauth.messagesender.MessageSender;

import org.jetbrains.annotations.NotNull;

public class Remove implements CommandExecutor
{
    private final CyberAuth plugin;
    private final MessageSender messageSender = new MessageSender();

    private final FileConfiguration messages;

    public Remove(CyberAuth plugin, FileConfiguration messages)
    {
        this.plugin = plugin;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args)
    {
        Player player = (Player) sender;

        if (!this.plugin.getAuthManager().userIs(player.getName()))
        {
            this.messageSender.sendMessage(sender, this.messages.getString("error.not_logged_in"));
            return true;
        }
        else if (!player.isOp())
        {
            this.messageSender.sendMessage(sender, this.messages.getString("error.permissions"));
            return true;
        }

        if (args.length < 1) return false;


        boolean result = this.plugin.getAuthDB().removeUser(args[0]);

        if (!result)
        {
            this.messageSender.sendMessage(sender, this.messages.getString("error.user_not_exist"), "{%username%}", args[0]);
            return true;
        }

        this.plugin.getAuthManager().removeUserByName(args[0]);
        this.messageSender.sendMessage(sender, this.messages.getString("remove_user.user_removed"), "{%username%}", player.getName());

        return true;
    }
}
