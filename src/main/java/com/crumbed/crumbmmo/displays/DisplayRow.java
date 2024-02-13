package com.crumbed.crumbmmo.displays;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.world.entity.Display;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.TextDisplay;
import org.joml.Matrix4f;

import java.awt.*;

public class DisplayRow {
    public TextDisplay raw;
    public Color[] colors;


    public DisplayRow(World world, int width) {
        colors = new Color[width];
        var rawPixels = new StringBuilder();

        for (var i = 0; i < width; i++) {
            colors[i] = new Color(255, 255, 255);
            rawPixels.append(ChatColor.of(colors[i])).append("█");
        }

        raw = world.spawn(new Location(world, 0, 0,0), TextDisplay.class);
        raw.setText(rawPixels.toString());
        raw.setTransformationMatrix(new Matrix4f(
                0.02f,0f,0f,0f,0f,0.02f,0f,0f,0f,0f,0.02f,0f,0f,0f,0f,1f
        ));
    }
}
