package com.crumbed.crumbmmo.ecs.systems;

import com.crumbed.crumbmmo.ecs.CEntity;
import com.crumbed.crumbmmo.ecs.ComponentQuery;
import com.crumbed.crumbmmo.ecs.EntitySystem;
import com.crumbed.crumbmmo.ecs.components.EntityName;
import com.crumbed.crumbmmo.ecs.components.NameTag;
import com.crumbed.crumbmmo.ecs.components.RawEntity;
import com.crumbed.crumbmmo.managers.EntityManager;
import com.crumbed.crumbmmo.utils.None;
import com.crumbed.crumbmmo.utils.Some;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import java.util.stream.Stream;

public class SyncNameTag extends EntitySystem {

    public SyncNameTag() {
        super(1, new ComponentQuery(NameTag.class, RawEntity.class));
    }

    @Override
    public void execute(Stream<ComponentQuery.Result> results) {
        results.forEach(r -> {
            var optLiving = r.getComponent(RawEntity.class)
                    .unwrap()
                    .getLivingEntity(r.parentEntity);
            if (optLiving.isNone()) return;
            var loc = optLiving
                    .unwrap()
                    .getEyeLocation();

            var tagLoc = r.getComponent(NameTag.class)
                    .unwrap()
                    .tag
                    .getLocation();

            tagLoc.setX(loc.getX());
            tagLoc.setY(loc.getY() + 0.5);
            tagLoc.setZ(loc.getZ());
        });
    }
}



















