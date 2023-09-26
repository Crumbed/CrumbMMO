package com.crumbed.crumbmmo.commands;

import com.crumbed.crumbmmo.managers.EntityManager;
import com.crumbed.crumbmmo.managers.PlayerManager;
import com.crumbed.crumbmmo.ecs.CPlayer;
import com.crumbed.crumbmmo.serializable.PlayerData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
                .unwrap()
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
}
