package com.crumbed.crumbmmo.ecs.systems;

import com.crumbed.crumbmmo.ecs.CEntity;
import com.crumbed.crumbmmo.ecs.ComponentQuery;
import com.crumbed.crumbmmo.ecs.EntityComponent;
import com.crumbed.crumbmmo.ecs.EntitySystem;
import com.crumbed.crumbmmo.ecs.components.RawEntity;
import com.crumbed.crumbmmo.managers.PlayerManager;
import com.crumbed.crumbmmo.ecs.components.EntityStats;
import com.crumbed.crumbmmo.utils.Option;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Stream;

public class SyncHealthTypes extends EntitySystem {
    public SyncHealthTypes() {
        super(10, new ComponentQuery(EntityStats.class, RawEntity.class));
    }


    @Override
    public void execute(Stream<ComponentQuery.Result> results) {
        results.forEach(r -> {
            Option<LivingEntity> entity = r
                    .getComponent(RawEntity.class)
                    .unwrap()
                    .getLivingEntity();
            if (entity.isNone()) return;
            LivingEntity raw = entity.unwrap();

            EntityStats stats = r
                    .getComponent(EntityStats.class)
                    .unwrap();

            // Sync raw player max health & health scale with custom values
            if (raw.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() != stats.health.getBaseValue()) {
                double healthScale = stats.health.calcHealthScale();
                raw.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                        .setBaseValue(stats.health.getBaseValue());

                if (raw instanceof Player) {
                    Player p = (Player) raw;
                    HashMap<Integer, Double> healthScales = PlayerManager
                            .INSTANCE
                            .unwrap()
                            .getHealthScales();
                    if (healthScales.containsKey(p.getUniqueId()) && !healthScales.get(p.getUniqueId()).equals(healthScale)) {
                        healthScales.put(r.parentEntity, healthScale);
                        p.setHealthScale(healthScale);
                    }
                }
            }
            // Sync raw player health with custom health
            if (raw.getHealth() != stats.health.getValue())
                raw.setHealth(stats.health.getValue());
        });
    }
}
