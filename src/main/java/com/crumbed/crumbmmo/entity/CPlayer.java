package com.crumbed.crumbmmo.entity;

import com.crumbed.crumbmmo.entity.components.EntityInventory;
import com.crumbed.crumbmmo.entity.components.EntityStats;
import com.crumbed.crumbmmo.entity.components.RawEntity;
import com.crumbed.crumbmmo.entity.components.RawLivingEntity;
import com.crumbed.crumbmmo.entity.systems.PlayerInvUpdate;
import com.crumbed.crumbmmo.items.CItem;
import com.crumbed.crumbmmo.stats.*;
import com.google.gson.annotations.SerializedName;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CPlayer extends CEntity {
    public transient Player rawPlayer;
    @SerializedName("player-uuid")
    private final UUID playerUuid;
    private EntityStats stats;
    public EntityInventory inv;

    public CPlayer(RawLivingEntity livingEntity, RawEntity entity, EntityStats stats, EntityInventory inv) {
        super( livingEntity, entity, stats, inv);
        assert livingEntity.raw instanceof Player;
        this.rawPlayer = (Player) livingEntity.raw;
        this.playerUuid = rawPlayer.getUniqueId();
        this.inv = inv;
        this.stats = stats;
    }
    public static CPlayer newPlayer(Player p) {
        ArrayList<Stat> statsList = (ArrayList<Stat>) Stream
                .of(GenericStat.values())
                .map(Stat::fromGeneric)
                .collect(Collectors.toList());
        p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100);
        p.setHealth(100);

        EntityStats stats = new EntityStats(
                (Damage) statsList.get(PlayerInvUpdate.DAMAGE),
                (Strength) statsList.get(PlayerInvUpdate.STRENGTH),
                (CritDamage) statsList.get(PlayerInvUpdate.CRITDAMAGE),
                (CritChance) statsList.get(PlayerInvUpdate.CRITCHANCE),
                (Health) statsList.get(PlayerInvUpdate.HEALTH),
                (Defense) statsList.get(PlayerInvUpdate.DEFENSE),
                (Mana) statsList.get(PlayerInvUpdate.MANA)
        );

        return new CPlayer(new RawLivingEntity(p), new RawEntity(p), stats, new EntityInventory());
    }

    @Override
    public void initLoaded() {
        rawPlayer = Bukkit.getPlayer(playerUuid);
        inv.activeSlot = rawPlayer.getInventory().getHeldItemSlot();
        inv.hasUpdated = true;
        super.initLoaded();

        addComponent(new RawLivingEntity(rawPlayer));
        addComponent(new RawEntity(rawPlayer));
        addComponent(inv);
        addComponent(stats);
    }

    public String getName() { return rawPlayer.getName(); }
    public UUID getUUID() { return rawPlayer.getUniqueId(); }
    public EntityStats getStats() { return stats; }
    public String displayStats() {
        return String.format(
                "%s%s's Stats: \n%s",
                ChatColor.GOLD, rawPlayer.getName(),
                stats.toString()
        );
    }

}

































































