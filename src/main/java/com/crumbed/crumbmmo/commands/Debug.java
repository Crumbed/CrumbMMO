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
        sender.sendMessage("Collecting players...");
        Stream<PlayerData> players = PlayerManager
                .INSTANCE
                .getPlayers()
                .filter(Objects::nonNull)
                .map(CPlayer::asData);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        players.forEach(p -> sender.sendMessage(gson.toJson(p)));
    }


    @SubCommand(name = { "players" }, requiresPlayer = false)
    public void players(CommandSender sender, String[] args) {
        execute(sender, args);
    }

    @SubCommand(name = { "entities" }, requiresPlayer = false)
    public void entities(CommandSender sender, String[] args) {
        AtomicInteger i = new AtomicInteger();
        EntityManager.INSTANCE
                .getEntities()
                .map(x -> {
                    String rep = String.format("%s : %s", i, (x == null)
                            ? "NULL"
                            : "Entity"
                    );
                    i.addAndGet(1);
                    return rep;
                })
                .forEach(sender::sendMessage);
    }

    @SubCommand(name = { "get_entity" }, params = { "<id>" }, requiresPlayer = false)
    public void getEntity(CommandSender sender, String[] args) {
        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Error: " + args[0] + " is an invalid <id>.");
            return;
        }

        Option<CEntity> e = EntityManager
                .INSTANCE
                .getEntity(id);

        if (e.isNone()) {
            sender.sendMessage(ChatColor.RED + "Error: there is not entity with id " + id);
            return;
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        sender.sendMessage(String.format(
                "%s%s",
                ChatColor.GRAY,
                gson.toJson(e.unwrap())
        ));
    }

}





















