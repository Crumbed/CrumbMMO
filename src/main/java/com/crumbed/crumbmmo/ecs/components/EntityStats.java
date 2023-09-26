package com.crumbed.crumbmmo.ecs.components;

import com.crumbed.crumbmmo.ecs.EntityComponent;
import com.crumbed.crumbmmo.stats.*;
import com.google.gson.annotations.SerializedName;
import org.bukkit.ChatColor;

public class EntityStats extends EntityComponent {
    public static int ID;
    @Override
    public int id() { return ID; }

    public Damage damage;
    public Strength strength;
    @SerializedName("crit-damage")
    public CritDamage critDamage;
    @SerializedName("crit-chance")
    public CritChance critChance;
    public Health health;
    public Defense defense;
    public Mana mana;


    public EntityStats(
        Damage damage,
        Strength strength,
        CritDamage critDamage,
        CritChance critChance,
        Health health,
        Defense defense,
        Mana mana
    ) {
        this.damage = damage;
        this.strength = strength;
        this.critDamage = critDamage;
        this.critChance = critChance;
        this.health = health;
        this.defense = defense;
        this.mana = mana;
    }



    public void setFromGeneric(GenericStat genStat, double value) {
        switch (genStat) {
            case Strength   :   strength.setValue(value); break;
            case CritDamage :   critDamage.setValue(value); break;
            case CritChance :   critChance.setValue(value); break;
            case Health     :   health.setValue(value); break;
            case Defense    :   defense.setValue(value); break;
            case Mana       :   mana.setValue(value); break;
            case Damage     :   damage.setValue(value); break;
        }
    }
    public void setMaxFromGeneric(GenericStat genStat, double value) {
        switch (genStat) {
            case Health     :   health.setBaseValue(value); break;
            case Mana       :   mana.setBaseValue(value); break;
        }
    }
    public void resetFromGeneric(GenericStat genStat) {
        switch (genStat) {
            case Strength   :   strength.setValue(strength.getDefaultValue().unwrap()); break;
            case CritDamage :   critDamage.setValue(critDamage.getDefaultValue().unwrap()); break;
            case CritChance :   critChance.setValue(critChance.getDefaultValue().unwrap()); break;
            case Health     :   health.setBaseValue(health.getDefaultValue().unwrap()); break;
            case Defense    :   defense.setValue(defense.getDefaultValue().unwrap()); break;
            case Mana       :   mana.setBaseValue(mana.getDefaultValue().unwrap()); break;
            case Damage     :   damage.setValue(damage.getDefaultValue().unwrap()); break;
        }
    }

    public String toString() {
        return String.format(
                "%s=-=-=-=-=-=-=-=-=-=\n"+
                "  %sDamage: %s%d\n" +
                "  %sStrength: %s%d\n" +
                "  %sCrit Damage: %s%d%%\n" +
                "  %sCrit Chance: %s%d%%\n" +
                "  %sHealth: %s%d/%d\n" +
                "  %sDefense: %s%d\n" +
                "  %sMana: %s%d/%d\n" +
                "%s=-=-=-=-=-=-=-=-=-=",
                ChatColor.GRAY,
                ChatColor.GRAY, ChatColor.RED, (int) damage.getValue(),
                ChatColor.GRAY, ChatColor.RED, (int) strength.getValue(),
                ChatColor.GRAY, ChatColor.BLUE, (int) (critDamage.getValue() * 100),
                ChatColor.GRAY, ChatColor.BLUE, (int) (critChance.getValue() * 100),
                ChatColor.GRAY, ChatColor.RED, (int) health.getValue(), (int) health.getBaseValue(),
                ChatColor.GRAY, ChatColor.GREEN, (int) defense.getValue(),
                ChatColor.GRAY, ChatColor.AQUA, (int) mana.getValue(), (int) mana.getBaseValue(),
                ChatColor.GRAY
        );
    }
}


























