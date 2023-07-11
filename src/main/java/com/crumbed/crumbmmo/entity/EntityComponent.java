package com.crumbed.crumbmmo.entity;

public interface EntityComponent {
    ComponentType getType();

    enum ComponentType {
        Stats,
        RawEntity,
        RawLivingEntity,
        Inventory,
    }
}
