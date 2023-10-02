package com.crumbed.crumbmmo.genericEvents;

import com.crumbed.crumbmmo.ecs.CPlayer;
import com.crumbed.crumbmmo.ecs.components.EntityStats;
import com.crumbed.crumbmmo.managers.EntityManager;
import com.crumbed.crumbmmo.managers.StatManager;
import com.crumbed.crumbmmo.managers.TimerManager;
import com.crumbed.crumbmmo.stats.DamageType;
import com.crumbed.crumbmmo.stats.DamageValue;
import com.crumbed.crumbmmo.utils.Option;
import com.crumbed.crumbmmo.utils.Timeable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Display;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.Objects;

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
        damageeStats.damage(damage);

        if (e.getDamager() instanceof Player) {
            attackIndicator(damage, (LivingEntity) e.getEntity());
        }

        if (damageeStats.health.getValue() <= 0) {
            var ent = (LivingEntity) e.getEntity();
            ent.damage(ent.getHealth());
            return;
        }

        e.setDamage(damage.getDamage());
    }




    public static void attackIndicator(DamageValue damage, LivingEntity damaged) {
        //Bukkit.getLogger().info("" + damage.getDamage());
        var loc = damaged.getEyeLocation();
        loc.add(new Vector(
                Math.random() - 0.5,
                Math.random() - 0.5,
                Math.random() - 0.5
        ));

        var ind = damaged.getWorld().spawn(loc, TextDisplay.class);
        ind.setText((damage.isCrit())
                ? String.format("%s%s%s-%s %s%d %s%s%s-",
                ChatColor.GOLD, ChatColor.COLOR_CHAR+"k", ChatColor.BOLD, ChatColor.RESET,
                damageColor(damage.getDamage()), damage.getDamage(),
                ChatColor.GOLD, ChatColor.COLOR_CHAR+"k", ChatColor.BOLD
                )
                : String.format("%s%d", damageColor(damage.getDamage()), damage.getDamage())
        );
        ind.setAlignment(TextDisplay.TextAlignment.CENTER);
        ind.setBillboard(Display.Billboard.CENTER);
        ind.setShadowed(true);

        TimerManager.getInstance().addTimeable(new Timeable() {
            private int remainingticks = 40;
            private final TextDisplay indicator = ind;
            @Override
            public int getRemainingTicks() { return remainingticks; }
            @Override
            public void subtractTicks(int ticks) { remainingticks -= ticks; }
            @Override
            public void afterTimer() { indicator.remove(); }
        });
    }

    public static ChatColor damageColor(int damage) {
        if (damage >= 100_000) return ChatColor.LIGHT_PURPLE;
        else if (damage >= 50_000) return ChatColor.GOLD;
        else if (damage >= 5_000) return ChatColor.DARK_PURPLE;
        else if (damage >= 1_000) return ChatColor.BLUE;
        else if (damage >= 500) return ChatColor.GREEN;
        else return ChatColor.GRAY;
    }
}


















