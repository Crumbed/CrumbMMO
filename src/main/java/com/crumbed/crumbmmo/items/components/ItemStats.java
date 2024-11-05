package com.crumbed.crumbmmo.items.components;

import com.crumbed.crumbmmo.items.ItemComponent;
import com.crumbed.crumbmmo.stats.*;
import de.tr7zw.nbtapi.iface.NBTHandler;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ItemStats implements ItemComponent {
    public static final NBTHandler<ItemStats> handler = new NBTHandler<>() {
        @Override
        public boolean fuzzyMatch(Object obj) {
            return obj instanceof ItemStats;
        }

        @Override
        public void set(@NotNull ReadWriteNBT nbt, @NotNull String key, @NotNull ItemStats stats) {
            nbt.removeKey(key);
            var tag = nbt.getOrCreateCompound(key);
            if (stats.damage != 0) tag.setInteger("damage", stats.damage);
            if (stats.strength != 0) tag.setInteger("strength", stats.strength);
            if (stats.critChance != 0f) tag.setFloat("crit_chance", stats.critChance);
            if (stats.critDamage != 0) tag.setInteger("crit_damage", stats.critDamage);
            if (stats.health != 0) tag.setInteger("health", stats.health);
            if (stats.defense != 0) tag.setInteger("defense", stats.defense);
            if (stats.mana != 0) tag.setInteger("mana", stats.mana);
        }

        @Override
        public ItemStats get(@NotNull ReadableNBT nbt, @NotNull String key) {
            var tag = nbt.getCompound(key);
            if (tag == null) return null;

            var stats = new ItemStats();
            stats.damage = tag.getInteger("damage");
            stats.strength = tag.getInteger("strength");
            stats.critChance = tag.getFloat("crit_chance");
            stats.critDamage = tag.getInteger("crit_damage");
            stats.health = tag.getInteger("health");
            stats.defense = tag.getInteger("defense");
            stats.mana = tag.getInteger("mana");

            return stats;
        }
    };

    public int damage = 0;
    public int strength = 0;
    public float critChance = 0;
    public int critDamage = 0;
    public int health = 0;
    public int defense = 0;
    public int mana = 0;

    public double get(Stat stat) {
        return switch (stat) {
            case Damage -> (double) damage;
            case Strength -> (double) strength;
            case CritChance -> (double) critChance;
            case CritDamage -> (double) critDamage;
            case Health -> (double) health;
            case Defense -> (double) defense;
            case Mana -> (double) mana;
            case HealthRegen, ManaRegen -> 0d;
        };
    }

    public ArrayList<String> toLore() {
        var lore = new ArrayList<String>();
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

























