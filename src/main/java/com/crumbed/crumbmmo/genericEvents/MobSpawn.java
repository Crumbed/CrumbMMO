package com.crumbed.crumbmmo.genericEvents;

import com.crumbed.crumbmmo.ecs.CEntity;
import com.crumbed.crumbmmo.managers.EntityManager;
import com.crumbed.crumbmmo.utils.Option;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;

import static com.crumbed.crumbmmo.utils.Namespaces.MOB_ID;

public class MobSpawn implements Listener {

    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {
        spawnMob(e.getEntity());
    }
    @EventHandler
    public void onSpawn(EntityDeathEvent e) { despawnMob(e.getEntity()); }

    public static void spawnMob(LivingEntity e) {
        if (e instanceof ArmorStand || e instanceof Player) return;
        int mobLevel = EntityManager
                .getMobData()
                .getBaseLevel(e.getType())
                + (int) (Math.random() * 4 + 0.5);
        EntityManager.getMobData()
                .generateMob(e, mobLevel, Option.none());
    }
    public static void despawnMob(LivingEntity e) {
        if (e instanceof ArmorStand || e instanceof Player) return;
        var data = e.getPersistentDataContainer();
        int id = switch (data.get(MOB_ID, PersistentDataType.INTEGER)) {
            case null -> -1;
            case Integer i -> i;
        };
        EntityManager.INSTANCE
                .killEntity(id);
    }
}


















