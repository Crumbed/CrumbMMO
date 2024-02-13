package com.crumbed.crumbmmo.ecs.systems;

import com.crumbed.crumbmmo.ecs.CEntity;
import com.crumbed.crumbmmo.ecs.ComponentQuery;
import com.crumbed.crumbmmo.ecs.EntityComponent;
import com.crumbed.crumbmmo.ecs.EntitySystem;
import com.crumbed.crumbmmo.ecs.components.EntityStats;
import com.crumbed.crumbmmo.ecs.components.RawEntity;
import com.crumbed.crumbmmo.managers.StatManager;
import org.bukkit.Bukkit;

import java.util.stream.Stream;

public class StatRegen extends EntitySystem {
    public StatRegen() {
        super(20, new ComponentQuery(RawEntity.class, EntityStats.class));
    }


    @Override
    public void execute(Stream<ComponentQuery.Result> results) {
        results.forEach(r -> {
            var entity = r.getComponent(RawEntity.class)
                    .unwrap()
                    .getLivingEntity(r.parentEntity);
            if (entity.isNone() || entity.unwrap().isDead()) return;

            var stats = r
                    .getComponent(EntityStats.class)
                    .unwrap();

            // regen health
            if (stats.health.value < stats.health.max.value) StatManager
                    .INSTANCE
                    .unwrap()
                    .regenHealth(stats.health);
            else if (stats.health.value > stats.health.max.value)
                stats.health.value = stats.health.max.value;

            // regen mana
            if (stats.mana.value < stats.mana.max.value) StatManager
                    .INSTANCE
                    .unwrap()
                    .regenMana(stats.mana);
            else if (stats.mana.value > stats.mana.max.value)
                stats.mana.value = stats.mana.max.value;
        });
    }
}
