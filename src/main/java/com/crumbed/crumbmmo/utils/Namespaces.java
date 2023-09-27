package com.crumbed.crumbmmo.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

public class Namespaces {
    public static NamespacedKey MOB_LEVEL;
    public static NamespacedKey MOB_NAME;
    public static NamespacedKey MOB_ID;
    public static NamespacedKey SESSION;

    public static void initNamespaces(Plugin plugin) {
        MOB_LEVEL = new NamespacedKey(plugin, "level");
        MOB_NAME = new NamespacedKey(plugin, "name");
        MOB_ID = new NamespacedKey(plugin, "id");
        SESSION = new NamespacedKey(plugin, "session");
    }
}
