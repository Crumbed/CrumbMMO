package com.crumbed.crumbmmo.stats;

import com.crumbed.crumbmmo.utils.Option;
import com.google.gson.annotations.SerializedName;

public enum GenericStat {
    @SerializedName("damage")
    Damage,
    @SerializedName("strength")
    Strength,
    @SerializedName("crit-damage")
    CritDamage,
    @SerializedName("crit-chance")
    CritChance,
    @SerializedName("health")
    Health,
    @SerializedName("defense")
    Defense,
    @SerializedName("mana")
    Mana;

    public static Option<GenericStat> fromString(String stat) {
        switch (stat) {
            case "damage":      return Option.some(GenericStat.Damage);
            case "strength":    return Option.some(GenericStat.Strength);
            case "crit-damage": return Option.some(GenericStat.CritDamage);
            case "crit-chance": return Option.some(GenericStat.CritChance);
            case "health":      return Option.some(GenericStat.Health);
            case "defense":     return Option.some(GenericStat.Defense);
            case "mana":        return Option.some(GenericStat.Mana);
        }
        return Option.none();
    }

    public String toString() {
        switch (this) {
            case Damage     :   return "damage";
            case Strength   :   return "strength";
            case CritDamage :   return "crit-damage";
            case CritChance :   return "crit-chance";
            case Health     :   return "health";
            case Defense    :   return "defense";
            case Mana       :   return "mana";
        }
        return null; // this will never run its just so the compiler will shut up
    }
}
