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
        switch (this) {
            case Contraband :   return ChatColor.RED;
            case Common     :   return ChatColor.WHITE;
            case Uncommon   :   return ChatColor.GREEN;
            case Rare       :   return ChatColor.BLUE;
            case Epic       :   return ChatColor.DARK_PURPLE;
            case Legendary  :   return ChatColor.GOLD;
            case Mythic     :   return ChatColor.LIGHT_PURPLE;
        }
        return ChatColor.RED;
    }
    public String toString() {
        switch (this) {
            case Contraband :   return "contraband";
            case Common     :   return "common";
            case Uncommon   :   return "uncommon";
            case Rare       :   return "rare";
            case Epic       :   return "epic";
            case Legendary  :   return "legendary";
            case Mythic     :   return "mythic";
        }
        return null;
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
