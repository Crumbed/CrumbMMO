package com.crumbed.crumbmmo.serializable;

import com.crumbed.crumbmmo.ecs.components.EntityInventory;
import com.crumbed.crumbmmo.ecs.components.EntityStats;
import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class PlayerData {
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
