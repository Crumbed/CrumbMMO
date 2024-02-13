package com.crumbed.crumbmmo.ecs.systems;

import com.crumbed.crumbmmo.ecs.ComponentQuery;
import com.crumbed.crumbmmo.ecs.EntitySystem;
import com.crumbed.crumbmmo.ecs.components.EntityStats;
import com.crumbed.crumbmmo.ecs.components.HealthTag;
import com.crumbed.crumbmmo.ecs.components.NameTag;
import com.crumbed.crumbmmo.ecs.components.RawEntity;
import org.bukkit.Location;

import java.util.stream.Stream;

public class SyncHealthTag extends EntitySystem {
    public SyncHealthTag() {
        super(10, new ComponentQuery(EntityStats.class, HealthTag.class));
    }


    @Override
    public void execute(Stream<ComponentQuery.Result> results) {
        results.forEach(r -> {
            var stats = r.getComponent(EntityStats.class).unwrap();
            var tag = r.getComponent(HealthTag.class).unwrap();

            tag.setHealth(
                    (int) stats.health.value,
                    (int) stats.health.max.value
            );
        });
    }
}













