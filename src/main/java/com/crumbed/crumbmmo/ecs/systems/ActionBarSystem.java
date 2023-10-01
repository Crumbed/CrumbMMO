package com.crumbed.crumbmmo.ecs.systems;

import com.crumbed.crumbmmo.ecs.CEntity;
import com.crumbed.crumbmmo.ecs.ComponentQuery;
import com.crumbed.crumbmmo.ecs.EntityComponent;
import com.crumbed.crumbmmo.ecs.EntitySystem;
import com.crumbed.crumbmmo.ecs.components.EntityActionBar;
import com.crumbed.crumbmmo.ecs.components.RawEntity;
import com.crumbed.crumbmmo.utils.Option;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.stream.Stream;

public class ActionBarSystem extends EntitySystem {

    public ActionBarSystem() {
        super(20, new ComponentQuery(EntityActionBar.class, RawEntity.class));
    }

    @Override
    public void execute(Stream<ComponentQuery.Result> results){
        results.forEach(r -> {
            var bar = r
                    .getComponent(EntityActionBar.class)
                    .unwrap();

            var e = r
                    .getComponent(RawEntity.class)
                    .unwrap()
                    .getLivingEntity(r.parentEntity);
            if (e.isNone() || !(e.unwrap() instanceof Player p)) return;

            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, bar.getActionBar());
        });
    }
}
