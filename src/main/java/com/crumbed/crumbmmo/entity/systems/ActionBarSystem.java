package com.crumbed.crumbmmo.entity.systems;

import com.crumbed.crumbmmo.entity.CEntity;
import com.crumbed.crumbmmo.entity.EntityComponent;
import com.crumbed.crumbmmo.entity.EntitySystem;
import com.crumbed.crumbmmo.entity.components.EntityActionBar;
import com.crumbed.crumbmmo.entity.components.RawLivingEntity;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.stream.Stream;

public class ActionBarSystem extends EntitySystem {

    public ActionBarSystem() {
        super( 20, EntityComponent.ComponentType.ActionBar, EntityComponent.ComponentType.RawLivingEntity);
    }

    @Override
    public void execute(Stream<CEntity> entities){
        entities.forEach(e -> {
            EntityActionBar bar = (EntityActionBar) e
                    .getComponent(EntityComponent.ComponentType.ActionBar)
                    .unwrap();

            Player entity = (Player) ((RawLivingEntity) e
                    .getComponent(EntityComponent.ComponentType.RawLivingEntity)
                    .unwrap()).raw;

            entity.spigot().sendMessage(ChatMessageType.ACTION_BAR, bar.getActionBar());

        });
    }
}
