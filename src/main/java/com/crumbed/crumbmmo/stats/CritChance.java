package com.crumbed.crumbmmo.stats;

import com.crumbed.crumbmmo.utils.Option;
import org.bukkit.ChatColor;

public class CritChance implements Stat {
    private double value;
    public static final double DEFAULT = 0.3;
    public static final double MAX = 1.0;

    public CritChance(double value) { this.value = value; }

    @Override
    public GenericStat getGenericType() { return GenericStat.CritChance; }
    @Override
    public double getValue() { return value; }
    @Override
    public void setValue(double value) { this.value = value; }
    @Override
    public Option<Double> getDefaultValue() { return Option.some(DEFAULT); }
    @Override
    public boolean isRegen() { return false; }
    @Override
    public Option<Double> getRegenRate() { return Option.none(); }
    @Override
    public Option<Double> getMaxValue() { return Option.some(MAX); }
    @Override
    public String display() {
        return String.format("%sCrit Chance: %s+%d%%",
                ChatColor.GRAY, ChatColor.RED, (int) this.value * 100
        );
    }
}
