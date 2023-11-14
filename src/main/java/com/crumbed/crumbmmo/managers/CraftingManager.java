package com.crumbed.crumbmmo.managers;

import com.crumbed.crumbmmo.CrumbMMO;
import com.crumbed.crumbmmo.items.CRecipe;
import com.crumbed.crumbmmo.utils.Option;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.stream;

public class CraftingManager {
    public static CraftingManager INSTANCE = null;

    private HashMap<String, CRecipe> recipes;

    private CraftingManager() { recipes = new HashMap<>(); }

    public static CraftingManager init(CrumbMMO plugin) {
        if (INSTANCE != null) return INSTANCE;
        var f = new File(plugin.getDataFolder(), "CustomRecipes.json");

        if (!f.exists()) try {
            f.createNewFile();
            return new CraftingManager();
        } catch (IOException ignored){}
        else try (var lines = Files.lines(f.toPath())) {
            var recipes = String.join("\n", lines.toList());
            Gson gson = new Gson();
            return gson.fromJson(recipes, CraftingManager.class);
        } catch (IOException ignored){}
        return null;
    }


    public Option<CRecipe.Result> matchRecipe(CraftingInventory grid) {
        var matrix = removeEmptyRows(grid.getMatrix());
        for (var recipeSet : recipes.entrySet()) {
            var recipe = recipeSet.getValue();
            if (recipe.shapeless) continue;
            var patterns = Arrays.stream(recipe.patterns)
                    .filter(x -> x.length == matrix.length)
                    .filter(x -> x[0].length() == matrix[0].length)
                    .map(recipe::toItems)
                    .toList();

            for (var pattern : patterns) {
                var isMatch = loopXY(matrix[0].length, matrix.length, (x, y) -> {
                    if ((pattern[y][x] == null && matrix[y][x] != null)
                            || (pattern[y][x] != null && matrix[y][x] == null)
                    ) return false;
                    else if (pattern[y][x] != null &&
                            pattern[y][x].itemId().toUpperCase().equals(pattern[y][x].itemId())
                    ) {
                        var material = matrix[y][x].getType();
                        if (!material.equals(Material.getMaterial(pattern[y][x].itemId()))) return false;
                    } else if (pattern[y][x] != null) {
                        if (!ItemManager.INSTANCE.itemReg.containsKey(pattern[y][x].itemId())) {
                            CrumbMMO.getInstance().getLogger().info(ChatColor.RED + "ERROR: item id: " + pattern[y][x].itemId() + " doesn't exist in recipe " + recipeSet.getKey());
                            return false;
                        }
                        var meta = matrix[y][x].getItemMeta();
                        if (meta == null) return false;
                        if (!meta.hasLore()) return false;
                        var id = meta.getLore().get(meta.getLore().size() - 1).substring(6);
                        if (!pattern[y][x].itemId().equals(id)) return false;
                    }
                    if (pattern[y][x] == null && matrix[y][x] == null) return true;

                    var requiredAmount = pattern[y][x].count();
                    var providedAmount = matrix[y][x].getAmount();
                    return providedAmount >= requiredAmount;
                });
                if (isMatch) return Option.some(recipe.asResult(pattern));
            }
        }
        return checkShapeless(grid.getMatrix());
    }

