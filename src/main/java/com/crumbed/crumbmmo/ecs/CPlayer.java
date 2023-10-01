package com.crumbed.crumbmmo.ecs;

import com.crumbed.crumbmmo.ecs.components.*;
import com.crumbed.crumbmmo.ecs.systems.PlayerInvUpdate;
import com.crumbed.crumbmmo.stats.*;
import com.crumbed.crumbmmo.serializable.PlayerData;
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
                        data.stats.health,
                        data.stats.defense,
                        data.stats.mana
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

        EntityActionBar actionBar = new EntityActionBar(
                stats.health, stats.defense, stats.mana
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

































































