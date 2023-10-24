package com.crumbed.crumbmmo.items.components;

import com.crumbed.crumbmmo.items.ItemComponent;
import com.crumbed.crumbmmo.stats.GenericStat;

import java.util.HashMap;

public class ItemStats extends ItemComponent {
    public static int ID;
    @Override
    public int id() { return ID; }


    private HashMap<GenericStat, Double> stats;

    public ItemStats() {
        stats = new HashMap<>();
    }

    public double get(GenericStat stat) { return stats.getOrDefault(stat, 0D); }
    public void set(GenericStat stat, double value) { stats.put(stat, value); }
}
