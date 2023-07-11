package com.crumbed.crumbmmo.commands;

import com.crumbed.crumbmmo.managers.PlayerManager;
import com.crumbed.crumbmmo.entity.CPlayer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.command.CommandSender;

import java.util.stream.Stream;

@CommandInfo(name = "cplayers", permission = "cmmo.debug", requiresPlayer = false)
public class Debug extends CustomCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("Collecting players...");
        Stream<CPlayer> players = PlayerManager
                .INSTANCE
                .unwrap()
                .getPlayers();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        players.forEach(p -> sender.sendMessage(gson.toJson(p)));
    }
}
