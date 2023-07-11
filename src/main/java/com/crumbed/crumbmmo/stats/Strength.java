package com.crumbed.crumbmmo.stats;

import com.crumbed.crumbmmo.utils.Option;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.SerializableAs;

public class Strength implements Stat {
    private double value;
    public static final double DEFAULT = 0.0;

    public Strength(double value) { this.value = value; }

    @Override
    public GenericStat getGenericType() { return GenericStat.Strength; }
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
    public Option<Double> getMaxValue() { return Option.none(); }

    @Override
    public String display() {
        return String.format("%sStrength: %s+%d",
                ChatColor.GRAY, ChatColor.RED, (int) this.value
        );
    }


}
