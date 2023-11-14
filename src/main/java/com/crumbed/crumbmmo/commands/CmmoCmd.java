package com.crumbed.crumbmmo.commands;

import com.crumbed.crumbmmo.managers.PlayerManager;
import com.crumbed.crumbmmo.ecs.CPlayer;
import com.crumbed.crumbmmo.stats.GenericStat;
import com.crumbed.crumbmmo.stats.Stat;
import com.crumbed.crumbmmo.utils.Option;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Stream;

@CommandInfo(name = "cmmo", requiresPlayer = false, treeCommand = true)
public class CmmoCmd extends CustomCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        // todo help command
    }

    public static SubCommand STATS = new SubCommand(
            "stats", "", false, new TabComponent[][] {
            { new TabComponent(TabComponent.Type.PlayerName, Option.some(PlayerManager.INSTANCE), true) },
    }, (sender, args) -> {
        CPlayer player;
        if (args.length >= 1) {
            String ign = args[0];
            Option<CPlayer> optPlayer = PlayerManager
                    .INSTANCE
                    .getPlayer(Bukkit.getPlayer(ign));

            if (optPlayer.isNone()) {
                sender.sendMessage(ChatColor.RED + "Cannot find player: " + ign); return; }
            player = optPlayer.unwrap();
        } else if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to view your stats.");
            return;
        } else {
            Player rawPlayer = (Player) sender;
            Option<CPlayer> optPlayer = PlayerManager
                    .INSTANCE
                    .getPlayer(rawPlayer);

            if (optPlayer.isNone()) {
                rawPlayer.sendMessage(ChatColor.RED + "Somehow you don't exist, please report this."); return; }
            player = optPlayer.unwrap();
        }

        sender.sendMessage(player.displayStats());
    });
}














