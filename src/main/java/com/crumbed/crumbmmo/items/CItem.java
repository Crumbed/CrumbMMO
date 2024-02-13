package com.crumbed.crumbmmo.items;

import com.crumbed.crumbmmo.items.components.ItemLore;
import com.crumbed.crumbmmo.items.components.ItemStats;
import com.crumbed.crumbmmo.managers.ItemManager;
import com.crumbed.crumbmmo.stats.*;
import com.crumbed.crumbmmo.utils.None;
import com.crumbed.crumbmmo.utils.Option;
import com.crumbed.crumbmmo.utils.Some;
import com.google.gson.annotations.SerializedName;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;

public class CItem {
    // A blank item
    public static final CItem EMPTY = new CItem();
    @SerializedName("display-name")
    private String name;
    private ArrayList<ItemComponent> components;
    private Rarity rarity;
    @SerializedName("item-id")
    private final String itemId;
    private String material;
    private String type;

    private transient ItemStack rawItem;


    /**
     * Constructs a blank CItem
     */
    public CItem() {
        itemId = "NULL";
        name = "";
        rarity = Rarity.Contraband;
        components = new ArrayList<>();
        material = "AIR";
        type = "null";
        rawItem = new ItemStack(Material.AIR);
    }
    public CItem(
            String itemId,
            String name,
            Rarity rarity,
            String material,
            String type,
            ItemStack rawItem
    ) {
        this.itemId = itemId;
        this.name = name;
        this.rarity = rarity;
        this.components = new ArrayList<>();
        this.material = material;
        this.type = type;
        this.rawItem = rawItem;
    }

    public static Option<CItem> fromItemStack(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) return Option.some(CItem.EMPTY);
        var meta = itemStack.getItemMeta();
        var itemLore = (ArrayList<String>) meta.getLore();
        if (itemLore == null || !itemLore.get(itemLore.size() - 1).contains("id: ")) {
            return Option.some(new CItem(
                    "vanilla:" + itemStack.getType(),
                    itemStack.getType().name(),
                    Rarity.Common,
                    itemStack.getType().toString(),
                    "item",
                    itemStack
            ));
        }

        var itemId = itemLore.get(itemLore.size() - 1).substring(6);
        //Bukkit.getLogger().info(itemId);
        if (!ItemManager.INSTANCE.itemReg.containsKey(itemId)) return Option.none();
        var item = ItemManager.INSTANCE.itemReg.get(itemId);

        // check for addons

        return Option.some(item);
    }

    public String getId() { return itemId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Rarity getRarity() { return rarity; }
    public void setRarity(Rarity rarity) { this.rarity = rarity; }
    public Stat.Value getStat(String id) {
        var oStats = getComponent(ItemStats.class);
        var genStat = Stat.fromString(id).unwrap();
        if (oStats.isNone()) return new Stat.Value(0);
        return new Stat.Value(oStats.unwrap().get(genStat));
    }
    public HashMap<Stat, Double> getStats() {
        var oStats = getComponent(ItemStats.class);
        return switch (oStats) {
            case Some<ItemStats> s -> s.inner().getAll();
            case None<ItemStats> ignored -> new HashMap<>();
        };
    }
    public void setStats(HashMap<Stat, Double> stats) {
        var oStats = getComponent(ItemStats.class);
        switch (oStats) {
            case Some<ItemStats> s -> s.inner().setAll(stats);
            case None<ItemStats> ignored -> addComponent(new ItemStats(stats));
        }
    }
    public Material getMaterial() { return Material.getMaterial(material); }
    public void setMaterial(Material material) { this.material = material.toString(); }
    public ItemStack getRawItem() { return rawItem; }

    public void initLoaded() {
        this.rawItem = ItemManager
                .INSTANCE
                .createItem(this);
    }
    public void initLoaded(ItemManager manager) {
        this.rawItem = manager.createItem(this);
    }

    public ArrayList<String> getFullLore() {
        var lore = new ArrayList<String>();
        for (var comp : components) {
            switch (comp) {
                case null -> {}
                case ItemLore l -> lore.addAll(l.toLore());
                case ItemStats s -> lore.addAll(s.toLore());
                default -> lore.addAll(comp.toLore());
            }
            lore.add("");
        }

        lore.add(String.format(
                "%s%s%s %s",
                rarity.color(),
                ChatColor.BOLD,
                rarity,
                this.type.substring(0, 1).toUpperCase() + this.type.substring(1).toLowerCase()
        ));
        lore.add(String.format(
                "%sid: %s",
                ChatColor.DARK_GRAY,
                this.itemId
        ));

        return lore;
    }

    public void addComponent(ItemComponent c) {
        int i = 0;
        // makes sure that when we add components
        // they are sorted by their id
        for (; i < components.size(); i++) {
            var cI = components.get(i);
            if (c.id() <= cI.id()) break;
        }

        components.add(i, c);
    }

    public <T extends ItemComponent> Option<T> getComponent(Class<T> compType) {
        int compId;
        try {
            compId = compType.getField("ID").getInt(null); // Extract the value of "ID"
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return Option.none();
        }

        for (var c : components) {
            if (c.id() != compId) continue;
            return Option.some((T) c);
        }
        return Option.none();
    }

}












































