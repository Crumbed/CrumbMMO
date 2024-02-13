package com.crumbed.crumbmmo.genericEvents;


import com.crumbed.crumbmmo.items.CRecipe;
import com.crumbed.crumbmmo.managers.CraftingManager;
import com.crumbed.crumbmmo.managers.ItemManager;
import com.crumbed.crumbmmo.utils.Some;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.bukkit.Bukkit.craftItem;

public class CraftingListener implements Listener {

    @EventHandler
    public void craft(InventoryClickEvent e) {
        if (!(e.getClickedInventory() instanceof CraftingInventory grid)) return;
        if (e.getSlot() != 0 || grid.getItem(0) == null || grid.isEmpty()) return;
        var matrix = grid.getMatrix();
        var optRecipe = CraftingManager
                .INSTANCE
                .matchRecipe(grid);
        if (!(optRecipe instanceof Some<CRecipe.Result> sRecipe)) {
            grid.setResult(null);
            return;
        }
        var recipe = sRecipe.inner();
        var pattern = Arrays.stream(recipe.pattern())
                .flatMap(Stream::of)
                .toArray(CRecipe.RecipeItem[]::new);
        var craftCount = 0;
        for (var rItem : pattern) {
            if (rItem == null) Bukkit.getLogger().info("[]");
            else Bukkit.getLogger().info("id: " + rItem.itemId() + " x" + rItem.count());
        }
        for (var i = 0; i < matrix.length; i++) {
            if (matrix[i] == null) continue;
            var newCount = matrix[i].getAmount() / pattern[i].count();
            if (newCount < craftCount || craftCount == 0) craftCount = newCount;
        }


        switch (e.getClick()) {
            case LEFT -> {
                CraftingManager.craftItem(matrix, pattern, 1);
                e.setCursor(grid.getResult());
                if (craftCount == 1) grid.setResult(null);
            }
            case SHIFT_LEFT -> {
                if (grid.getResult().getMaxStackSize() == 1) craftCount = 1;
                e.setCancelled(true);
                CraftingManager.craftItem(matrix, pattern, craftCount);
                for (var i = 0; i < craftCount; i++)
                    e.getWhoClicked().getInventory().addItem(grid.getResult().clone());
                grid.setResult(null);
            }
            case NUMBER_KEY -> {
                if (e.getWhoClicked().getInventory().getItem(e.getHotbarButton()) != null) break;
                CraftingManager.craftItem(matrix, pattern, 1);
                e.getWhoClicked().getInventory().setItem(e.getHotbarButton(), grid.getResult());
                if (craftCount == 1) grid.setResult(null);
            }
            case DOUBLE_CLICK -> {
                Bukkit.getLogger().info("wtf is double click");
                grid.setResult(null);
                e.setCancelled(true);
            }
            default -> e.setCancelled(true);
        }
    }

    @EventHandler
    public void moveItem(PrepareItemCraftEvent e) {
        var grid = e.getInventory();
        if (!(CraftingManager.INSTANCE.matchRecipe(grid) instanceof Some<CRecipe.Result> s)) return;
        var recipe = s.inner();
        var resultItem = recipe.resultItem();

        ItemStack item;
        if (resultItem.itemId().toUpperCase().equals(resultItem.itemId())) {
            item = new ItemStack(Material.getMaterial(resultItem.itemId()), resultItem.count());
        } else {
            var citem = ItemManager
                    .INSTANCE
                    .itemReg
                    .get(resultItem.itemId());
            item = citem.getRawItem();
            item.setAmount(resultItem.count());
        }

        grid.setResult(item);
    }
}





















