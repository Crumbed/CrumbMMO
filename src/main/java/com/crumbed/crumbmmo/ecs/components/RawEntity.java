package com.crumbed.crumbmmo.ecs.components;

import com.crumbed.crumbmmo.ecs.EntityComponent;
import com.crumbed.crumbmmo.managers.EntityManager;
import com.crumbed.crumbmmo.utils.Option;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.UUID;

public class RawEntity extends EntityComponent {
    public static int ID;
    @Override
    public int id() { return ID; }
    public UUID id;

    public RawEntity(UUID rawId) {
        this.id = rawId;
    }

    public Option<Entity> getEntity(int entityId) {
        Entity e = Bukkit.getEntity(id);
        if (e == null) {
            EntityManager.INSTANCE
                    .getEntity(entityId);
            return Option.none();
        }
        return Option.some(e);
    }

    public Option<LivingEntity> getLivingEntity(int entityId) {
        Entity e = Bukkit.getEntity(id);
        if (e == null) {
            EntityManager.INSTANCE
                    .getEntity(entityId);
            return Option.none();
        }

        return switch (e) {
            case LivingEntity living -> Option.some(living);
            case default -> Option.none();
        };
    }
}
