package com.crumbed.crumbmmo.managers;

import com.crumbed.crumbmmo.CrumbMMO;
import com.crumbed.crumbmmo.ecs.CEntity;
import com.crumbed.crumbmmo.ecs.CPlayer;
import com.crumbed.crumbmmo.ecs.systems.PlayerInvUpdate;
import com.crumbed.crumbmmo.items.CItem;
import com.crumbed.crumbmmo.jsonUtils.JsonPlayerData;
import com.crumbed.crumbmmo.utils.None;
import com.crumbed.crumbmmo.utils.Option;
import com.crumbed.crumbmmo.utils.Some;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.crumbed.crumbmmo.commands.BrigadierCommand.getArg;

public class PlayerManager {
    public static PlayerManager INSTANCE = null;
    /**
     * Map from MC UUID -> Entity ID
     */
    private HashMap<UUID, Integer> playerIds;
    private JsonPlayerData playerData;
    /**
     * Map form Entity ID -> Health scale
     */
    private HashMap<Integer, Double> playerHealthScales;

    private PlayerManager(CrumbMMO plugin) {
        playerIds = new HashMap<>();
        playerHealthScales = new HashMap<>();
        File f = new File(plugin.getDataFolder(), "PlayerData.json");

        if (!f.exists()) try {
            f.createNewFile();
            playerData = new JsonPlayerData();
        } catch (IOException ignored){}
        else try (Stream<String> lines = Files.lines(f.toPath())) {
            String jsonPlayerData = String.join("\n", lines
                    .collect(Collectors.toList()));
            Gson gson = new Gson();
            playerData = gson.fromJson(jsonPlayerData, JsonPlayerData.class);
        } catch (IOException ignored){}
    }
    public static void init(CrumbMMO plugin) {
        if (INSTANCE == null) INSTANCE = new PlayerManager(plugin);
    }

    /**
     * @return Map from Entity ID -> Health scale as Double
     */
    public HashMap<Integer, Double> getHealthScales() { return playerHealthScales; }

    public void addPlayer(final Player player) {
        CPlayer p;
        // if player is new
        if (!playerData.isPlayerRegistered(player.getUniqueId())) {
            p = CPlayer.newPlayer(player);
        } else {
            p = playerData.loadPlayer(player.getUniqueId());
        }
        EntityManager.INSTANCE
                .addEntity(p);

        // Calculate player health scale
        double healthScale = StatManager
            .INSTANCE
            .unwrap()
            .calcHealthScale(p.getStats().health);
        PlayerManager.INSTANCE
                .getHealthScales()
                .put(p.id, healthScale);
        p.rawPlayer.setHealthScale(healthScale);

        PlayerManager.INSTANCE
                .syncPlayerInv(p);

        playerIds.put(p.getUUID(), p.id);
    }

    /**
     * @param   uuid    MC UUID of the Player
     * @return  Option.some if CPlayer exists
     * @return  Option.none otherwise
     */
    public Option<CPlayer> getPlayer(UUID uuid) {
        var id = playerIds.get(uuid);
        if (id == null) return Option.none();
        var entity = EntityManager
                .INSTANCE
                .getEntity(id);

        return switch (entity) {
            case Some<CEntity> ent -> switch (ent.inner()) {
                case CPlayer p -> Option.some(p);
                default -> Option.none();
            };
            case None<CEntity> ignored -> Option.none();
        };
    }

    /**
     * @param   player  Player
     * @return  Some if CPlayer exists
     * @return  None otherwise
     */
    public Option<CPlayer> getPlayer(Player player) {
        return switch (player) {
            case null -> Option.none();
            default -> getPlayer(player.getUniqueId());
        };
    }

    /**
     * @param   name  name of the Player
     * @return  Some if CPlayer exists
     * @return  None otherwise
     */
    public Option<CPlayer> getPlayer(String name) {
        var players = getPlayers()
                .filter(p -> p.rawPlayer.getName()
                        .equalsIgnoreCase(name))
                .toList();

        if (players.isEmpty()) return Option.none();
        return Option.some(players.get(0));
    }

    /**
     * @return  A stream of all CPlayers
     */
    public Stream<CPlayer> getPlayers() {
        return playerIds.values()
                .stream()
                .map(entId -> (CPlayer) EntityManager
                        .INSTANCE
                        .getEntity(entId)
                        .unwrap())
                .distinct();
    }


