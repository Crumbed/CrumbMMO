package com.crumbed.crumbmmo.items;

import com.google.gson.annotations.SerializedName;
import org.bukkit.ChatColor;

public enum Rarity {
    None,
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
            case Common, None -> ChatColor.WHITE;
            case Uncommon -> ChatColor.GREEN;
            case Rare -> ChatColor.BLUE;
            case Epic -> ChatColor.DARK_PURPLE;
            case Legendary -> ChatColor.GOLD;
            case Mythic -> ChatColor.LIGHT_PURPLE;
        };
    }
    public String toString() {
        return switch (this) {
            case None -> "";
            case Contraband -> "Contraband";
            case Common -> "Common";
            case Uncommon -> "Uncommon";
            case Rare -> "Rare";
            case Epic -> "Epic";
            case Legendary -> "Legendary";
            case Mythic -> "Mythic";
        };
    }

    public String id() {
        return switch (this) {
            case None -> "";
            case Contraband -> "contraband";
            case Common -> "common";
            case Uncommon -> "uncommon";
            case Rare -> "rare";
            case Epic -> "epic";
            case Legendary -> "legendary";
            case Mythic -> "mythic";
        };
    }

    public static Rarity fromString(String rarity) {
        switch (rarity.toLowerCase()) {
            case ""             :   return None;
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
