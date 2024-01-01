package com.crumbed.crumbmmo.managers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.crumbed.crumbmmo.CrumbMMO;
import com.crumbed.crumbmmo.commands.TabComponent;
import com.crumbed.crumbmmo.ecs.CEntity;
import com.crumbed.crumbmmo.ecs.NPC;
import com.crumbed.crumbmmo.utils.Option;
import com.crumbed.crumbmmo.utils.Some;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.util.UUIDTypeAdapter;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.metadata.EntityMetadataStore;
import org.bukkit.entity.Player;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public class NpcManager implements TabComponent.Source {
    public static NpcManager INSTANCE = null;

    public HashMap<String, Integer> npcs;

    public NpcManager() {
        npcs = new HashMap<>();
    }


    public void createNpc(String name, Location l) {
        var server = (MinecraftServer) ((CraftServer) Bukkit.getServer()).getServer();
        var world = ((CraftWorld) l.getWorld()).getHandle();
        var profile = new GameProfile(UUID.randomUUID(), name);
        var npc = new ServerPlayer(server, world, profile);
        npc.moveTo(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
        addPacket(npc);

        var cnpc = new NPC(npc, l, profile);
        EntityManager.INSTANCE.addEntity(cnpc);
        npcs.put(name, cnpc.id);
    }

    public void addPacket(ServerPlayer npc) {
        var dw = npc.getEntityData();
        var data = new EntityDataAccessor<>(17, EntityDataSerializers.BYTE);
        dw.set(data, (byte) 127);

        PlayerManager.INSTANCE.getPlayers().forEach(p -> {
            var con = ((CraftPlayer) p.rawPlayer).getHandle().connection;
            con.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, npc));
            con.send(new ClientboundAddPlayerPacket(npc));
            con.send(new ClientboundRotateHeadPacket(npc, (byte) (npc.getBukkitYaw() * 256 / 360)));
            con.send(new ClientboundSetEntityDataPacket(npc.getId(), dw.packDirty()));
        });
    }

    public void sendNpcs(Player p) {
        for (var id : npcs.values()) {
            var npc = ((NPC) EntityManager.INSTANCE
                    .getEntity(id)
                    .unwrap())
                    .raw;
            var dw = npc.getEntityData();
            var data = new EntityDataAccessor<>(17, EntityDataSerializers.BYTE);
            dw.set(data, (byte) 127);

            var con = ((CraftPlayer) p).getHandle().connection;
            con.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, npc));
            con.send(new ClientboundAddPlayerPacket(npc));
            con.send(new ClientboundRotateHeadPacket(npc, (byte) (npc.getBukkitYaw() * 256 / 360)));
            con.send(new ClientboundSetEntityDataPacket(npc.getId(), dw.packDirty()));
        }
    }

    public Option<NPC> get(String id) {
        var npcId = npcs.get(id);
        if (npcId == null) return Option.none();
        if (!(EntityManager.INSTANCE.getEntity(npcId) instanceof Some<CEntity> ent))
            return Option.none();
        if (!(ent.inner() instanceof NPC npc))
            return Option.none();

        return Option.some(npc);
    }

    public boolean setLocation(String npcId, Location loc) {
        if (!(get(npcId) instanceof Some<NPC> s)) return false;
        var npc = s.inner();

        npc.raw.setPos(loc.getX(), loc.getY(), loc.getZ());
        npc.loc = loc;
        final var protocol = CrumbMMO.getProtocol();
        var packet = protocol.createPacket(PacketType.Play.Server.ENTITY_TELEPORT);
        packet.getIntegers().write(0, npc.raw.getId());
        packet.getDoubles()
                .write(0, loc.getX())
                .write(1, loc.getY())
                .write(2, loc.getZ());
        packet.getBooleans().write(0, true);
        PlayerManager.INSTANCE.getPlayers().forEach(p -> {
            protocol.sendServerPacket(p.rawPlayer, packet);
        });
        return true;
    }

    public boolean setId(String currentId, String newId) {
        if (!(get(currentId) instanceof Some<NPC> s) || npcs.containsKey(newId)) return false;
        var npc = s.inner();

        npcs.remove(currentId);
        npcs.put(newId, npc.id);
        return true;
    }

    public boolean setName(String npcId, String newName) {
        if (!(get(npcId) instanceof Some<NPC> s)) return false;
        var npc = s.inner();

        var profileClass = GameProfile.class;
        try {
            var field = profileClass.getDeclaredField("name");
            field.setAccessible(true);
            field.set(npc.profile, newName);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }

        var p_info = new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, npc.raw);
        var p_add = new ClientboundAddPlayerPacket(npc.raw);
        var p_rotate = new ClientboundRotateHeadPacket(npc.raw, (byte) (npc.raw.getBukkitYaw() * 256 / 360));
        PlayerManager.INSTANCE.getPlayers().forEach(p -> {
            var con = ((CraftPlayer) p.rawPlayer).getHandle().connection;
            con.send(p_info);
            con.send(p_add);
            con.send(p_rotate);
        });
        return true;
    }

    public boolean setSkin(String npcId, String uuid) {
        if (!(get(npcId) instanceof Some<NPC> s)) return false;
        var npc = s.inner();

        try {
            var connection = (HttpsURLConnection) new URL(String.format("https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false", uuid)).openConnection();
            if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                var reply = String.join("\n", new BufferedReader(new InputStreamReader(connection.getInputStream())).lines().toList().toArray(String[]::new));
                Bukkit.getLogger().info(reply);
                var jsonParser = new JsonParser();
                var gson = new Gson();
                var profile = (JsonObject) jsonParser.parse(reply);

                var texturesJson = profile.get("properties").getAsJsonArray().get(0);
                var textures = gson.fromJson(texturesJson, Property.class);

                npc.flags.skinTexture = textures.getValue();
                npc.flags.skinSignature = textures.getSignature();
                npc.profile.getProperties().put("textures", textures);
                return true;
            } else {
                System.out.println("Connection could not be opened (Response code " + connection.getResponseCode() + ", " + connection.getResponseMessage() + ")");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean setFlag(String npcId, String flag, boolean value) {
        if (!(get(npcId) instanceof Some<NPC> s)) return false;
        var npc = s.inner();

        switch (flag) {
            case "always_look" -> npc.flags.lookAtPlayer = value;
        }

        return true;
    }


    public Stream<NPC> getNpcs() {
        return npcs
                .values()
                .stream()
                .map(x -> (NPC) EntityManager.INSTANCE.getEntity(x).unwrap());
    }


    @Override
    public String[] getTabSource() {
        return npcs.keySet().toArray(new String[0]);
    }
}
























