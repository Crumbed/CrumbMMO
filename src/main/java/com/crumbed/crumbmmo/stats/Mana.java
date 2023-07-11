package com.crumbed.crumbmmo.stats;

import com.crumbed.crumbmmo.utils.Option;
import org.bukkit.ChatColor;

public class Mana implements Stat {
    private double baseMana;
    private double value;
    public static double DEFAULT = 100;

    public Mana(double value) {
        this.baseMana = value;
        this.value = value;
    }

    @Override
    public GenericStat getGenericType() { return GenericStat.Mana; }
    @Override
    public double getValue() { return value; }
    @Override
    public void setValue(double value) { this.value = value; }
    public double getBaseValue() { return baseMana; }
    public void setBaseValue(double value) { this.baseMana = value; }
    @Override
    public Option<Double> getDefaultValue() { return Option.some(DEFAULT); }
    @Override
    public boolean isRegen() { return true; }
    @Override
    public Option<Double> getRegenRate() { return Option.some(0.02); }
    @Override
    public Option<Double> getMaxValue() { return Option.none(); }

    @Override
    public String display() {
        return String.format("%sMana: %s+%d",
                ChatColor.GRAY, ChatColor.GREEN, (int) this.baseMana
        );
    }
}
