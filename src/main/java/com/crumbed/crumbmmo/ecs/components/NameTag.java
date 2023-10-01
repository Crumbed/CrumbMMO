package com.crumbed.crumbmmo.ecs.components;

import com.crumbed.crumbmmo.ecs.EntityComponent;
import org.bukkit.entity.TextDisplay;

public class NameTag extends EntityComponent {
    public static int ID;
    @Override
    public int id() { return ID; }

    public transient TextDisplay tag;
    public NameTag(TextDisplay tag) {
        this.tag = tag;
    }
}
