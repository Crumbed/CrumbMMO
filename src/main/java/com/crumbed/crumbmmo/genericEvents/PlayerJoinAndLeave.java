package com.crumbed.crumbmmo.genericEvents;

import com.crumbed.crumbmmo.CrumbMMO;
import com.crumbed.crumbmmo.managers.NpcManager;
import com.crumbed.crumbmmo.managers.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public record PlayerJoinAndLeave(CrumbMMO plugin) implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        var player = e.getPlayer();
        var pm = PlayerManager.INSTANCE;
        pm.addPlayer(player);
        NpcManager.INSTANCE.sendNpcs(player);
        plugin.getLogger().info("Added " + player.getName() + " to PlayerManager");
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        var player = e.getPlayer();
        var pm = PlayerManager.INSTANCE;
        pm.deloadPlayer(player);
    }
}