    public Option<CRecipe.Result> checkShapeless(ItemStack[] matrix) {
        for (var recipeSet : recipes.entrySet()) {
            var recipe = recipeSet.getValue();
            if (!recipe.shapeless) continue;
            var remainingItems = Arrays.stream(recipe.toItems(recipe.patterns[0]))
                    .flatMap(Stream::of)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(ArrayList::new));

            var isMatch = false;
            for (var pos = matrix.length-1; pos >= 0; pos--) {
                if (isMatch && matrix[pos] != null) {
                    isMatch = false;
                    break;
                }
                if (matrix[pos] == null) continue;
                var rItem = remainingItems.get(remainingItems.size()-1);

                if (rItem.itemId().toUpperCase().equals(rItem.itemId())) {
                    if (!matrix[pos].getType().equals(Material.getMaterial(rItem.itemId()))) continue;
                } else {
                    if (!ItemManager.INSTANCE.itemReg.containsKey(rItem.itemId())) {
                        CrumbMMO.getInstance().getLogger().info(ChatColor.RED + "ERROR: item id: " + rItem.itemId() + " doesn't exist in recipe " + recipeSet.getKey());
                        break;
                    }

                    var meta = matrix[pos].getItemMeta();
                    if (meta == null) continue;
                    if (!meta.hasLore()) continue;
                    var id = meta.getLore().get(meta.getLore().size() - 1).substring(6);
                    if (!rItem.itemId().equals(id)) continue;
                }

                if (matrix[pos].getAmount() < rItem.count()) continue;
                remainingItems.remove(remainingItems.size()-1);
                if (remainingItems.isEmpty()) isMatch = true;
            }

            if (!isMatch) continue;
            var matrix2d = new ItemStack[3][3];
            for (var y = 0; y < 3; y++) {
                for (var x = 0; x < 3; x++) {
                    matrix2d[y][x] = matrix[(x + 1) * (y + 1) - 1];
                }
            }
            return Option.some(recipe.asResult(recipe.toItems(recipe.conformShapeless(matrix2d))));
        }
        return Option.none();
    }



    @FunctionalInterface
    public interface XYFunction<One, Two> { boolean apply(One x, Two y); }

    public static boolean loopXY(int xBound, int yBound, XYFunction<Integer, Integer> fn) {
        for (var y = 0; y < yBound; y++) {
            for (var x = 0; x < xBound; x++) {
                if (!fn.apply(x, y)) return false;
            }
        }
        return true;
    }


    public ItemStack[][] removeEmptyRows(ItemStack[] matrix) {
        if (stream(matrix).filter(Objects::nonNull).toList().isEmpty()) return new ItemStack[0][0];
        else if (matrix.length == 4) return check2x2(matrix);

        var newMatrix = new ArrayList<>(List.of(
                new ArrayList<>(stream(new ItemStack[] { matrix[0], matrix[1], matrix[2] }).toList()),
                new ArrayList<>(stream(new ItemStack[] { matrix[3], matrix[4], matrix[5] }).toList()),
                new ArrayList<>(stream(new ItemStack[] { matrix[6], matrix[7], matrix[8] }).toList())
        ));

        var col1 = stream(new ItemStack[] {
                newMatrix.get(0).get(0),
                newMatrix.get(1).get(0),
                newMatrix.get(2).get(0)
        }).filter(Objects::nonNull).toList().isEmpty();
        var col2 = stream(new ItemStack[] {
                newMatrix.get(0).get(2),
                newMatrix.get(1).get(2),
                newMatrix.get(2).get(2)
        }).filter(Objects::nonNull).toList().isEmpty();

        if (col1) {
            newMatrix.get(0).remove(0);
            newMatrix.get(1).remove(0);
            newMatrix.get(2).remove(0);
        }
        if (col2) {
            newMatrix.get(0).remove(newMatrix.get(0).size() - 1);
            newMatrix.get(1).remove(newMatrix.get(1).size() - 1);
            newMatrix.get(2).remove(newMatrix.get(2).size() - 1);
        }

        if (newMatrix.get(0).stream().filter(Objects::nonNull).toList().isEmpty()) newMatrix.remove(0);
        if (newMatrix.get(newMatrix.size() - 1).stream().filter(Objects::nonNull).toList().isEmpty())
            newMatrix.remove(newMatrix.size() - 1);

        if (newMatrix.get(0).size() <= 2) {
            for (var i = newMatrix.get(0).size()-1; i >= 0; i--) {
                var col = new ArrayList<ItemStack>();
                for (var row : newMatrix) col.add(row.get(i));
                if (!col.stream().filter(Objects::nonNull).toList().isEmpty()) continue;
                for (var row : newMatrix) row.remove(i);
            }
        }
        if (newMatrix.size() <= 2) {
            for (var i = newMatrix.size()-1; i >= 0; i--) {
                var isEmpty = newMatrix.get(i)
                        .stream()
                        .filter(Objects::nonNull)
                        .toList()
                        .isEmpty();
                if (isEmpty) newMatrix.remove(i);
            }
        }

        return newMatrix.stream()
                .map(x -> x.toArray(new ItemStack[0]))
                .toList()
                .toArray(new ItemStack[0][0]);
    }


    public ItemStack[][] check2x2(ItemStack[] matrix) {
        var newMatrix = new ArrayList<>(List.of(
                new ArrayList<>(stream(new ItemStack[] { matrix[0], matrix[1] }).toList()),
                new ArrayList<>(stream(new ItemStack[] { matrix[2], matrix[3] }).toList())
        ));

        final var col1 = stream(new ItemStack[] {
                matrix[0],
                matrix[2]
        }).filter(Objects::nonNull).toList().isEmpty();
        final var col2 = stream(new ItemStack[] {
                matrix[1],
                matrix[3]
        }).filter(Objects::nonNull).toList().isEmpty();
        if (col1) {
            newMatrix.get(0).remove(0);
            newMatrix.get(1).remove(0);
        }
        if (col2) {
            newMatrix.get(0).remove(newMatrix.size() - 1);
            newMatrix.get(1).remove(newMatrix.size() - 1);
        }

        if (newMatrix.get(0).stream().filter(Objects::nonNull).toList().isEmpty()) newMatrix.remove(0);
        if (newMatrix.get(1).stream().filter(Objects::nonNull).toList().isEmpty()) newMatrix.remove(1);

        return newMatrix.stream()
                .map(x -> x.toArray(new ItemStack[0]))
                .toList()
                .toArray(new ItemStack[0][0]);
    }


    public static void craftItem(ItemStack[] matrix, CRecipe.RecipeItem[] pattern, int amount) {
        for (var i = 0; i < matrix.length; i++) {
            if (matrix[i] == null) continue;
            Bukkit.getLogger().info("cost: " + (matrix[i].getAmount() - pattern[i].count() * amount));
            matrix[i].setAmount(matrix[i].getAmount() - pattern[i].count() * amount);
        }
    }
}































