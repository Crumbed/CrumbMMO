package com.crumbed.crumbmmo.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static com.mojang.brigadier.arguments.StringArgumentType.string;


public class BrigadierTest extends BrigadierCommand {

    public LiteralArgumentBuilder<CommandSourceStack> build() {
        return literal("brigadier_test")
            .requires(source -> source.getBukkitSender().hasPermission("cmmo.dev"))
            .then(literal("greet")
                .then(arg("player-name", string())
                    .suggests((c, builder) -> {
                        Bukkit.getOnlinePlayers()
                            .stream()
                            .map(Player::getName)
                            .filter(name -> name.startsWith(getArg(c, "player-name", String.class, "")))
                            .forEach(builder::suggest);

                        return builder.buildFuture();
                    }).executes(BrigadierTest::greet)
                ).executes(BrigadierTest::greetSender)
            )
            .executes(c -> {
                c.getSource().getBukkitSender().sendMessage("BRIGADIER");
                return Command.SINGLE_SUCCESS;
            });
    }


    private static int greetSender(CommandContext<CommandSourceStack> c) {
        var source = c.getSource().getBukkitSender().getName();
        c.getSource().getBukkitSender().sendMessage("Hello, " + source + "!");
        return Command.SINGLE_SUCCESS;
    }
    private static int greet(CommandContext<CommandSourceStack> c) {
        var name = c.getArgument("player-name", String.class);
        var p = Bukkit.getPlayer(name);
        if (p == null) {
            c.getSource().getBukkitSender().sendMessage("Player: " + name + ", is not online.");
            return 0;
        }

        p.sendMessage("Hello, " + name + "!");
        return Command.SINGLE_SUCCESS;
    }



}








































