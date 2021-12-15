package com.denisindenbom.cyberauth.commands;

import com.denisindenbom.cyberauth.messagesender.MessageSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.denisindenbom.cyberauth.CyberAuth;
import org.jetbrains.annotations.NotNull;

public class Reload implements CommandExecutor
{
    private final CyberAuth plugin;
    private final MessageSender messageSender = new MessageSender();

    private final FileConfiguration messages;

    public Reload(CyberAuth plugin, FileConfiguration messages)
    {
        this.plugin = plugin;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args)
    {
        Player player = (Player) sender;
        if (!player.isOp())
        {
            this.messageSender.sendMessage(sender, this.messages.getString("error.permissions"));
            return true;
        }

        plugin.reloadPlugin();

        return true;
    }
}
