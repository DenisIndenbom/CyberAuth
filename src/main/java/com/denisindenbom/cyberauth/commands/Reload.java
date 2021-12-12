package com.denisindenbom.cyberauth.commands;

import com.denisindenbom.cyberauth.messagesender.MessageSender;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.denisindenbom.cyberauth.CyberAuth;
import org.jetbrains.annotations.NotNull;

public class Reload implements CommandExecutor
{
    private final CyberAuth plugin;
    private final MessageSender messageSender = new MessageSender();

    public Reload(CyberAuth plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args)
    {
        Player player = (Player) sender;
        if (!player.isOp())
        {
            this.messageSender.sendMessage(sender,"<c4>You don't have permissions!");
            return false;
        }

        plugin.reloadPlugin();

        return true;
    }
}
