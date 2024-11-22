package com.crumbed.crumbmmo.jsonUtils;

import com.crumbed.crumbmmo.ecs.components.EntityInventory;
import com.crumbed.crumbmmo.ecs.components.EntityStats;
import com.google.gson.annotations.SerializedName;
import de.tr7zw.nbtapi.iface.NBTHandler;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerData {
    public static final NBTHandler<PlayerData> HANDLER = new NBTHandler<>() {
        @Override
        public void set(@NotNull ReadWriteNBT nbt, @NotNull String name, @NotNull PlayerData data) {

        }

        @Override
        public PlayerData get(@NotNull ReadableNBT readableNBT, @NotNull String name) {
            var tag = readableNBT.getCompound(name);
            if (tag == null) return null;

            var uuid = UUID.fromString(tag.getString("uuid"));
            var playerName = tag.getString("name");
            var stats = tag.get("stats", EntityStats.HANDLER);
            // working on entityinventory parsing.
            // thinking about raw item pointers
            // does it make sense to just not save custom inventory state?
        }
    };

    @SerializedName("player-uuid")
    public UUID playerUUID;
    @SerializedName("player-name")
    public String playerName;
    public EntityStats stats;
    public EntityInventory inv;

    public PlayerData(
            UUID uuid,
            String name,
            EntityStats stats,
            EntityInventory inv
    ) {
        this.playerUUID = uuid;
        this.playerName = name;
        this.stats = stats;
        this.inv = inv;
    }
}
