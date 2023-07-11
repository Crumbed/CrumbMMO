package com.crumbed.crumbmmo.entity.systems;

import com.crumbed.crumbmmo.entity.CEntity;
import com.crumbed.crumbmmo.entity.EntityComponent;
import com.crumbed.crumbmmo.entity.EntitySystem;
import com.crumbed.crumbmmo.entity.components.EntityStats;
import com.crumbed.crumbmmo.managers.StatManager;

import java.util.stream.Stream;

public class StatRegen extends EntitySystem {
    public StatRegen() {
        super(20, EntityComponent.ComponentType.Stats);
    }


    @Override
    public void execute(Stream<CEntity> entities) {
        entities.forEach(e -> {
            EntityStats stats = (EntityStats) e
                    .getComponent(EntityComponent.ComponentType.Stats)
                    .unwrap();

            // regen health
            if (stats.health.getValue() < stats.health.getBaseValue()) StatManager
                    .INSTANCE
                    .unwrap()
                    .regenHealth(stats.health);
            else if (stats.health.getValue() > stats.health.getBaseValue())
                stats.health.setValue(stats.health.getBaseValue());

            // regen mana
            if (stats.mana.getValue() < stats.mana.getBaseValue()) StatManager
                    .INSTANCE
                    .unwrap()
                    .regenMana(stats.mana);
            else if (stats.mana.getValue() > stats.mana.getBaseValue())
                stats.mana.setValue(stats.mana.getBaseValue());
        });
    }
}
