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

    private final boolean kick;
    private final long authTime;

    public Logout(CyberAuth plugin, boolean kick, long authTime)
    {
        this.plugin = plugin;

        this.kick = kick;
        this.authTime = authTime;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label,
                             String @NotNull [] args)
    {
        Player player = (Player) sender;

        this.plugin.getAuthManager().removeUserByName(player.getName());

        if (this.kick) this.plugin.getPlayerListener().timerKick(player, this.authTime);

        return true;
    }
}
