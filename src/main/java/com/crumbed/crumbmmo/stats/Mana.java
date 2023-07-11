package com.crumbed.crumbmmo.stats;

import com.crumbed.crumbmmo.utils.ActionBar;
import com.crumbed.crumbmmo.utils.Option;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

public class Mana implements Stat, ActionBar {
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

    /**
     * @return  The MAX mana value
     */
    public double getBaseValue() { return baseMana; }

    /**
     * @param   value   The new MAX mana value
     */
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

    @Override
    public String genActBar() {
        return String.format(
                "%s%s/%s",
                ChatColor.BLUE,
                (int) value,
                (int) baseMana
        );
    }
}
