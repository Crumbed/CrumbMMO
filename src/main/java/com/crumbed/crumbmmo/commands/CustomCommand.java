package com.crumbed.crumbmmo.commands;

import com.crumbed.crumbmmo.CrumbMMO;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.A;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public abstract class CustomCommand implements CommandExecutor, TabCompleter {
    private final CommandInfo commandInfo;
    private HashMap<String, Method> subCommands;

    public CustomCommand() {
        commandInfo = getClass().getDeclaredAnnotation(CommandInfo.class);
        requireNonNull(commandInfo, "Commands must have CommandInfo annotation");
        initSubCommands();
    }

    public CommandInfo getCommandInfo() { return commandInfo; }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String lbl, String[] args) {
        String fullJoined = String.join(".", Arrays.asList(args));
        ArrayList<Map.Entry<String, Method>> matches = (ArrayList<Map.Entry<String,Method>>) subCommands.entrySet()
                .stream()
                .filter(set -> {
                    String name = set.getKey();
                    SubCommand sc = requireNonNull(set.getValue().getDeclaredAnnotation(SubCommand.class));
                    if (fullJoined.length() >= name.length()) return false;
                    if (!name.substring(0, fullJoined.length())
                            .equalsIgnoreCase(fullJoined)) return false;
                    if (!sender.hasPermission(sc.permission())) return false;
                    return true;
                })
                .collect(Collectors.toList());

        ArrayList<String> tabs = new ArrayList<>();
        for (Map.Entry<String, Method> entry : matches) {
            String name = entry.getKey();
            SubCommand sc = requireNonNull(entry.getValue().getDeclaredAnnotation(SubCommand.class));
            int lastDot = name.substring(0, fullJoined.length()).lastIndexOf('.');

            name = name
                    .substring(lastDot+1)
                    .replaceAll("\\.", " ");
            tabs.add(name);
            for (int i = 0; i < sc.params().length; i++)
                tabs.add(name + " " + sc.params()[i]);
        }

        return tabs;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
        if (!commandInfo.permission().isEmpty()
            && !sender.hasPermission(commandInfo.permission())) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to execute this command.");
            return true;
        }

        if (commandInfo.requiresPlayer()) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players may execute this command.");
                return true;
            }
        }
        if (checkSubCommands(sender, args)) return true;

        execute(sender, args);
        return true;
    }

    private boolean checkSubCommands(CommandSender sender, String[] args) {
        String fullJoined = String.join(".", Arrays.asList(args)) + ".";
        ArrayList<String> matches = (ArrayList<String>) subCommands.keySet()
                .stream()
                .filter(str -> {
                    if (fullJoined.length() < str.length()) return false;
                    return fullJoined
                            .substring(0, str.length())
                            .equalsIgnoreCase(str);
                })
                .collect(Collectors.toList());
        if (matches.isEmpty()) return false;

        String longestMatch = "";
        for (String str : matches)
            if (str.length() > longestMatch.length()) longestMatch = str;

        String[] remainingArgs = fullJoined
                .substring(longestMatch.length() + 1)
                .split("\\.");
        // sender.sendMessage("remaining args: " + String.join(", ", Arrays.asList(remainingArgs)));

        Method cmd = subCommands.get(longestMatch);
        SubCommand sc = requireNonNull(cmd.getDeclaredAnnotation(SubCommand.class));

        if (!sc.permission().isEmpty()
                && !sender.hasPermission(sc.permission())) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to execute this command.");
            return true;
        }
        if (sc.requiresPlayer()) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players may execute this command.");
                return true;
            }
        }

        try {
            cmd.invoke(this, sender, remainingArgs);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void initSubCommands() {
        subCommands = new HashMap<>();
        if (!commandInfo.treeCommand()) return;
        Stream<Method> methods = Arrays.stream(this.getClass().getMethods());

        methods.forEach(method -> {
            SubCommand sc = method.getDeclaredAnnotation(SubCommand.class);
            if (sc == null) return;
            String joined = String.join(".", Arrays.asList(sc.name()));
            subCommands.put(joined, method);
        });
    }


    public void execute(CommandSender sender, String[] args) {}

}


































