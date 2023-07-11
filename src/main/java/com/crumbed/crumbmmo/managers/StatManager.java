package com.crumbed.crumbmmo.managers;

import com.crumbed.crumbmmo.stats.*;
import com.crumbed.crumbmmo.utils.Option;

public class StatManager {
    public static Option<StatManager> INSTANCE = Option.none();

    private StatManager() {
    }

    public static void init() {
        if (INSTANCE.isNone()) INSTANCE = Option.some(new StatManager());
    }


    public DamageValue calcDamage(
            Damage dmg,
            Strength str,
            CritDamage critDamage,
            CritChance critChance,
            Option<Float> attackCooldown,
            DamageType dt
    ) {
        int initDmg = (int) ((int) (5 + dmg.getValue()) * (1 + (str.getValue() / 100)));
        float cooldown;
        if (attackCooldown.isSome())
            cooldown = attackCooldown.unwrap();
        else cooldown = 1F;

        if (Math.random() <= critChance.getValue() && cooldown > 0.8)
            return new DamageValue((int) (initDmg * (1 + (critDamage.getValue() / 100))), true, dt);
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

























