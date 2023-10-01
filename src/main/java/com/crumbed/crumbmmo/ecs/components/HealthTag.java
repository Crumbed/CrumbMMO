package com.crumbed.crumbmmo.ecs.components;

import com.crumbed.crumbmmo.ecs.EntityComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.TextDisplay;

public class HealthTag extends EntityComponent {
    public static int ID;
    @Override
    public int id() { return ID; }

    public transient TextDisplay tag;
    public HealthTag(TextDisplay tag) { this.tag = tag; }

    public void setHealth(int at, int max) {
        tag.setText(String.format(
                "%s%d%s/%s%d❤",
                ChatColor.RED, at, ChatColor.GRAY,
                ChatColor.RED, max
        ));
    }
}
