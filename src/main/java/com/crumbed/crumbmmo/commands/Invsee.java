package com.crumbed.crumbmmo.commands;

import com.crumbed.crumbmmo.managers.ItemManager;
import com.crumbed.crumbmmo.managers.PlayerManager;
import com.crumbed.crumbmmo.ecs.CPlayer;
import com.crumbed.crumbmmo.items.CItem;
import com.crumbed.crumbmmo.utils.Option;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

@CommandInfo(name = "cpinv", permission = "cmmo.admin", requiresPlayer = false)
public class Invsee extends CustomCommand {
    public static TabComponent[][] ARGS = new TabComponent[][] {
            { new TabComponent(TabComponent.Type.PlayerName, Option.some(PlayerManager.INSTANCE), false) }
    };

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Syntax Error: Expected player name!");
            return;
        }

        Option<CPlayer> player_opt = PlayerManager
                .INSTANCE
                .getPlayer(args[0]);
        if (player_opt.isNone()) {
            sender.sendMessage(ChatColor.RED + "Error: Could not find player with name: " + args[0]);
            return;
        }
        CPlayer player = player_opt.unwrap();

        if (sender instanceof Player) {
            Inventory inv = buildInv(player);
            ((Player) sender).openInventory(inv);
            return;
        }

        CItem inv[] = player.inv.inventory;
        CItem armor[] = player.inv.armor;
        CItem curr[] = null;

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        int offset = 0;
        for (int i = 0; i < inv.length + armor.length; ++i) {
            if (i == 0) {
                Bukkit.getServer().getLogger().info("HOTBAR:\n--------------------");
                curr = inv;
            }
            else if (i == 9)     Bukkit.getServer().getLogger().info("--------------------\nINVENTORY:\n--------------------");
            else if (i == 36) {
                Bukkit.getServer().getLogger().info("--------------------\nARMOR + GEAR:\n--------------------");
                curr = armor;
                offset = 36;
            }

            Bukkit.getServer().getLogger().info(gson.toJson(curr[i - offset]) + ",");
        }
    }

    private Inventory buildInv(CPlayer p) {
        CItem inv[] = p.inv.inventory;
        CItem armor[] = p.inv.armor;

        Inventory view = Bukkit.createInventory(null, 54, p.getName() + "'s Inventory:");
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
        meta.setOwnerProfile(p.rawPlayer.getPlayerProfile());
        meta.setDisplayName(ChatColor.GREEN.toString()+ChatColor.BOLD + p.getName());
        playerHead.setItemMeta(meta);
        ItemStack menuGlass = ItemManager
                .INSTANCE
                .itemReg
                .get("black_menu_glass")
                .getRawItem();
        int offset = 0;
        for (int i = 0; i < 54; ++i) {
            if (i == 0) { view.setItem(i, playerHead); continue; }
            else if (i == 3) offset = 3;
            else if (i == 18) offset = 9;
            else if (i == 45) offset = 45;
            int itemIndex = i - offset;

            if (offset == 3 && itemIndex < 5) view.setItem(i, armor[itemIndex].getRawItem());
            else if (i > 17) view.setItem(i, inv[itemIndex].getRawItem());
            else view.setItem(i, menuGlass);
        }

        return view;
    }
}




































