package com.crumbed.crumbmmo.commands;

import com.crumbed.crumbmmo.CrumbMMO;
import com.crumbed.crumbmmo.utils.Some;
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
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static org.reflections.scanners.Scanners.SubTypes;

public abstract class CustomCommand implements CommandExecutor, TabCompleter {
    private final CommandInfo commandInfo;
    private HashMap<String, SubCommand> subCommands;

    public CustomCommand() {
        commandInfo = getClass().getDeclaredAnnotation(CommandInfo.class);
        requireNonNull(commandInfo, "Commands must have CommandInfo annotation");
        initSubCommands();
    }

    public CommandInfo getCommandInfo() { return commandInfo; }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String lbl, String[] args) {
        var fullJoined = String.join(".", Arrays.asList(args));
        Class<?> cmdClass = null;
        var classes = new Reflections("com.crumbed.crumbmmo.commands");
        for (var clazz : classes.get(SubTypes.of(CustomCommand.class).asClass())) {
            var cmdInfo = clazz.getDeclaredAnnotation(CommandInfo.class);
            if (!lbl.equals(cmdInfo.name())) continue;
            cmdClass = clazz;
            break;
        }

        ArrayList<TabComponent[]> cmdArgs = null;
        try {
            cmdArgs = Arrays.stream(((TabComponent[][]) cmdClass.getField("ARGS").get(null)))
                    .filter(a -> a.length-1 >= args.length)
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) { return List.of(new String[] {}); }

        var iOff = 0;
        for (var i = 0; i < args.length - 1; i++) {
            var arg = args[i];
            SubCommand subCmd = null;

            // check command arguments
            for (var j = 0; j < cmdArgs.size(); j++) {
                var cmdArg = cmdArgs.get(j)[i-iOff];

                switch (cmdArg.type()) {
                    case Int, Count -> { try {
                        Integer.parseInt(arg);
                        for (var k = cmdArgs.size()-1; k >= 0; k--) {
                            var argK = cmdArgs.get(k)[i-iOff];
                            if (argK.type() != TabComponent.Type.Int && argK.type() != TabComponent.Type.Count) {
                                cmdArgs.remove(k);
                                if (k < j) j--;
                            }
                        }
                    } catch (NumberFormatException ignored){
                        cmdArgs.remove(j);
                        j--;
                    }}

                    case PlayerName -> {
                        if (!Bukkit.getOnlinePlayers()
                                .stream()
                                .map(Player::getName)
                                .toList()
                                .contains(arg)) {
                            cmdArgs.remove(j);
                            j--;
                        }

                        for (var k = cmdArgs.size()-1; k >= 0; k--) {
                            var argK = cmdArgs.get(k)[i-iOff];
                            if (argK.type() != TabComponent.Type.PlayerName) {
                                cmdArgs.remove(k);
                                if (k < j) j--;
                            }
                        }
                    }

                    case Id -> {
                        var tabSource = cmdArg.tabSource().unwrap().getTabSource();
                        if (Arrays.stream(tabSource)
                                .filter(x -> x.equalsIgnoreCase(arg))
                                .toList()
                                .isEmpty()
                        ) {
                            cmdArgs.remove(j);
                            j--;
                        }
                    }

                    case Literal -> {
                        var lit = (TabComponent.Literal) cmdArg.tabSource().unwrap();
                        if (!arg.equalsIgnoreCase(lit.lit())) {
                            cmdArgs.remove(j);
                            j--;
                        }
                    }
                }
            }

            if (!cmdArgs.isEmpty() || subCmd != null || i != 0) continue;
            // Check sub commands
            for (var f : cmdClass.getFields()) {
                if (f.getType() != SubCommand.class) continue;
                try { subCmd = (SubCommand) f.get(null);
                } catch (IllegalAccessException e) { e.printStackTrace(); }
                if (subCmd == null) continue;

                if (subCmd.name().equalsIgnoreCase(arg)) {
                    cmdArgs = Arrays.stream(subCmd.params()).collect(Collectors.toCollection(ArrayList::new));
                    iOff = 1;
                    break;
                }
                subCmd = null;
            }
        }

        var tabs = new ArrayList<String>();
        final int depth = args.length - 1 - iOff;
        // Populate tab list
        for (var compArr : cmdArgs) {
            var comp = compArr[depth];
            tabs.addAll(switch (comp.type()) {
                case Int -> List.of( "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" );
                case Count -> {
                    if (comp.tabSource().isSome())
                        yield Arrays.stream(comp.tabSource().unwrap().getTabSource()).toList();
                    var l = new int[64];
                    for (var i = 1; i <= 64; i++) l[i-1] = i;
                    yield Arrays.stream(l).mapToObj(x -> x + "").toList();
                }
                case Id, PlayerName -> Arrays.stream(comp.tabSource().unwrap().getTabSource()).toList();
                case Literal -> List.of(((TabComponent.Literal) comp.tabSource().unwrap()).lit());
            });
        }

        // Add sub commands to tabs
        if (args.length <= 1) {
            for (var f : cmdClass.getFields()) {
                if (f.getType() != SubCommand.class) continue;
                SubCommand subCmd = null;
                try { subCmd = (SubCommand) f.get(null);
                } catch (IllegalAccessException e) { e.printStackTrace(); }
                if (subCmd == null) continue;

                if (!sender.hasPermission(subCmd.permission())) continue;
                tabs.add(subCmd.name());
            }
        }

        // Trim tabs to match input
        for (var i = tabs.size()-1; i >= 0; i--) {
            var tab = tabs.get(i);
            var lastArg = args[args.length-1];
            if (
                    lastArg.length() > tab.length()
                    || !tab.substring(0, lastArg.length()).equalsIgnoreCase(lastArg)
            ) {
                tabs.remove(i);
                continue;
            }

            tabs.set(i, tab);
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
        var fullJoined = String.join(".", Arrays.asList(args)) + ".";
        var matches = subCommands.keySet()
                .stream()
                .filter(str -> {
                    if (fullJoined.length() < str.length()) return false;
                    return fullJoined
                            .substring(0, str.length())
                            .equalsIgnoreCase(str);
                })
                .collect(Collectors.toCollection(ArrayList::new));
        if (matches.isEmpty()) return false;

        var longestMatch = "";
        for (var str : matches)
            if (str.length() > longestMatch.length()) longestMatch = str;

        var remainingArgs = fullJoined
                .substring(longestMatch.length() + 1)
                .split("\\.");
        // sender.sendMessage("remaining args: " + String.join(", ", Arrays.asList(remainingArgs)));

        var sc = subCommands.get(longestMatch);

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

        sc.exec(sender, remainingArgs);
        return true;
    }

    private void initSubCommands() {
        subCommands = new HashMap<>();
        if (!commandInfo.treeCommand()) return;
        var fields = Arrays.stream(this.getClass().getFields());

        fields.forEach(f -> {
            if (f.getType() != SubCommand.class) return;
            SubCommand sc = null;
            try {
                sc = (SubCommand) f.get(null);
            } catch (IllegalAccessException e) { e.printStackTrace(); }
            assert sc != null;
            var joined = String.join(".", Arrays.asList(sc.name()));
            subCommands.put(joined, sc);
        });
    }


    public void execute(CommandSender sender, String[] args) {}

}


































