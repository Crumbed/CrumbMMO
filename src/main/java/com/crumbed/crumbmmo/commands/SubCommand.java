package com.crumbed.crumbmmo.commands;


import com.crumbed.crumbmmo.utils.Option;
import org.bukkit.command.CommandSender;

public record SubCommand(
        String name,
        String permission,
        boolean requiresPlayer,
        TabComponent[][] params,
        CommandFn<CommandSender, String[]> cmd
) {
    @FunctionalInterface
    public interface CommandFn<SENDER, ARGS> {
        void apply(SENDER sender, ARGS args);
    }

    public void exec(CommandSender sender, String[] args) {
        cmd.apply(sender, args);
    }
}
