package com.crumbed.crumbmmo.stats;

import com.crumbed.crumbmmo.utils.Option;

/**
 * interface for all Stats
 */
public interface Stat {
    /**
     * Gets the generic representation of the stat
     * @return GenericStat enum
     */
    GenericStat getGenericType();

    /**
     * @return The value of the stat
     */
    double getValue();

    /**
     * @param value     The new value of the stat
     */
    void setValue(double value);

    /**
     * @return  Option.some if the stat has a default value
     * @return  Option.none if the stat doesnt have a default value
     */
    Option<Double> getDefaultValue();

    /**
     * @return  if the stat is a regeneratable stat
     */
    boolean isRegen();

    /**
     * @return  Option.some(regen rate) if isRegen()
     * @return  Option.none if !isRegen()
     */
    Option<Double> getRegenRate();

    /**
     * @return  Option.some(max value) if the Stat has a hard cap
     * @return  Option.none if the Stat doesnt have a hard cap
     */
    Option<Double> getMaxValue();

    /**
     * @return  A String representing the stat
     */
    public String display();


    /**
     * @param   genericStat The generic type of the Stat
     * @return  A Stat from the generic type given
     */
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

    /**
     * @param   genericStat The generic type of the Stat
     * @param   value       The value for the Stat
     * @return  A Stat from the generic type and value given
     */
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






















