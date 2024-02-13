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
            double dmg,
            double str,
            double critDamage,
            double critChance,
            Option<Float> attackCooldown,
            DamageType dt
    ) {
        var initDmg = (int) ((int) (5 + dmg) * (1 + (str / 100)));
        var cooldown = switch (attackCooldown) {
            case Some<Float> s -> s.inner();
            case None<Float> ignored -> 1f;
        };

        if (Math.random() <= critChance && cooldown > 0.8)
            return new DamageValue((int) (initDmg * (1 + critDamage)), true, dt);
        else if (cooldown < 0.5) initDmg /= 2;
        return new DamageValue(initDmg, false, dt);
    }

    public void regenHealth(BigStat hp) {
        var newHp = hp.value + (hp.max.value * 0.01 + 1.5) * hp.regen.value;
        hp.value = Math.min(newHp, hp.max.value);
    }

    public void regenMana(BigStat mana) {
        var newMana = mana.value + mana.max.value * mana.regen.value;
        mana.value = Math.min(newMana, mana.max.value);
    }

    public double calcHealthScale(BigStat health) {
        var healthScale = 0D;
        if (health.max.value <= 100) healthScale = 20;
        else if (health.max.value > 100) healthScale = 40;
        else healthScale = health.max.value / 50D + 20D;

        if (healthScale % 2 > 0 && !(health.max.value % 100 == 50)) {
            healthScale = Math.round(healthScale / 2) * 2;
        }

        return healthScale;
    }
}

























