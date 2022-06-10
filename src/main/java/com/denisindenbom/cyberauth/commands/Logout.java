package com.denisindenbom.cyberauth.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.denisindenbom.cyberauth.CyberAuth;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Logout implements CommandExecutor
{
    private final CyberAuth plugin;

    private final long authTime;

    public Logout(CyberAuth plugin, long authTime)
    {
        this.plugin = plugin;

        this.authTime = authTime;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label,
                             String @NotNull [] args)
    {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        this.plugin.getAuthManager().removeUserByName(player.getName());

        this.plugin.getPlayerListener().kickTimer(player, this.authTime);

        return true;
    }
}
