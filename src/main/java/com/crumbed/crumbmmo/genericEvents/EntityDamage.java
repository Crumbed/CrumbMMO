package com.crumbed.crumbmmo.genericEvents;

import com.crumbed.crumbmmo.ecs.components.EntityStats;
import com.crumbed.crumbmmo.managers.EntityManager;
import com.crumbed.crumbmmo.managers.StatManager;
import com.crumbed.crumbmmo.stats.DamageType;
import com.crumbed.crumbmmo.utils.Option;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamage implements Listener {


    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        var damager = EntityManager
                .INSTANCE
                .getEntity(e.getDamager())
                .unwrap();
        var optDamagee = EntityManager
                .INSTANCE
                .getEntity(e.getEntity());
        if (optDamagee.isNone()) return;
        var damagee = optDamagee.unwrap();

        var dStats = damager.getComponent(EntityStats.class).unwrap();
        var damage = StatManager.INSTANCE.unwrap().calcDamage(
                dStats.damage,
                dStats.strength,
                dStats.critDamage,
                dStats.critChance,
                (e.getDamager() instanceof Player p)
                        ? Option.some(p.getAttackCooldown())
                        : Option.none(),
                DamageType.Melee
        );

        var damageeStats = damagee.getComponent(EntityStats.class).unwrap();
        var finalDmg = damageeStats.damage(damage);

        if (damageeStats.health.getValue() <= 0) {
            var ent = (LivingEntity) e.getEntity();
            ent.damage(ent.getHealth());
            return;
        }

        e.setDamage(finalDmg);
    }
}


















