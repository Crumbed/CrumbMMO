package com.crumbed.crumbmmo.managers;

import com.crumbed.crumbmmo.stats.*;
import com.crumbed.crumbmmo.utils.None;
import com.crumbed.crumbmmo.utils.Option;
import com.crumbed.crumbmmo.utils.Some;

public class StatManager {
    public static Option<StatManager> INSTANCE = Option.none();

    private StatManager() {
    }

    public static void init() {
        if (INSTANCE.isNone()) INSTANCE = Option.some(new StatManager());
    }


    /**
     * Calculate the damage an attack will deal
     *
     * @param   dmg             damage
     * @param   str             strength
     * @param   critDamage      crit damage
     * @param   critChance      crit chance
     * @param   attackCooldown  attack cooldown
     * @param   dt              damage type
     *
     * @return  A DamageValue that represents the calculated damage
     */
    public DamageValue calcDamage(
            Damage dmg,
            Strength str,
            CritDamage critDamage,
            CritChance critChance,
            Option<Float> attackCooldown,
            DamageType dt
    ) {
        int initDmg = (int) ((int) (5 + dmg.getValue()) * (1 + (str.getValue() / 100)));
        var cooldown = switch (attackCooldown) {
            case Some<Float> s -> s.inner();
            case None<Float> ignored -> 1f;
        };

        if (Math.random() <= critChance.getValue() && cooldown > 0.8)
            return new DamageValue((int) (initDmg * (1 + critDamage.getValue())), true, dt);
        else if (cooldown < 0.5) initDmg /= 2;
        return new DamageValue(initDmg, false, dt);
    }

    public void regenHealth(Health hp) {
        double newHp = hp.getValue() + (hp.getBaseValue() / 100 + 1.5) * (hp.getRegenRate().unwrap() / 100);
        hp.setValue(Math.min(newHp, hp.getBaseValue()));
    }

    public void regenMana(Mana mana) {
        double newMana = mana.getValue() + mana.getBaseValue() * 0.02;
        mana.setValue(Math.min(newMana, mana.getBaseValue()));
    }
}

























