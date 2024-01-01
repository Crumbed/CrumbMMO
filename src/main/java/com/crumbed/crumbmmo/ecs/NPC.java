package com.crumbed.crumbmmo.ecs;


import com.crumbed.crumbmmo.ecs.components.NpcComponent;
import com.crumbed.crumbmmo.ecs.components.RawEntity;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Location;


public class NPC extends CEntity {
    public ServerPlayer raw;
    public Location loc;
    public GameProfile profile;
    public NpcComponent flags;

    public NPC(ServerPlayer npc, Location loc, GameProfile profile) {
        super(new NpcComponent(false));
        raw = npc;
        this.loc = loc;
        this.profile = profile;
        flags = super.getComponent(NpcComponent.class).unwrap();
    }
}










