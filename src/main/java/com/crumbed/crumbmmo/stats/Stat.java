package com.crumbed.crumbmmo.stats;

import com.crumbed.crumbmmo.utils.Option;

public interface Stat {
    GenericStat getGenericType();
    double getValue();
    void setValue(double value);
    Option<Double> getDefaultValue();
    boolean isRegen();
    Option<Double> getRegenRate();
    Option<Double> getMaxValue();
    public String display();


    static Stat fromGeneric(GenericStat genericStat) {
        switch (genericStat) {
            case Strength   :   return new Strength(Strength.DEFAULT);
            case CritChance :   return new CritChance(CritChance.DEFAULT);
            case CritDamage :   return new CritDamage(CritDamage.DEFAULT);
            case Health     :   return new Health(Health.DEFAULT);
            case Defense    :   return new Defense(Defense.DEFAULT);
            case Mana       :   return new Mana(Mana.DEFAULT);
            case Damage     :   return new Damage(Damage.DEFAULT);
        }
        return null;
    }
    static Stat fromGeneric(GenericStat genericStat, double value) {
        switch (genericStat) {
            case Strength   :   return new Strength(value);
            case CritChance :   return new CritChance(value);
            case CritDamage :   return new CritDamage(value);
            case Health     :   return new Health(value);
            case Defense    :   return new Defense(value);
            case Mana       :   return new Mana(value);
            case Damage     :   return new Damage(value);
        }
        return null;
    }
}
