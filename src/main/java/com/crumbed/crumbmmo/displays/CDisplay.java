package com.crumbed.crumbmmo.displays;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.UUID;

public class CDisplay implements TabComponent.Source {
    public static HashMap<UUID, CDisplay> displays = new HashMap<>();
    public DisplayRow[] rows;
    public final UUID id;
    private Location loc;
    public final int width, height;

    public CDisplay() {
        id = null;
        loc = null;
        width = 0;
        height = 0;
    }
    public CDisplay(World world, int width, int height) {
        this.width = width;
        this.height = height;
        loc = new Location(world, 0d, 0d, 0d);

        rows = new DisplayRow[height];
        for (var i = 0; i < height; i++) {
            rows[i] = new DisplayRow(world, width);
        }

        id = UUID.randomUUID();
        displays.put(id, this);
    }


    public void setLocation(double x, double y, double z) {
        loc.setX(x);
        loc.setY(y);
        loc.setZ(z);

        var pointer = loc.clone();
        for (var row : rows) {
            row.raw.teleport(pointer);
            pointer.subtract(0, 0.01, 0);
        }
    }

    @Override
    public String[] getTabSource() {
        return displays.keySet().stream().map(UUID::toString).toList().toArray(String[]::new);
    }
}

























