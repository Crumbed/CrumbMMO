package com.crumbed.crumbmmo.entity.components;

import com.crumbed.crumbmmo.entity.EntityComponent;
import org.bukkit.entity.LivingEntity;

public class RawLivingEntity implements EntityComponent {
    private static final ComponentType TYPE = ComponentType.RawLivingEntity;
    public LivingEntity raw;

    public RawLivingEntity(LivingEntity raw) {
        this.raw = raw;
    }

    @Override
    public ComponentType getType() { return TYPE; }
}
