package com.crumbed.crumbmmo.items.components;

import com.crumbed.crumbmmo.items.ItemComponent;
import com.crumbed.crumbmmo.stats.*;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemStats extends ItemComponent {
    public static int ID;
    @Override
    public int id() { return ID; }


    private HashMap<Stat, Double> stats;

    public ItemStats() {
        stats = new HashMap<>();
    }
    public ItemStats(HashMap<Stat, Double> stats) {
        this.stats = stats;
    }

    public HashMap<Stat, Double> getAll() { return stats; }
    public void setAll(HashMap<Stat, Double> stats) { this.stats = stats; }
    public double get(Stat stat) { return stats.getOrDefault(stat, 0D); }
    public void set(Stat stat, double value) { stats.put(stat, value); }

    @Override
    public ArrayList<String> toLore() {
        var lore = new ArrayList<String>();
        var damage = get(Stat.Damage);
        var strength = get(Stat.Strength);
        var critChance = get(Stat.CritChance);
        var critDamage = get(Stat.CritDamage);
        var health = get(Stat.Health);
        var defense = get(Stat.Defense);
        var mana = get(Stat.Mana);

        if (damage != 0D) lore.add(Stat.Damage.display(damage));
        if (strength != 0D) lore.add(Stat.Strength.display(strength));
        if (critChance != 0D) lore.add(Stat.CritChance.display(critChance));
        if (critDamage != 0D) lore.add(Stat.CritDamage.display(critDamage));
        if (health != 0D) lore.add(Stat.Health.display(health));
        if (defense != 0D) lore.add(Stat.Defense.display(defense));
        if (mana != 0D) lore.add(Stat.Mana.display(mana));

        return lore;
    }
}

























