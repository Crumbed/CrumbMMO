package com.crumbed.crumbmmo.ecs;

import com.crumbed.crumbmmo.ecs.components.*;
import com.crumbed.crumbmmo.ecs.systems.PlayerInvUpdate;
import com.crumbed.crumbmmo.stats.*;
import com.crumbed.crumbmmo.jsonUtils.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CPlayer extends CEntity {
    public Player rawPlayer;
    private EntityStats stats;
    public EntityInventory inv;
    public EntityActionBar actionBar;

    public CPlayer(PlayerData data) {
        super(
                new EntityActionBar(
                    new Stat.StatBar(Stat.Health, data.stats.health),
                    new Stat.StatBar(Stat.Defense, data.stats.defense),
                    new Stat.StatBar(Stat.Mana, data.stats.mana)
                ),
                data.inv,
                data.stats,
                new RawEntity(data.playerUUID)
        );

        this.rawPlayer = Bukkit.getPlayer(data.playerUUID);
        this.stats = data.stats;
        this.inv = data.inv;
        this.actionBar = getComponent(EntityActionBar.class).unwrap();
    }
    public CPlayer(Player p, RawEntity entity, EntityStats stats, EntityInventory inv, EntityActionBar actionBar) {
        super(actionBar, inv, stats, entity);
        this.rawPlayer = p;
        this.inv = inv;
        this.stats = stats;
        this.actionBar = actionBar;
    }
    public static CPlayer newPlayer(Player p) {
        var statsList = (ArrayList<Double>) Stream
            .of(Stat.values())
            .map(Stat::defaultValue)
            .map(x -> x.value)
            .toList();
        p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100);
        p.setHealth(100);

        EntityStats stats = new EntityStats(
            statsList.get(PlayerInvUpdate.DAMAGE),
            statsList.get(PlayerInvUpdate.STRENGTH),
            statsList.get(PlayerInvUpdate.CRITDAMAGE),
            statsList.get(PlayerInvUpdate.CRITCHANCE),
            statsList.get(PlayerInvUpdate.HEALTH),
            statsList.get(PlayerInvUpdate.DEFENSE),
            statsList.get(PlayerInvUpdate.MANA),
            statsList.get(PlayerInvUpdate.HEALTH_REGEN),
            statsList.get(PlayerInvUpdate.MANA_REGEN)
        );

        EntityActionBar actionBar = new EntityActionBar(
            new Stat.StatBar(Stat.Health, stats.health),
            new Stat.StatBar(Stat.Defense, stats.defense),
            new Stat.StatBar(Stat.Mana, stats.mana)
        );

        return new CPlayer(p, new RawEntity(p.getUniqueId()), stats, new EntityInventory(), actionBar);
    }

    public PlayerData asData() {
        return new PlayerData(
                rawPlayer.getUniqueId(),
                rawPlayer.getName(),
                stats,
                inv
        );
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

































































