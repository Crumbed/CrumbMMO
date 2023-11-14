package com.crumbed.crumbmmo.commands;

import com.crumbed.crumbmmo.managers.ItemManager;
import com.crumbed.crumbmmo.items.CItem;
import com.crumbed.crumbmmo.managers.PlayerManager;
import com.crumbed.crumbmmo.utils.Option;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


@CommandInfo(name = "cgive", requiresPlayer = true)
public class CGive extends CustomCommand {
    public static TabComponent[][] ARGS = {
            {
                    new TabComponent(TabComponent.Type.Id, Option.some(ItemManager.INSTANCE), false),
                    new TabComponent(TabComponent.Type.Count, Option.none(), true),
                    new TabComponent(TabComponent.Type.PlayerName, Option.some(PlayerManager.INSTANCE), true)
            }
    };

    @Override
    public void execute(CommandSender sender, String[] args) {
        CItem item = ItemManager
                .INSTANCE
                .itemReg
                .get(args[0]);
        var raw = item.getRawItem();

        if (args.length > 1 && args[1] != null) {
            var count = Integer.parseInt(args[1]);
            raw.setAmount(count);
        }

        var player = switch (sender) {
            case Player p -> PlayerManager.INSTANCE.getPlayer(p).unwrap().rawPlayer;
            case default -> {
                if (args.length < 2 || args[2] == null) yield null;
                yield Bukkit.getPlayer(args[2]);
            }
        };

        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Error: could not find player \"" + args[2] + "\"");
            return;
        }

        player.getInventory().addItem(raw);
    }
}
