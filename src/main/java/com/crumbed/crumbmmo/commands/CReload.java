package com.crumbed.crumbmmo.commands;

import com.crumbed.crumbmmo.managers.ItemManager;
import org.bukkit.command.CommandSender;

@CommandInfo(name = "creload", permission = "cmmo.admin", requiresPlayer = false)
public class CReload extends CustomCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        ItemManager.reload();
    }
}
