package com.denisindenbom.cyberauth.listeners;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;

import org.bukkit.event.Listener;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.entity.EntityType;

import org.bukkit.scheduler.BukkitRunnable;

import com.denisindenbom.cyberauth.CyberAuth;
import com.denisindenbom.cyberauth.formattext.FormatText;
import com.denisindenbom.cyberauth.messagesender.MessageSender;

import org.jetbrains.annotations.NotNull;


public class PlayerListener implements Listener
{
    private final CyberAuth plugin;
    private final FileConfiguration messages;

    private final boolean kick;
    private final long authTime;

    private final MessageSender messageSender = new MessageSender();

    public PlayerListener(CyberAuth plugin, FileConfiguration messages, boolean kick, long authTime)
    {
        this.plugin = plugin;
        this.messages = messages;
        this.kick = kick;
        this.authTime = authTime;

        this.notification();
    }

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event)
    {
        if (this.kick) this.timerKick(event.getPlayer(), this.authTime);

        String message;

        if (this.plugin.getAuthDB().userIs(event.getPlayer().getName()))
            message = this.messages.getString("login.log_in");
        else message = this.messages.getString("registration.register_in");

        this.messageSender.sendMessage(event.getPlayer(), message);
    }

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent event)
    {
        this.plugin.getAuthManager().removeUserByName(event.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerInteract(@NotNull PlayerInteractEvent event)
    {
        if (!userIsAuth(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerChat(@NotNull PlayerChatEvent event)
    {
        if (!userIsAuth(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(@NotNull PlayerMoveEvent event)
    {
        if (!userIsAuth(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerItemDamage(@NotNull PlayerItemDamageEvent event)
    {
        if (!userIsAuth(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPickupItem(@NotNull EntityPickupItemEvent event)
    {
        if (event.getEntity().getType().equals(EntityType.PLAYER))
        {
            Player player = (Player) event.getEntity();

            if (!userIsAuth(player)) event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupArrow(@NotNull PlayerPickupArrowEvent event)
    {
        if (!userIsAuth(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(@NotNull PlayerDropItemEvent event)
    {
        if (!userIsAuth(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(@NotNull EntityDamageByEntityEvent event)
    {
        Player damager = null;
        Player injured = null;

        if (event.getDamager().getType().equals(EntityType.PLAYER)) damager = (Player) event.getDamager();
        if (event.getEntity().getType().equals(EntityType.PLAYER)) injured = (Player) event.getEntity();

        if (!userIsAuth(injured) || !userIsAuth(damager)) event.setCancelled(true);
    }

    public void timerKick(Player player, long delay)
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                FormatText formatText = new FormatText();
                String kickMessage = formatText.format(messages.getString("error.timeout"));

                if (!userIsAuth(player)) player.kickPlayer(kickMessage);
            }
        }.runTaskLater(this.plugin, delay);
    }

    private boolean userIsAuth(Player player)
    {
        if (player == null) return true;

        return this.plugin.getAuthManager().userIs(player.getName());
    }

    private void notification()
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for (Player player : plugin.getServer().getOnlinePlayers())
                {
                    String playerName = player.getName();

                    if (!plugin.getAuthManager().userIs(playerName))
                    {
                        if (plugin.getAuthDB().userIs(playerName))
                            messageSender.sendMessage(player, messages.getString("login.log_in"));
                        else messageSender.sendMessage(player, messages.getString("registration.register_in"));
                    }
                }
            }
        }.runTaskTimer(this.plugin, 10, 200);
    }
}
