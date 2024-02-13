package com.crumbed.crumbmmo.items;

import com.google.gson.annotations.SerializedName;
import org.bukkit.ChatColor;

public enum Rarity {
    @SerializedName("contraband")
    Contraband,
    @SerializedName("common")
    Common,
    @SerializedName("uncommon")
    Uncommon,
    @SerializedName("rare")
    Rare,
    @SerializedName("epic")
    Epic,
    @SerializedName("legendary")
    Legendary,
    @SerializedName("mythic")
    Mythic;

    public ChatColor color() {
        return switch (this) {
            case Contraband -> ChatColor.RED;
            case Common -> ChatColor.WHITE;
            case Uncommon -> ChatColor.GREEN;
            case Rare -> ChatColor.BLUE;
            case Epic -> ChatColor.DARK_PURPLE;
            case Legendary -> ChatColor.GOLD;
            case Mythic -> ChatColor.LIGHT_PURPLE;
        };
    }
    public String toString() {
        return switch (this) {
            case Contraband -> "Contraband";
            case Common -> "Common";
            case Uncommon -> "Uncommon";
            case Rare -> "Rare";
            case Epic -> "Epic";
            case Legendary -> "Legendary";
            case Mythic -> "Mythic";
        };
    }

    public static Rarity fromString(String rarity) {
        switch (rarity.toLowerCase()) {
            case "contraband"   :   return Contraband;
            case "common"       :   return Common;
            case "uncommon"     :   return Uncommon;
            case "rare"         :   return Rare;
            case "epic"         :   return Epic;
            case "legendary"    :   return Legendary;
            case "mythic"       :   return Mythic;
        }
        return null;
    }
}
