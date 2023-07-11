package com.crumbed.crumbmmo.entity.components;

import com.crumbed.crumbmmo.entity.EntityComponent;
import org.bukkit.entity.Entity;

public class RawEntity implements EntityComponent {
    private static final ComponentType TYPE = ComponentType.RawEntity;
    public Entity raw;

    public RawEntity(Entity raw) {
        this.raw = raw;
    }

    @Override
    public ComponentType getType() { return TYPE; }
}
