package com.crumbed.crumbmmo.managers;

import com.crumbed.crumbmmo.CrumbMMO;
import com.crumbed.crumbmmo.ecs.CPlayer;
import com.crumbed.crumbmmo.items.CItem;
import com.crumbed.crumbmmo.items.ItemComponent;
import com.crumbed.crumbmmo.items.Rarity;
import com.crumbed.crumbmmo.jsonUtils.ComponentAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
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

    @SerializedName("item-ids")
    private ArrayList<String> itemIds;
    private JsonObject items;
    /**
     * map from item-id -> CItem
     */
    public transient HashMap<String, CItem> itemReg;
    public transient CrumbMMO plugin;

    public ItemManager(CrumbMMO plugin) {
        itemIds = new ArrayList<>();
        items = new JsonObject();
        itemReg = new HashMap<>();
        this.plugin = plugin;
    }




    public static void reload() {
        var plugin = INSTANCE.plugin;
        INSTANCE = new Builder(plugin).create();
    }


    public void checkOutdatedItems(Inventory inv) {
        for (int i = 0; i < inv.getSize(); ++i) {
            ItemStack item = inv.getItem(i);
            assert item != null;
            if (item.getType() == Material.AIR) continue;
            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            List<String> lore = meta.getLore();
            if (!lore.get(lore.size()-1).contains("id: ")) continue;

            String id = lore.get(lore.size()-1).substring(6);
            if (!itemReg.containsKey(id)) {
                inv.setItem(i, new ItemStack(Material.AIR));
                if (!(inv instanceof PlayerInventory)) continue;
                ((PlayerInventory) inv)
                    .getHolder()
                    .sendMessage(ChatColor.RED + "An unrecognised item was removed from your inventory, please contact staff if you believe this was a mistake.");
                Bukkit.getLogger().info(item.getItemMeta().getDisplayName() + ", was removed from " + ((PlayerInventory) inv).getHolder().getName() + "'s inventory.");
                continue;
            }
            CItem freshItem = itemReg.get(id);
            CItem currItem = CItem.fromItemStack(item).unwrap();

            if (!currItem.getName().equals(freshItem.getName()) ||
                !currItem.getMaterial().equals(freshItem.getMaterial()) ||
                !currItem.getStats().equals(freshItem.getStats()) ||
                !currItem.getRarity().equals(freshItem.getRarity())
            ) {
                inv.setItem(i, freshItem.getRawItem());
                if (inv instanceof PlayerInventory) {
                    CPlayer player = PlayerManager
                        .INSTANCE
                        .getPlayer(((PlayerInventory) inv)
                            .getHolder()
                            .getUniqueId())
                        .unwrap();
                    PlayerManager
                        .INSTANCE
                        .syncPlayerInv(player);
                }
            }
        }
    }

    public ItemStack createItem(CItem citem) {
        ItemStack item = new ItemStack(citem.getMaterial());
        ChatColor color = citem.getRarity().color();
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color + citem.getName());
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setUnbreakable(true);
        meta.setLore(citem.getFullLore());
        item.setItemMeta(meta);

        return item;
    }


    public static class Builder {
        public CrumbMMO plugin;

        public Builder(CrumbMMO plugin) {
            this.plugin = plugin;
        }

        private int componentCount = 0;
        public <T extends ItemComponent> Builder with(Class<T> component) {
            try {
                component.getField("ID").setInt(null, componentCount);
                componentCount += 1;
            } catch(NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            return this;
        }

        public ItemManager create() {
            if (INSTANCE != null) return INSTANCE;
            var f = new File(plugin.getDataFolder(), "CustomItems.json");

            ItemManager ins = null;
            if (!f.exists()) try {
                f.createNewFile();
                return new ItemManager(plugin);
            } catch (IOException ignored){}
            else try (var lines = Files.lines(f.toPath())) {
                var customItems = String.join("\n", lines
                    .collect(Collectors.toList()));
                var gson = new Gson();
                ins = gson.fromJson(customItems, ItemManager.class);
            } catch (IOException ignored){}
            assert ins != null;
            ins.itemReg = new HashMap<>();
            var gson = new Gson().newBuilder()
                .registerTypeAdapter(ItemComponent.class, new ComponentAdapter<ItemComponent>())
                .create();

            for (String id : ins.itemIds) {
                Bukkit.getLogger().info("Attempting to load item: " + id);
                var jsonItem = ins.items.get(id);
                var item = gson.fromJson(jsonItem, CItem.class);
                item.initLoaded(ins);

                ins.itemReg.put(id, item);
            }
            Bukkit.getLogger().info(ins.itemReg.containsKey("cleaver") +"");

            var menuGlassItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            var menuGlassMeta = menuGlassItem.getItemMeta();
            menuGlassMeta.setDisplayName(" ");
            menuGlassItem.setItemMeta(menuGlassMeta);
            var menuGlass = new CItem("menu_glass",
                " ",
                Rarity.Contraband,
                "null",
                "null",
                menuGlassItem
            );

            ins.itemReg.put("black_menu_glass", menuGlass);
            return ins;
        }
    }


    public Stream<String> getItemIds() {
        return itemIds.stream();
    }

}



































