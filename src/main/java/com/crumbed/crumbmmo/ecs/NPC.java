package com.crumbed.crumbmmo.ecs;


import com.crumbed.crumbmmo.ecs.components.NpcComponent;
import com.crumbed.crumbmmo.jsonUtils.NpcData;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;

import java.util.UUID;


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

    public NPC(NpcData data) {
        super(data.flags);
        var server = (MinecraftServer) ((CraftServer) Bukkit.getServer()).getServer();
        var world = ((CraftWorld) data.loc.getWorld()).getHandle();
        profile = new GameProfile(UUID.randomUUID(), data.name);
        profile.getProperties().get("textures").clear();
        profile.getProperties().put("textures",
                new Property("textures", data.flags.skinTexture, data.flags.skinSignature));
        var npc = new ServerPlayer(server, world, profile);
        npc.moveTo(data.loc.getX(), data.loc.getY(), data.loc.getZ(), data.loc.getYaw(), data.loc.getPitch());

        raw = npc;
        loc = data.loc;
        flags = data.flags;
    }

    public NpcData toNpcData(String id) {
        return new NpcData(
                id,
                raw.displayName,
                loc,
                flags
        );
    }
}





































