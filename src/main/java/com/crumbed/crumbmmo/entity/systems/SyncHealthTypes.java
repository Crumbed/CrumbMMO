package com.crumbed.crumbmmo.entity.systems;

import com.crumbed.crumbmmo.entity.CEntity;
import com.crumbed.crumbmmo.entity.EntityComponent;
import com.crumbed.crumbmmo.entity.EntitySystem;
import com.crumbed.crumbmmo.managers.PlayerManager;
import com.crumbed.crumbmmo.entity.components.EntityStats;
import com.crumbed.crumbmmo.entity.components.RawLivingEntity;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Stream;

public class SyncHealthTypes extends EntitySystem {
    public SyncHealthTypes() {
        super(10, EntityComponent.ComponentType.Stats, EntityComponent.ComponentType.RawLivingEntity);
    }


    @Override
    public void execute(Stream<CEntity> entities) {
        entities.forEach(e -> {
            LivingEntity raw = ((RawLivingEntity) e
                    .getComponent(EntityComponent.ComponentType.RawLivingEntity)
                    .unwrap())
                    .raw;
            EntityStats stats = (EntityStats) e
                    .getComponent(EntityComponent.ComponentType.Stats)
                    .unwrap();

            // Sync raw player max health & health scale with custom values
            if (raw.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() != stats.health.getBaseValue()) {
                double healthScale = stats.health.calcHealthScale();
                raw.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                        .setBaseValue(stats.health.getBaseValue());

                if (raw instanceof Player) {
                    HashMap<UUID, Double> healthScales = PlayerManager
                            .INSTANCE
                            .unwrap()
                            .getHealthScales();
                    if (healthScales.containsKey(e.id) && !healthScales.get(e.id).equals(healthScale)) {
                        healthScales.put(e.id, healthScale);
                        ((Player) raw).setHealthScale(healthScale);
                    }
                }
            }
            // Sync raw player health with custom health
            if (raw.getHealth() != stats.health.getValue())
                raw.setHealth(stats.health.getValue());
        });
    }
}
