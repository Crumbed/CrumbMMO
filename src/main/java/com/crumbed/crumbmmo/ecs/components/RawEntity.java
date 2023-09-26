package com.crumbed.crumbmmo.ecs.components;

import com.crumbed.crumbmmo.ecs.EntityComponent;
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

    public Option<Entity> getEntity() {
        Entity e = Bukkit.getEntity(id);

        return (e == null)
                ? Option.none()
                : Option.some(e);
    }

    public Option<LivingEntity> getLivingEntity() {
        Entity e = Bukkit.getEntity(id);

        if (e == null) return Option.none();
        else if (e instanceof LivingEntity)
            return Option.some((LivingEntity) e);
        return Option.none();
    }
}
