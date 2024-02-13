package com.crumbed.crumbmmo.stats;

import com.crumbed.crumbmmo.utils.ActionBar;
import com.crumbed.crumbmmo.utils.Option;
import com.google.gson.annotations.SerializedName;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import static com.crumbed.crumbmmo.commands.BrigadierCommand.getArg;

public enum Stat {
    @SerializedName("damage")
    Damage,
    @SerializedName("strength")
    Strength,
    @SerializedName("crit-damage")
    CritDamage,
    @SerializedName("crit-chance")
    CritChance,
    @SerializedName("health")
    Health,
    @SerializedName("defense")
    Defense,
    @SerializedName("mana")
    Mana,
    @SerializedName("health-regen")
    HealthRegen,
    @SerializedName("mana-regen")
    ManaRegen;

    public static Option<Stat> fromString(String stat) {
        return switch (stat) {
            case "damage" -> Option.some(Stat.Damage);
            case "strength" -> Option.some(Stat.Strength);
            case "crit-damage" -> Option.some(Stat.CritDamage);
            case "crit-chance" -> Option.some(Stat.CritChance);
            case "health" -> Option.some(Stat.Health);
            case "defense" -> Option.some(Stat.Defense);
            case "mana" -> Option.some(Stat.Mana);
            case "health-regen" -> Option.some(Stat.HealthRegen);
            case "mana-regen" -> Option.some(Stat.ManaRegen);
            default -> Option.none();
        };
    }

    public Value defaultValue() {
        return switch (this) {
            case Damage, Strength, Defense -> new Value(0D);
            case CritDamage -> new Value(0.5D);
            case CritChance -> new Value(0.3D);
            case Health, Mana -> new Value(100D);
            case HealthRegen -> new Value(1D);
            case ManaRegen -> new Value(0.02D);
        };
    }



    public String toString() {
        return switch (this) {
            case Damage -> "damage";
            case Strength -> "strength";
            case CritDamage -> "crit-damage";
            case CritChance -> "crit-chance";
            case Health -> "health";
            case HealthRegen -> "health-regen";
            case Defense -> "defense";
            case Mana -> "mana";
            case ManaRegen -> "mana-regen";
        };
    }

    public static final String[] literals = {
        "damage",
        "strength",
        "crit-damage",
        "crit-chance",
        "health",
        "health-regen",
        "max-health",
        "defense",
        "mana",
        "max-mana",
        "mana-regen"
    };

    public static CompletableFuture<Suggestions> suggest(CommandContext<CommandSourceStack> c, SuggestionsBuilder builder) {
        Arrays.stream(literals)
            .filter(lit -> lit.startsWith(getArg(c, "stat", String.class, "")))
            .forEach(builder::suggest);

        return builder.buildFuture();
    }

    public String display(double value) {
        return switch (this) {
            case Damage -> ChatColor.GRAY + "Damage: " + ChatColor.RED + "+" + (int) value;
            case Strength -> ChatColor.GRAY + "Strength: " + ChatColor.RED + "+" + (int) value;
            case CritDamage -> ChatColor.GRAY + "Crit Damage: " + ChatColor.BLUE + "+" + (int) value * 100 + "%";
            case CritChance -> ChatColor.GRAY + "Crit Chance: " + ChatColor.BLUE + "+" + (int) value * 100 + "%";
            case Health -> ChatColor.GRAY + "Health: " + ChatColor.GREEN + "+" + (int) value;
            case Defense -> ChatColor.GRAY + "Defense: " + ChatColor.GREEN + "+" + (int) value;
            case Mana -> ChatColor.GRAY + "Mana: " + ChatColor.GREEN + "+" + (int) value;
            case HealthRegen -> ChatColor.GRAY + "Health Regeneration: " + ChatColor.RED + "+" + (int) value;
            case ManaRegen -> ChatColor.GRAY + "Mana Regeneration: " + ChatColor.AQUA + "+" + (int) value * 100 + "%";
        };
    }


    public static class StatBar implements ActionBar {
        private Value v;
        private Stat t;

        public StatBar(Stat t, Value v) {
            this.v = v;
            this.t = t;
        }


        @Override
        public String genActBar() {
            var bar = switch (this.t) {
                case Damage, Strength, Health, HealthRegen -> ChatColor.RED;
                case CritDamage, CritChance -> ChatColor.BLUE;
                case Defense -> ChatColor.GREEN;
                case Mana, ManaRegen -> ChatColor.AQUA;
            } + "";

            return bar + switch (this.t) {
                case CritChance, CritDamage, HealthRegen, ManaRegen -> (int) (this.v.value * 100) + "%";
                default -> (this.v instanceof BigStat stat)
                    ? (int) this.v.value + "/" + (int) stat.max.value
                    : (int) this.v.value + "";
            };
        }
    }

    public static class Value {
        public double value;

        public Value(double value) {
            this.value = value;
        }
    }

}













