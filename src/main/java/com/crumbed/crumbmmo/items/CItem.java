package com.crumbed.crumbmmo.items;

import com.crumbed.crumbmmo.items.components.ItemStats;
import com.crumbed.crumbmmo.stats.*;
import de.tr7zw.nbtapi.NBT;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class CItem {
    public ItemStack raw;
    private final String itemId;
    private String name;
    private Rarity rarity;
    private Material material;

    private ItemStats stats;
    private ArrayList<String> lore;

    public CItem(ItemStack item) {
        raw = item;
        var nbt = NBT.readNbt(item);
        if (!nbt.hasTag("item_id")) {
            itemId = STR."minecraft:\{item.getType()}";
            return;
        }

        itemId = nbt.getString("item_id");
        name = nbt.getString("name");
        rarity = Rarity.fromString(nbt.getString("rarity"));
        material = Material.getMaterial(nbt.getString("material"));
        stats = nbt.get("stats", ItemStats.handler);
        lore = new ArrayList<>(nbt.getStringList("lore").toListCopy());

        /*
        {
            name: "Aspect of the End",
            item_id: "aspect_of_the_end",
            rarity: "rare",
            material: "iron_sword",

            stats: {
                "damage": 100,
                "strength": 100
            },
            lore: [
                ???
            ],
        }
        */


        // nbt layout defines the CItem
    }


    public ItemStack getRawItem() { return raw; }
    public String getId() { return itemId; }
    public String getName() { return name; }
    public Rarity getRarity() { return rarity; }
    public Stat.Value getStat(Stat stat) { return new Stat.Value(stats.get(stat)); }
    public ItemStats getStats() { return stats; }
    public Material getMaterial() { return material; }

    public void setName(String name) {
        this.name = name;
        NBT.modify(raw, (nbt) -> {
            nbt.setString("name", name);
        });

        updateName();
    }
    public void setRarity(Rarity rarity) {
        this.rarity = rarity;
        NBT.modify(raw, (nbt) -> {
            nbt.setString("rarity", rarity.id());
        });

        updateName();
    }
    public void setStats(ItemStats stats) {
        this.stats = stats;
        NBT.modify(raw, (nbt) -> {
            nbt.set("stats", stats, ItemStats.handler);
        });

        updateLore();
    }

    public ArrayList<String> getFullLore() {
        var lore = new ArrayList<String>();
        var statsLore = stats.toLore();
        var baseLore = this.lore;

        if (!statsLore.isEmpty()) {
            lore.addAll(statsLore);
            lore.add("");
        }
        if (!baseLore.isEmpty()) {
            lore.addAll(baseLore);
            lore.add("");
        }

        lore.add(STR."\{rarity.color()}\{ChatColor.BOLD}\{rarity}");
        lore.add(STR."\{ChatColor.DARK_GRAY}id: \{itemId}");

        return lore;
    }

    private void updateName() {
        var meta = raw.getItemMeta();
        meta.setDisplayName(rarity.color() + name);
        raw.setItemMeta(meta);
    }

    private void updateLore() {
        var meta = raw.getItemMeta();
        meta.setLore(getFullLore());
        raw.setItemMeta(meta);
    }
}












































