package com.crumbed.crumbmmo.ecs.systems;

import com.crumbed.crumbmmo.ecs.CEntity;
import com.crumbed.crumbmmo.ecs.ComponentQuery;
import com.crumbed.crumbmmo.ecs.EntitySystem;
import com.crumbed.crumbmmo.ecs.components.EntityName;
import com.crumbed.crumbmmo.ecs.components.RawEntity;
import com.crumbed.crumbmmo.managers.PlayerManager;
import com.crumbed.crumbmmo.ecs.components.EntityStats;
import com.crumbed.crumbmmo.managers.StatManager;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.stream.Stream;

public class SyncHealthTypes extends EntitySystem {
    public SyncHealthTypes() {
        super(10, new ComponentQuery(EntityStats.class, RawEntity.class));
    }


    @Override
    public void execute(Stream<ComponentQuery.Result> results) {
        results.forEach(r -> {
            var name = r
                    .getComponent(EntityName.class);
            if (name.isSome()) Bukkit.getLogger().info(name.unwrap().name);
            var entity = r
                    .getComponent(RawEntity.class)
                    .unwrap()
                    .getLivingEntity(r.parentEntity);
            if (entity.isNone()) return;
            var raw = entity.unwrap();
            if (raw.isDead()) return;

            var stats = r
                    .getComponent(EntityStats.class)
                    .unwrap();

            // Sync raw player max health & health scale with custom values
            if (raw.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() != stats.health.max.value) {
                var healthScale = StatManager.INSTANCE.unwrap().calcHealthScale(stats.health);
                raw.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(stats.health.max.value);

                if (raw instanceof Player) {
                    var p = (Player) raw;
                    var healthScales = PlayerManager
                            .INSTANCE
                            .getHealthScales();
                    if (healthScales.containsKey(p.getUniqueId()) && !healthScales.get(p.getUniqueId()).equals(healthScale)) {
                        healthScales.put(r.parentEntity, healthScale);
                        p.setHealthScale(healthScale);
                    }
                }
            }
            // Sync raw player health with custom health
            if (raw.getHealth() != stats.health.value) raw.setHealth(stats.health.value);
        });
    }
}
