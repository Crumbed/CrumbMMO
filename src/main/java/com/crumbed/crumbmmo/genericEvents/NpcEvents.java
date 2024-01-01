package com.crumbed.crumbmmo.genericEvents;

import com.crumbed.crumbmmo.managers.NpcManager;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class NpcEvents implements Listener {




    @EventHandler
    public void followPlayer(PlayerMoveEvent e) {
        var p = e.getPlayer();
        var loc = p.getLocation();
        NpcManager.INSTANCE.getNpcs().forEach(npc -> {
            var npcLoc = npc.loc;
            if (!npc.flags.lookAtPlayer) return;

            if (loc.getX() > npcLoc.getX() + 5
                    || loc.getX() < npcLoc.getX() - 5
                    || loc.getZ() > npcLoc.getZ() + 5
                    || loc.getZ() < npcLoc.getZ() - 5
            ) return;

            npcLoc.setDirection(loc.subtract(npcLoc).toVector());
            float yaw = npcLoc.getYaw();
            float pitch = npcLoc.getPitch();

            var con = ((CraftPlayer) p).getHandle().connection;
            con.send(new ClientboundRotateHeadPacket(
                    npc.raw,
                    (byte) ((yaw % 360) * 256 / 360)
            ));
            con.send(new ClientboundMoveEntityPacket.Rot(
                    npc.raw.getId(),
                    (byte) ((yaw % 360) * 256 / 360),
                    (byte) ((pitch % 360) * 256 / 360),
                    false
            ));
        });
    }
}
