package com.crumbed.crumbmmo.ecs.components;

import com.crumbed.crumbmmo.ecs.EntityComponent;
import com.crumbed.crumbmmo.stats.*;
import com.google.gson.annotations.SerializedName;
import org.bukkit.ChatColor;

import static com.crumbed.crumbmmo.stats.Stat.Health;

public class EntityStats extends EntityComponent {
    public static int ID;
    @Override
    public int id() { return ID; }

    public Stat.Value damage;
    public Stat.Value strength;
    @SerializedName("crit-damage")
    public Stat.Value critDamage;
    @SerializedName("crit-chance")
    public Stat.Value critChance;
    public Stat.Value defense;
    public BigStat health;
    public BigStat mana;


    public EntityStats(
        double damage,
        double strength,
        double critDamage,
        double critChance,
        double health,
        double defense,
        double mana,
        double healthRegen,
        double manaRegen
    ) {
        this.damage = new Stat.Value(damage);
        this.strength = new Stat.Value(strength);
        this.critDamage = new Stat.Value(critDamage);
        this.critChance = new Stat.Value(critChance);
        this.defense = new Stat.Value(defense);
        this.health = new BigStat(health, health, healthRegen);
        this.mana = new BigStat(mana, mana, manaRegen);
    }



    public void damage(DamageValue damage) {
        damage.reduceDamage(defense.value / (defense.value + 100));
        health.value -= damage.getDamage();
        if (health.value < 0) health.value = 0;
    }

    public void setFromGeneric(Stat genStat, double value) {
        switch (genStat) {
            case Strength -> strength.value = value;
            case CritDamage -> critDamage.value = value;
            case CritChance -> critChance.value = value;
            case Health -> health.value = value;
            case HealthRegen -> health.regen.value = value;
            case Defense -> defense.value = value;
            case Mana -> mana.value = value;
            case ManaRegen -> mana.regen.value = value;
            case Damage -> damage.value = value;
        }
    }
    public void setMaxFromGeneric(Stat genStat, double value) {
        switch (genStat) {
            case Health -> health.max.value = value;
            case Mana -> mana.max.value = value;
        }
    }
    public void resetFromGeneric(Stat genStat) {
        switch (genStat) {
            case Damage -> damage = Stat.Damage.defaultValue();
            case Strength -> damage = Stat.Strength.defaultValue();
            case CritChance -> damage = Stat.CritChance.defaultValue();
            case CritDamage -> damage = Stat.CritDamage.defaultValue();
            case Defense -> damage = Stat.Defense.defaultValue();
            case Health -> health.value = Stat.Health.defaultValue().value;
            case HealthRegen -> health.regen = Stat.HealthRegen.defaultValue();
            case Mana -> mana.value = Stat.HealthRegen.defaultValue().value;
            case ManaRegen -> mana.regen = Stat.HealthRegen.defaultValue();
        }
    }

    public String toString() {
        return String.format("""
                %s=-=-=-=-=-=-=-=-=-=
                  %sDamage: %s%d
                  %sStrength: %s%d
                  %sCrit Damage: %s%d%%
                  %sCrit Chance: %s%d%%
                  %sHealth: %s%d/%d
                  %sDefense: %s%d
                  %sMana: %s%d/%d
                  %sHealth Regeneration: %s%d
                  %sMana Regeneration: %s%d%%
                %s=-=-=-=-=-=-=-=-=-=
                """,
            ChatColor.GRAY,
            ChatColor.GRAY, ChatColor.RED, (int) damage.value,
            ChatColor.GRAY, ChatColor.RED, (int) strength.value,
            ChatColor.GRAY, ChatColor.BLUE, (int) (critDamage.value * 100),
            ChatColor.GRAY, ChatColor.BLUE, (int) (critChance.value * 100),
            ChatColor.GRAY, ChatColor.RED, (int) health.value, (int) health.max.value,
            ChatColor.GRAY, ChatColor.GREEN, (int) defense.value,
            ChatColor.GRAY, ChatColor.AQUA, (int) mana.value, (int) mana.max.value,
            ChatColor.GRAY, ChatColor.RED, (int) health.regen.value,
            ChatColor.GRAY, ChatColor.AQUA, (int) (mana.regen.value * 100),
            ChatColor.GRAY
        );
    }
}


























