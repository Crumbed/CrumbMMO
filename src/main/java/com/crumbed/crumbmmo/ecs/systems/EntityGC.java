package com.crumbed.crumbmmo.ecs.systems;

import com.crumbed.crumbmmo.ecs.CEntity;
import com.crumbed.crumbmmo.ecs.ComponentQuery;
import com.crumbed.crumbmmo.ecs.EntitySystem;
import com.crumbed.crumbmmo.ecs.components.RawEntity;
import com.crumbed.crumbmmo.managers.EntityManager;

import java.util.stream.Stream;

public class EntityGC extends EntitySystem {
    public EntityGC() {
        super(1200, new ComponentQuery(RawEntity.class));
    }



    @Override
    public void execute(Stream<ComponentQuery.Result> results) {
        results.forEach(r -> {
            var raw = r.getComponent(RawEntity.class).unwrap();
            if (raw.getEntity(r.parentEntity).isNone()) {
                EntityManager.INSTANCE.killEntity(r.parentEntity);
            }
        });
    }
}

