    public double[] syncPlayerInv(int entityId) {
        var e = EntityManager.INSTANCE
                .getEntity(entityId)
                .unwrap();

        return syncPlayerInv((CPlayer) e);
    }
    /**
     * Syncs the CPlayer EntityInventory component with the vanilla inventory
     *
     * @param   p   The CPlayer whose inventory should be synced
     * @return  An array from the sum of all stat boosts
     */
    public double[] syncPlayerInv(CPlayer p) {
        var swapStats = new double[7];
        var inv = p.rawPlayer.getInventory();
        var items = inv.getContents();
        var heldItemSlot = inv.getHeldItemSlot();
        for (var i = 0; i < items.length; i++) {
            var optItem = CItem.fromItemStack(items[i]);
            CItem item;
            if (optItem instanceof Some<CItem> some) {
                item = some.inner();
            } else {
                p.rawPlayer.sendMessage(ChatColor.RED + "An unrecognised item was removed from your inventory, please contact staff if you believe this was a mistake.");
                Bukkit.getLogger().info(items[i].getItemMeta().getDisplayName() + ", was removed from " + p.rawPlayer.getName() + "'s inventory.");
                continue;
            }

            //Bukkit.getLogger().info(i + ": " + item.unwrap().getName());
            //Bukkit.getLogger().info("held slot: " + heldItemSlot);

            if (p.inv.inventory[p.inv.activeSlot] != null && i == heldItemSlot) {
                swapStats[PlayerInvUpdate.DAMAGE] -= p.inv.inventory[p.inv.activeSlot].getStat("damage").value;
                swapStats[PlayerInvUpdate.STRENGTH] -= p.inv.inventory[p.inv.activeSlot].getStat("strength").value;
                swapStats[PlayerInvUpdate.CRITDAMAGE] -= p.inv.inventory[p.inv.activeSlot].getStat("crit-damage").value;
                swapStats[PlayerInvUpdate.CRITCHANCE] -= p.inv.inventory[p.inv.activeSlot].getStat("crit-chance").value;
                swapStats[PlayerInvUpdate.HEALTH] -= p.inv.inventory[p.inv.activeSlot].getStat("health").value;
                swapStats[PlayerInvUpdate.DEFENSE] -= p.inv.inventory[p.inv.activeSlot].getStat("defense").value;
                swapStats[PlayerInvUpdate.MANA] -= p.inv.inventory[p.inv.activeSlot].getStat("mana").value;
            }
            if (i <= 35) p.inv.inventory[i] = item;
            else {
                var index = i-36;
                if (p.inv.armor[index] != null) {
                    swapStats[PlayerInvUpdate.DAMAGE] -= p.inv.armor[index].getStat("damage").value;
                    swapStats[PlayerInvUpdate.STRENGTH] -= p.inv.armor[index].getStat("strength").value;
                    swapStats[PlayerInvUpdate.CRITDAMAGE] -= p.inv.armor[index].getStat("crit-damage").value;
                    swapStats[PlayerInvUpdate.CRITCHANCE] -= p.inv.armor[index].getStat("crit-chance").value;
                    swapStats[PlayerInvUpdate.HEALTH] -= p.inv.armor[index].getStat("health").value;
                    swapStats[PlayerInvUpdate.DEFENSE] -= p.inv.armor[index].getStat("defense").value;
                    swapStats[PlayerInvUpdate.MANA] -= p.inv.armor[index].getStat("mana").value;
                }
                p.inv.armor[index] = item;
            }
        }
        p.inv.activeSlot = heldItemSlot;
        return swapStats;
    }

    public void deloadPlayer(CPlayer p) {
        playerData.savePlayer(p);
        var eid = playerIds.remove(p.getUUID());
        EntityManager.INSTANCE
                .killEntity(eid);
    }

    public void deloadPlayer(Player p) {
        deloadPlayer(getPlayer(p.getUniqueId()).unwrap());
    }

    public void writeData(CrumbMMO plugin) {
        getPlayers()
                .toList()
                .forEach(this::deloadPlayer);

        var gson = new GsonBuilder().setPrettyPrinting().create();
        var jsonPlayerData = gson.toJson(playerData);

        try {
            var f = new FileWriter(new File(plugin.getDataFolder(), "PlayerData.json"));
            f.write(jsonPlayerData);
            f.close();
        } catch (IOException e) { e.printStackTrace(); }
    }


    public static CompletableFuture<Suggestions> suggest(CommandContext<CommandSourceStack> c, SuggestionsBuilder builder) {
        Bukkit.getOnlinePlayers()
            .stream()
            .map(Player::getName)
            .filter(name -> name.startsWith(getArg(c, "player-name", String.class, "")))
            .forEach(builder::suggest);

        return builder.buildFuture();
    }
}
































