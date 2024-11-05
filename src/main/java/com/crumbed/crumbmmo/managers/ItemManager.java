package com.crumbed.crumbmmo.managers;

import com.crumbed.crumbmmo.CrumbMMO;
import com.crumbed.crumbmmo.ecs.CPlayer;
import com.crumbed.crumbmmo.items.CItem;
import com.crumbed.crumbmmo.items.ItemComponent;
import com.crumbed.crumbmmo.jsonUtils.ComponentAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.NBTHandler;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Manager for all Item related activities
 */
public class ItemManager {
    public static ItemManager INSTANCE = null;

    private ArrayList<String> itemIds;
    private ReadWriteNBT items;
    private HashMap<String, ReadableNBT> itemReg;
    public CrumbMMO plugin;

    public ItemManager(CrumbMMO plugin) {
        itemIds = new ArrayList<>();
        itemReg = new HashMap<>();
        this.plugin = plugin;
    }

    public static ItemManager init(CrumbMMO plugin) {
        if (INSTANCE != null) return INSTANCE;
        var f = new File(plugin.getDataFolder(), "custom_items.snbt");

        var ins = new ItemManager(plugin);
        if (!f.exists()) try {
            f.createNewFile();
            var writer = new FileWriter(f);
            writer.write("{item_ids: [], items: {}}");
            writer.close();
            return ins;
        } catch (IOException ignored){}
        else try (var lines = Files.lines(f.toPath())) {
            var customItems = String.join("\n", lines
                .collect(Collectors.toList()));
            var nbt = NBT.parseNBT(customItems);
            ins.itemIds = new ArrayList<>(nbt.getStringList("item_ids").toListCopy());
            ins.items = nbt.getOrCreateCompound("items");
        } catch (IOException ignored){}
        assert ins != null;

        ins.itemReg = new HashMap<>();

        for (String id : ins.itemIds) {
            Bukkit.getLogger().info("Attempting to load item: " + id);
            var item = ins.items.getCompound(id);
            ins.itemReg.put(id, item);
        }

        final var menuGlassNbt = "{name: \" \", item_id: \"black_menu_glass\", rarity: \"\", material: \"black_stained_glass_pane\"}";
        ins.itemReg.put("black_menu_glass", NBT.parseNBT(menuGlassNbt));
        return ins;
    }


    public static void reload() {
        var plugin = INSTANCE.plugin;
        INSTANCE = null;
        INSTANCE = init(plugin);
    }

    public CItem getItem(String id) {
        var cnbt = itemReg.get(id);
        return new CItem(cnbt);
    }

    public Stream<String> getItemIds() {
        return itemIds.stream();
    }
}



































