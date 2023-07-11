package com.crumbed.crumbmmo.stats;

import com.crumbed.crumbmmo.utils.Option;
import org.bukkit.ChatColor;

public class Health implements Stat {
    private double value;
    private double baseHealth;
    public static final double DEFAULT = 100;

    public Health(double value) {
        this.value = value;
        this.baseHealth = value;
    }

    @Override
    public GenericStat getGenericType() { return GenericStat.Health; }
    @Override
    public double getValue() { return value; }
    @Override
    public void setValue(double value) { this.value = value; }
    public double getBaseValue() { return baseHealth; }
    public void setBaseValue(double value) { this.baseHealth = value; }
    @Override
    public Option<Double> getDefaultValue() { return Option.some(DEFAULT); }
    @Override
    public boolean isRegen() { return true; }
    @Override
    public Option<Double> getRegenRate() { return Option.some(100D); }
    @Override
    public Option<Double> getMaxValue() { return Option.none(); }
    @Override
    public String display() {
        return String.format("%sHealth: %s+%d",
                ChatColor.GRAY, ChatColor.GREEN, (int) this.baseHealth
        );
    }

    public double calcHealthScale() {
        double healthScale;
        if (baseHealth <= 100) healthScale = 20;
        else if (baseHealth > 1000) healthScale = 40;
        else healthScale = baseHealth / 50.0 + 20;
        if (healthScale % 2 > 0 && !(baseHealth % 100 == 50))
            healthScale = Math.round(healthScale / 2) * 2;

        return healthScale;
    }
}













