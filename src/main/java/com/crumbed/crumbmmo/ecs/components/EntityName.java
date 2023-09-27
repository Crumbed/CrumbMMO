package com.crumbed.crumbmmo.ecs.components;

import com.crumbed.crumbmmo.ecs.EntityComponent;

public class EntityName extends EntityComponent {
    public static int ID;
    @Override
    public int id() { return ID; }

    public String name;

    public EntityName(String name) { this.name = name; }
}
