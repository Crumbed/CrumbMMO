package com.crumbed.crumbmmo.stats;

import com.crumbed.crumbmmo.utils.ActionBar;
import com.crumbed.crumbmmo.utils.Option;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

public class Defense implements Stat, ActionBar {
    private double value;
    public static final double DEFAULT = 0;

    public Defense(double value) { this.value = value; }


    @Override
    public GenericStat getGenericType() { return GenericStat.Defense; }
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
        return String.format("%sDefense: %s+%d",
                ChatColor.GRAY, ChatColor.GREEN, (int) this.value
        );
    }

    @Override
    public String genActBar() {
        return String.format(
                "%s%s",
                ChatColor.GREEN,
                (int) value
        );
    }
}
