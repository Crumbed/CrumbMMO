package com.crumbed.crumbmmo.genericEvents;

import com.crumbed.crumbmmo.CrumbMMO;
import com.crumbed.crumbmmo.managers.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinAndLeave implements Listener {
    private CrumbMMO plugin;
    public PlayerJoinAndLeave(CrumbMMO plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        PlayerManager pm = PlayerManager
                .INSTANCE
                .unwrap();
        if (pm.getPlayer(player).isSome()) { pm.getPlayer(player).unwrap().initLoaded(); return; }
        pm.addPlayer(player);
        plugin.getLogger().info("Added " + player.getName() + " to PlayerManager");
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        PlayerManager pm = PlayerManager
                .INSTANCE
                .unwrap();
        pm.deloadPlayer(player);
    }
}
