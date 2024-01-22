package com.crumbed.crumbmmo.jsonUtils;

import com.crumbed.crumbmmo.ecs.CPlayer;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.UUID;

public class JsonPlayerData {
    @SerializedName("registered-ids")
    public ArrayList<UUID> playerIds;
    @SerializedName("player-data")
    public JsonObject players;

    public JsonPlayerData() {
        playerIds = new ArrayList<>();
        players = new JsonObject();
    }

    public boolean isPlayerRegistered(UUID uuid) { return playerIds.contains(uuid); }

    public CPlayer loadPlayer(UUID uuid) {
        JsonElement jsonPlayer = players.get(uuid.toString());
        Gson gson = new Gson();
        PlayerData data = gson.fromJson(jsonPlayer, PlayerData.class);
        return new CPlayer(data);
    }

    public void savePlayer(CPlayer p) {
        Gson gson = new Gson();
        if (!isPlayerRegistered(p.getUUID())) playerIds.add(p.getUUID());
        JsonElement jsonPlayer = gson.toJsonTree(p.asData());
        players.add(p.getUUID().toString(), jsonPlayer);
    }
}