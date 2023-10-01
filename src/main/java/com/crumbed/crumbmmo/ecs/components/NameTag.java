package com.crumbed.crumbmmo.ecs.components;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.crumbed.crumbmmo.CrumbMMO;
import com.crumbed.crumbmmo.ecs.EntityComponent;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class NameTag extends EntityComponent {
    public static int ID;
    @Override
    public int id() { return ID; }

    public transient TextDisplay tag;
    public NameTag(TextDisplay tag) {
        this.tag = tag;

    }


}
