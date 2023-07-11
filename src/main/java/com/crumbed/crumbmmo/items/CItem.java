package com.crumbed.crumbmmo.items;

import com.crumbed.crumbmmo.managers.ItemManager;
import com.crumbed.crumbmmo.stats.*;
import com.crumbed.crumbmmo.utils.Option;
import com.google.gson.annotations.SerializedName;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;

public class CItem {
    public static final CItem EMPTY = new CItem();
    @SerializedName("display-name")
    private String name;
    private HashMap<GenericStat, Double> stats;
    private ArrayList<String> lore;
    private Rarity rarity;
    @SerializedName("item-id")
    private final String itemId;
    private String material;
    private String type;

    private transient ItemStack rawItem;


    public CItem() {
        itemId = "NULL";
        name = "";
        rarity = Rarity.Contraband;
        stats = new HashMap<>();
        lore = new ArrayList<>();
        material = "AIR";
        type = "null";
        rawItem = new ItemStack(Material.AIR);
    }
    public CItem(
            String itemId,
            String name,
            Rarity rarity,
            HashMap<GenericStat, Double> stats,
            ArrayList<String> lore,
            String material,
            String type,
            ItemStack rawItem
    ) {
        this.itemId = itemId;
        this.name = name;
        this.rarity = rarity;
        this.stats = stats;
        this.lore = lore;
        this.material = material;
        this.type = type;
        this.rawItem = rawItem;
    }

    public static Option<CItem> fromItemStack(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) return Option.some(CItem.EMPTY);
        ItemMeta meta = itemStack.getItemMeta();
        ArrayList<String> itemLore = (ArrayList<String>) meta.getLore();
        if (itemLore == null || !itemLore.get(itemLore.size() - 1).contains("id: ")) {
            return Option.some(new CItem(
                    "vanilla::" + itemStack.getType(),
                    itemStack.getType().name(),
                    Rarity.Common,
                    new HashMap<>(),
                    new ArrayList<>(),
                    itemStack.getType().toString(),
                    "item",
                    itemStack
            ));
        }

        String itemId = itemLore.get(itemLore.size() - 1).substring(6);
        //Bukkit.getLogger().info(itemId);
        ItemManager itemManager = ItemManager
                .INSTANCE
                .unwrap();
        if (!itemManager.itemReg.containsKey(itemId)) return Option.none();
        CItem item = itemManager.itemReg.get(itemId);

        // check for addons

        return Option.some(item);
    }

    public String getId() { return itemId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Rarity getRarity() { return rarity; }
    public void setRarity(Rarity rarity) { this.rarity = rarity; }
    public Stat getStat(String id) {
        GenericStat genStat = GenericStat.fromString(id).unwrap();
        if (stats.get(genStat) == null) return Stat.fromGeneric(genStat, 0D);
        return Stat.fromGeneric(genStat, stats.get(genStat));
    }
    public HashMap<GenericStat, Double> getStats() { return stats; }
    public void setStats(HashMap<GenericStat, Double> stats) { this.stats = stats; }
    public Material getMaterial() { return Material.getMaterial(material); }
    public void setMaterial(Material material) { this.material = material.toString(); }
    public ItemStack getRawItem() { return rawItem; }

    public void initLoaded() {
        this.rawItem = ItemManager
                .INSTANCE
                .unwrap()
                .createItem(this);
    }

    public ArrayList<String> getFullLore() {
        ArrayList<String> lore = new ArrayList<>();
        Damage damage = (Damage) getStat("damage");
        Strength strength = (Strength) getStat("strength");
        CritChance critChance = (CritChance) getStat("crit-chance");
        CritDamage critDamage = (CritDamage) getStat("crit-damage");
        Health health = (Health) getStat("health");
        Defense defense = (Defense) getStat("defense");
        Mana mana = (Mana) getStat("mana");

        if (damage.getValue() != 0D) lore.add(damage.display());
        if (strength.getValue() != 0D) lore.add(strength.display());
        if (critChance.getValue() != 0D) lore.add(critChance.display());
        if (critDamage.getValue() != 0D) lore.add(critDamage.display());
        if (health.getValue() != 0D) lore.add(health.display());
        if (defense.getValue() != 0D) lore.add(defense.display());
        if (mana.getValue() != 0D) lore.add(mana.display());
        // add enchants here

        if (!lore.isEmpty()) lore.add("");
        else lore.addAll(this.lore);
        lore.add(String.format(
                "%s%s%s %s",
                rarity.color(),
                ChatColor.BOLD,
                rarity,
                this.type
        ));

        lore.add(String.format(
                "%sid: %s",
                ChatColor.DARK_GRAY,
                this.itemId
        ));

        return lore;
    }

}












































