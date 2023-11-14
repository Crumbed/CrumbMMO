package com.crumbed.crumbmmo.commands;

import com.crumbed.crumbmmo.ecs.CEntity;
import com.crumbed.crumbmmo.managers.EntityManager;
import com.crumbed.crumbmmo.managers.PlayerManager;
import com.crumbed.crumbmmo.ecs.CPlayer;
import com.crumbed.crumbmmo.serializable.PlayerData;
import com.crumbed.crumbmmo.utils.Option;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@CommandInfo(name = "cdebug", permission = "cmmo.debug", requiresPlayer = false, treeCommand = true)
public class Debug extends CustomCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
    }


    public static SubCommand PLAYERS = new SubCommand("players", "", false, new TabComponent[0][0], (sender, args) -> {
        sender.sendMessage("Collecting players...");
        Stream<PlayerData> players = PlayerManager
                .INSTANCE
                .getPlayers()
                .filter(Objects::nonNull)
                .map(CPlayer::asData);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        players.forEach(p -> sender.sendMessage(gson.toJson(p)));
    });

    public static SubCommand ENTITIES = new SubCommand("entities", "", false, new TabComponent[0][0], (sender, args) -> {
        var i = new AtomicInteger();
        EntityManager.INSTANCE
                .getEntities()
                .map(x -> {
                    var rep = String.format("%s : %s", i, (x == null)
                            ? "NULL"
                            : "Entity"
                    );
                    i.addAndGet(1);
                    return rep;
                })
                .forEach(sender::sendMessage);
    });


    public static SubCommand GET_ENTITY = new SubCommand("get_entity", "", false, new TabComponent[][] {
            { new TabComponent(TabComponent.Type.Id, Option.some(EntityManager.INSTANCE), false) }
    }, (sender, args) -> {
        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Error: " + args[0] + " is an invalid <id>.");
            return;
        }

        var e = EntityManager
                .INSTANCE
                .getEntity(id);

        if (e.isNone()) {
            sender.sendMessage(ChatColor.RED + "Error: there is not entity with id " + id);
            return;
        }

        var gson = new GsonBuilder().setPrettyPrinting().create();
        sender.sendMessage(String.format(
                "%s%s",
                ChatColor.GRAY,
                gson.toJson(e.unwrap())
        ));
    });
}





















