package com.crumbed.crumbmmo.items.components;

import com.crumbed.crumbmmo.items.ItemComponent;
import com.crumbed.crumbmmo.stats.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ItemStats extends ItemComponent {
    public static int ID;
    @Override
    public int id() { return ID; }


    private HashMap<GenericStat, Double> stats;

    public ItemStats() {
        stats = new HashMap<>();
    }
    public ItemStats(HashMap<GenericStat, Double> stats) {
        this.stats = stats;
    }

    public HashMap<GenericStat, Double> getAll() { return stats; }
    public void setAll(HashMap<GenericStat, Double> stats) { this.stats = stats; }
    public double get(GenericStat stat) { return stats.getOrDefault(stat, 0D); }
    public void set(GenericStat stat, double value) { stats.put(stat, value); }

    @Override
    public ArrayList<String> toLore() {
        var lore = new ArrayList<String>();
        var damage = (Damage) Stat.fromGeneric(GenericStat.Damage, get(GenericStat.Damage));
        var strength = (Strength) Stat.fromGeneric(GenericStat.Strength, get(GenericStat.Strength));
        var critChance = (CritChance) Stat.fromGeneric(GenericStat.CritChance, get(GenericStat.CritChance));
        var critDamage = (CritDamage) Stat.fromGeneric(GenericStat.CritDamage, get(GenericStat.CritDamage));
        var health = (Health) Stat.fromGeneric(GenericStat.Health, get(GenericStat.Health));
        var defense = (Defense) Stat.fromGeneric(GenericStat.Defense, get(GenericStat.Defense));
        var mana = (Mana) Stat.fromGeneric(GenericStat.Mana, get(GenericStat.Mana));

        if (damage.getValue() != 0D) lore.add(damage.display());
        if (strength.getValue() != 0D) lore.add(strength.display());
        if (critChance.getValue() != 0D) lore.add(critChance.display());
        if (critDamage.getValue() != 0D) lore.add(critDamage.display());
        if (health.getValue() != 0D) lore.add(health.display());
        if (defense.getValue() != 0D) lore.add(defense.display());
        if (mana.getValue() != 0D) lore.add(mana.display());

        return lore;
    }
}

























