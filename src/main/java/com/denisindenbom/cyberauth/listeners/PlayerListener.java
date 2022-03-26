package com.denisindenbom.cyberauth.listeners;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;

import org.bukkit.event.Listener;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;

import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.entity.EntityType;

import org.bukkit.event.inventory.InventoryClickEvent;

import org.bukkit.scheduler.BukkitRunnable;

import com.denisindenbom.cyberauth.CyberAuth;
import com.denisindenbom.cyberauth.formattext.FormatText;
import com.denisindenbom.cyberauth.messagesender.MessageSender;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PlayerListener implements Listener
{
    private final CyberAuth plugin;
    private final FileConfiguration messages;

    private final boolean kick;
    private final long authTime;

    private final List<String> VALID_COMMANDS = this.getCommandsList("/login ", "/l ", "/log ", "/register ", "/r ", "/reg ", "/change_password ");

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
        // start the timer on the kick
        if (this.kick) this.timerKick(event.getPlayer(), this.authTime);

        String message;

        if (this.plugin.getAuthDB().userExists(event.getPlayer().getName()))
            message = this.messages.getString("login.log_in");
        else message = this.messages.getString("registration.register_in");

        this.messageSender.sendMessage(event.getPlayer(), message);
    }

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent event)
    {
        // delete player from list of authorized players
        this.plugin.getAuthManager().removeUserByName(event.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerChat(@NotNull AsyncPlayerChatEvent event)
    {
        // check that player is authorized
        if (!userIsAuth(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerCommandPreprocess(@NotNull PlayerCommandPreprocessEvent event)
    {
        if (userIsAuth(event.getPlayer())) return;

        for (String validCommand : this.VALID_COMMANDS)
            if (event.getMessage().contains(validCommand)) return;

        this.messageSender.sendMessage(event.getPlayer(), this.messages.getString("error.not_logged_in"));
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(@NotNull PlayerInteractEvent event)
    {
        // check that player is authorized
        if (!userIsAuth(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(@NotNull PlayerMoveEvent event)
    {
        // check that player is authorized
        if (userIsAuth(event.getPlayer())) return;

        if(event.getTo() == null) return;

        // check that player move correctly
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
           event.getFrom().getBlockZ() == event.getTo().getBlockZ() &&
           event.getFrom().getBlockY() - event.getTo().getBlockY() >= 0) return;

        // canceled event
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerItemDamage(@NotNull PlayerItemDamageEvent event)
    {
        // check that player is authorized
        if (!userIsAuth(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    void onPlayerUseInventory(@NotNull InventoryClickEvent event)
    {
        if (!(event.getWhoClicked() instanceof Player)) return;

        // check that player is authorized
        if (!userIsAuth((Player) event.getWhoClicked())) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPickupItem(@NotNull EntityPickupItemEvent event)
    {
        if (!event.getEntity().getType().equals(EntityType.PLAYER)) return;

        Player player = (Player) event.getEntity();
        // check that player is authorized
        if (!userIsAuth(player)) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPickupArrow(@NotNull PlayerPickupArrowEvent event)
    {
        // check that player is authorized
        if (!userIsAuth(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(@NotNull PlayerDropItemEvent event)
    {
        // check that player is authorized
        if (!userIsAuth(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByPlayer(@NotNull EntityDamageByEntityEvent event)
    {
        if (!event.getDamager().getType().equals(EntityType.PLAYER)) return;

        Player damager = (Player) event.getDamager();
        // check that the damager is authorized
        if (!userIsAuth(damager)) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDamage(@NotNull EntityDamageEvent event)
    {
        if (!event.getEntityType().equals(EntityType.PLAYER)) return;

        if (!userIsAuth((Player) event.getEntity())) event.setCancelled(true);
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

        return this.plugin.getAuthManager().userExists(player.getName());
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

                    if (!plugin.getAuthManager().userExists(playerName))
                    {
                        if (plugin.getAuthDB().userExists(playerName))
                            messageSender.sendMessage(player, messages.getString("login.log_in"));
                        else messageSender.sendMessage(player, messages.getString("registration.register_in"));
                    }
                }
            }
        }.runTaskTimer(this.plugin, 10, 200);
    }

    private List<String> getCommandsList(String... commands)
    {
        return new ArrayList<>(Arrays.stream(commands).toList());
    }
}
