package com.crumbed.crumbmmo.genericEvents;

import com.crumbed.crumbmmo.ecs.CPlayer;
import com.crumbed.crumbmmo.managers.PlayerManager;
import com.crumbed.crumbmmo.utils.Some;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawn implements Listener {


    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        var optPlayer = PlayerManager.INSTANCE
                .getPlayer(e.getPlayer());
        if (!(optPlayer instanceof Some<CPlayer> some)) return;
        var player = some.inner();

        player.getStats().health.setValue(player.getStats().health.getBaseValue());
    }

}
