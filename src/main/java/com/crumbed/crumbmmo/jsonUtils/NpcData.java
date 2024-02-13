package com.crumbed.crumbmmo.jsonUtils;

import com.crumbed.crumbmmo.ecs.components.NpcComponent;
import org.bukkit.Location;


public class NpcData {
    public String id;
    public String name;
    public Location loc;
    public NpcComponent flags;

    public NpcData(
            String id,
            String name,
            Location loc,
            NpcComponent flags
    ) {
        this.id = id;
        this.name = name;
        this.loc = loc;
        this.flags = flags;
    }

}























