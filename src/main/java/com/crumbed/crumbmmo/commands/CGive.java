package com.crumbed.crumbmmo.commands;

import com.crumbed.crumbmmo.managers.ItemManager;
import com.crumbed.crumbmmo.items.CItem;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "cgive", requiresPlayer = true)
public class CGive extends CustomCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return;
//        CPlayer p = PlayerManager
//                .INSTANCE
//                .unwrap()
//                .getPlayer((Player) sender)
//                .unwrap();
        Player player = (Player) sender;

        CItem item = ItemManager
                .INSTANCE
                .unwrap()
                .itemReg
                .get(args[0]);
        var raw = item.getRawItem();
        if (args[1] != null) raw.setAmount(Integer.parseInt(args[1]));

        player.getInventory().addItem(raw);
    }
}
