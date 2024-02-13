package com.crumbed.crumbmmo.managers;

import com.comphenix.protocol.PacketType;
import com.crumbed.crumbmmo.CrumbMMO;
import com.crumbed.crumbmmo.commands.BrigadierCommand;
import com.crumbed.crumbmmo.ecs.CEntity;
import com.crumbed.crumbmmo.ecs.NPC;
import com.crumbed.crumbmmo.jsonUtils.NpcAdapter;
import com.crumbed.crumbmmo.jsonUtils.NpcData;
import com.crumbed.crumbmmo.utils.None;
import com.crumbed.crumbmmo.utils.Option;
import com.crumbed.crumbmmo.utils.Some;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NpcManager {
    public static NpcManager INSTANCE = null;

    @SerializedName("json-npcs")
    private ArrayList<NpcData> jsonNpcs;
    transient public HashMap<String, Integer> npcs;

    public NpcManager() {
        jsonNpcs = new ArrayList<>();
        npcs = new HashMap<>();
    }


    public static NpcManager loadNpcs(CrumbMMO plugin) {
        var f = new File(plugin.getDataFolder(), "Npcs.json");

        if (!f.exists()) try {
            f.createNewFile();
            return new NpcManager();
        } catch (IOException ignored){}
        else try (Stream<String> lines = Files.lines(f.toPath())) {
            var jsonNpcs = String.join("\n", lines
                    .collect(Collectors.toList()));
            var gson = new GsonBuilder()
                    .registerTypeAdapter(NpcData.class, new NpcAdapter())
                    .create();
            var manager = gson.fromJson(jsonNpcs, NpcManager.class);
            manager.jsonNpcs.stream().map(NPC::new).forEach(x -> {
                EntityManager.INSTANCE.addEntity(x);
                manager.npcs.put(x.raw.displayName, x.id);
            });

            return manager;
        } catch (IOException ignored){}
        return null;
    }

    public void saveNpcs(CrumbMMO plugin) {
        try {
            var f = new FileWriter(new File(plugin.getDataFolder(), "Npcs.json"));
            var gson = new GsonBuilder()
                    .registerTypeAdapter(NpcData.class, new NpcAdapter())
                    .setPrettyPrinting()
                    .create();
            jsonNpcs = this.npcs.entrySet()
                    .stream()
                    .map(x -> ((NPC) EntityManager.INSTANCE.getEntity(x.getValue()).unwrap()).toNpcData(x.getKey()))
                    .collect(Collectors.toCollection(ArrayList::new));

            f.write(gson.toJson(this));
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        var dataItem = new SynchedEntityData.DataItem<>(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) 127);
        PlayerManager.INSTANCE.getPlayers().forEach(p -> {
            var con = ((CraftPlayer) p.rawPlayer).getHandle().connection;
            con.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, npc));
            con.send(new ClientboundAddPlayerPacket(npc));
            con.send(new ClientboundRotateHeadPacket(npc, (byte) (npc.getBukkitYaw() * 256 / 360)));
            con.send(new ClientboundSetEntityDataPacket(npc.getId(), List.of(dataItem.value())));
        });
    }

    public void sendNpcs(Player p) {
        var dataItem = new SynchedEntityData.DataItem<>(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) 127);
        for (var id : npcs.values()) {
            var npc = ((NPC) EntityManager.INSTANCE
                    .getEntity(id)
                    .unwrap())
                    .raw;

            var con = ((CraftPlayer) p).getHandle().connection;
            con.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, npc));
            con.send(new ClientboundAddPlayerPacket(npc));
            con.send(new ClientboundRotateHeadPacket(npc, (byte) (npc.getBukkitYaw() * 256 / 360)));
            con.send(new ClientboundSetEntityDataPacket(npc.getId(), List.of(dataItem.value())));
        }
    }


    private Option<String[]> getSkin(String name) {
        try {
            var gson = new GsonBuilder().setPrettyPrinting().create();
            var url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            var reader = new InputStreamReader(url.openStream());
            var uuid = gson.fromJson(reader, JsonObject.class).get("id").getAsString();

            var url2 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            var reader2 = new InputStreamReader(url2.openStream());
            var props = gson.fromJson(reader2, JsonObject.class)
                    .get("properties")
                    .getAsJsonArray()
                    .get(0)
                    .getAsJsonObject();
            var texture = props.get("value").getAsString();
            var signature = props.get("signature").getAsString();

            return Option.some(new String[] { texture, signature });
        } catch (IOException ignored) {
            return Option.none();
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

    public String setLocation(String npcId, Location loc) {
        if (!(get(npcId) instanceof Some<NPC> s)) {
            return ChatColor.RED + "Error: could not find NPC at id: " + npcId;
        }
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
        return ChatColor.GREEN + "Successfully moved " + npcId + " to " + loc;
    }

    public String setId(String currentId, String newId) {
        if (!(get(currentId) instanceof Some<NPC> s) || npcs.containsKey(newId)) {
            return ChatColor.RED + "Error: could not find NPC at id: " + currentId;
        }
        var npc = s.inner();

        npcs.remove(currentId);
        npcs.put(newId, npc.id);
        return ChatColor.GREEN + "Successfully changed id of " + currentId + " to " + newId;
    }

    public String setName(String npcId, String newName) {
        if (!(get(npcId) instanceof Some<NPC> s)) {
            return ChatColor.RED + "Error: could not find NPC at id: " + npcId;
        }
        var npc = s.inner();

        final var profileClass = GameProfile.class;
        try {
            var field = profileClass.getDeclaredField("name");
            field.setAccessible(true);
            field.set(npc.profile, newName);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return ChatColor.RED + "FATAL ERROR: I have no clue how this happened, please report to staff";
        }

        npc.raw.displayName = newName;

        return ChatColor.GREEN + "Successfully set name of " + npcId + " to " + newName;
    }

    public String setSkin(String npcId, String name) {
        if (!(get(npcId) instanceof Some<NPC> s)) {
            return ChatColor.RED + "Error: could not find NPC at id: " + npcId;
        }
        var npc = s.inner();
        var skin = switch (getSkin(name)) {
            case Some<String[]> someSkin -> someSkin.inner();
            case None<String[]> ignored -> null;
        };
        if (skin == null) {
            return ChatColor.RED + "Error: failed to fetch skin of player " + name;
        }


        npc.flags.skinTexture = skin[0];
        npc.flags.skinSignature = skin[1];
        npc.raw.getGameProfile().getProperties().get("textures").clear();
        npc.raw.getGameProfile().getProperties().put("textures", new Property("textures", skin[0], skin[1]));
        return ChatColor.GREEN + "Successfully set skin of " + npcId + " to that of " + name;
    }


    public String setFlag(String npcId, String flag, boolean value) {
        if (!(get(npcId) instanceof Some<NPC> s)) {
            return ChatColor.RED + "Error: could not find NPC at id: " + npcId;
        }
        var npc = s.inner();

        switch (flag) {
            case "always_look" -> npc.flags.lookAtPlayer = value;
            default -> {
                return ChatColor.RED + "Error: unknown flag: " + flag;
            }
        }

        return ChatColor.GREEN + "Successfully changed set " + flag + " to " + value + " for " + npcId;
    }


    public Stream<NPC> getNpcs() {
        return npcs
                .values()
                .stream()
                .map(x -> (NPC) EntityManager.INSTANCE.getEntity(x).unwrap());
    }



    public static CompletableFuture<Suggestions> suggest(CommandContext<CommandSourceStack> c, SuggestionsBuilder builder) {
        NpcManager.INSTANCE.npcs
            .keySet()
            .stream()
            .filter(x -> x.startsWith(c.getArgument(BrigadierCommand.Params.Npc.name(), String.class)))
            .forEach(builder::suggest);

        return builder.buildFuture();
    }
}
























