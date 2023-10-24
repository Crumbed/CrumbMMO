package com.crumbed.crumbmmo.items;

import com.crumbed.crumbmmo.CrumbMMO;
import com.crumbed.crumbmmo.managers.CraftingManager;
import com.crumbed.crumbmmo.managers.ItemManager;
import com.crumbed.crumbmmo.utils.None;
import com.crumbed.crumbmmo.utils.Some;
import com.google.gson.annotations.SerializedName;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class CRecipe {
    public final HashMap<String, RecipeItem> key;
    public final RecipeItem result;
    public final boolean shapeless;
    public final String[][] patterns;

    private CRecipe() {
        this.key = new HashMap<>();
        this.result = null;
        this.shapeless = false;
        this.patterns = new String[0][0];
    }

    public RecipeItem[][] toItems(String[] pattern) {
        var matrix = new RecipeItem[pattern.length][pattern[0].length()];
        for (var y = 0; y < pattern.length; y++) {
            for (var x = 0; x < pattern[0].length(); x++) matrix[y][x] = key.get(pattern[y].substring(x, x+1));
        }
        return matrix;
    }

    public String[] conformShapeless(ItemStack[][] matrix) {
        if (!shapeless) return null;
        var pattern = new String[matrix.length];
        for (int y = 0; y < matrix.length; y++) {
            var builder = new StringBuilder();
            for (int x = 0; x < matrix[0].length; x++) {
                var mItem = matrix[y][x];
                if (mItem == null) {
                    builder.append(" ");
                    continue;
                }

                String id;
                var optItem = CItem.fromItemStack(mItem);
                if (!(optItem instanceof Some<CItem> cItem)) return null;
                id = cItem.inner().getId().substring(cItem.inner().getId().indexOf(':') + 1);
                var rItem = new CRecipe.RecipeItem(id, mItem.getAmount());

                for (var set : key.entrySet()) {
                    if (!set.getValue().itemId().equals(id)) continue;
                    if (set.getValue().count() > rItem.count()) continue;
                    builder.append(set.getKey());
                    break;
                }
            }

            pattern[y] = builder.toString();
        }

        for (var s : pattern)
            Bukkit.getLogger().info(s);
        return pattern;
    }

    public Result asResult(RecipeItem[][] pattern) { return new Result(result, pattern); }

    public record Result(
            RecipeItem resultItem,
            RecipeItem[][] pattern
    ) {}

    public record RecipeItem(
            @SerializedName("item-id")
            String itemId,
            int count
    ) {}
}
