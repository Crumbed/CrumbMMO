package com.crumbed.crumbmmo.managers;

import com.crumbed.crumbmmo.CrumbMMO;
import com.crumbed.crumbmmo.entity.CEntity;
import com.crumbed.crumbmmo.entity.CPlayer;
import com.crumbed.crumbmmo.entity.systems.PlayerInvUpdate;
import com.crumbed.crumbmmo.items.CItem;
import com.crumbed.crumbmmo.utils.JsonPlayerData;
import com.crumbed.crumbmmo.utils.Option;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerManager {
    public static Option<PlayerManager> INSTANCE = Option.none();
    /**
     * Map from MC UUID -> Entity ID
     */
    private HashMap<UUID, UUID> playerIds;
    private JsonPlayerData playerData;
    private HashMap<UUID, Double> playerHealthScales;

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
        if (INSTANCE.isNone()) INSTANCE = Option.some(new PlayerManager(plugin));
    }

    public HashMap<UUID, Double> getHealthScales() { return playerHealthScales; }

    public void addPlayer(final Player player) {
        CPlayer p;
        // if player is new
        if (!playerData.isPlayerRegistered(player.getUniqueId())) {
            p = CPlayer.newPlayer(player);
        } else {
            p = playerData.loadPlayer(player.getUniqueId());
        }

        // Calculate player health scale
        double healthScale = p.getStats()
                .health
                .calcHealthScale();
        PlayerManager manager = PlayerManager.INSTANCE.unwrap();
        manager.getHealthScales().put(p.id, healthScale);
        p.rawPlayer.setHealthScale(healthScale);

        manager.syncPlayerInv(p);

        // Add player to EntityManager & playerIds
        playerIds.put(p.getUUID(), p.id);
        EntityManager
                .INSTANCE
                .unwrap()
                .addEntity(p);
    }

    /**
     * @param   uuid    MC UUID of the Player
     * @return  Option.some if CPlayer exists
     * @return  Option.none otherwise
     */
    public Option<CPlayer> getPlayer(UUID uuid) {
        Option<CEntity> entity = EntityManager
                .INSTANCE
                .unwrap()
                .getEntity(playerIds.get(uuid));

        if (entity.isSome() && entity.unwrap() instanceof CPlayer) {
            return Option.some(
                    (CPlayer) entity.unwrap()
            );
        }

        return Option.none();
    }

    /**
     * @param   player  Player
     * @return  Option.some if CPlayer exists
     * @return  Option.none otherwise
     */
    public Option<CPlayer> getPlayer(Player player) {
        if (player == null) return Option.none();
        return getPlayer(player.getUniqueId());
    }

    /**
     * @param   name  name of the Player
     * @return  Option.some if CPlayer exists
     * @return  Option.none otherwise
     */
    public Option<CPlayer> getPlayer(String name) {
        List<CPlayer> players = getPlayers()
                .filter(p -> p.rawPlayer.getName()
                        .equalsIgnoreCase(name))
                .collect(Collectors.toList());

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
                        .unwrap()
                        .getEntity(entId)
                        .unwrap())
                .distinct();
    }

    /**
     * Syncs the CPlayer EntityInventory component with the vanilla inventory
     *
     * @param   p   The CPlayer whose inventory should be synced
     * @return  An array from the sum of all stat boosts
     */
    public double[] syncPlayerInv(CPlayer p) {
        double[] swapStats = new double[7];
        PlayerInventory inv = p.rawPlayer.getInventory();
        ItemStack[] items = inv.getContents();
        int heldItemSlot = inv.getHeldItemSlot();
        for (int i = 0; i < items.length; i++) {
            Option<CItem> item = CItem.fromItemStack(items[i]);
            if (item.isNone()) {
                p.rawPlayer.sendMessage(ChatColor.RED + "An unrecognised item was removed from your inventory, please contact staff if you believe this was a mistake.");
                Bukkit.getLogger().info(items[i].getItemMeta().getDisplayName() + ", was removed from " + p.rawPlayer.getName() + "'s inventory.");
                continue;
            }
            //Bukkit.getLogger().info(i + ": " + item.unwrap().getName());
            //Bukkit.getLogger().info("held slot: " + heldItemSlot);

            if (p.inv.inventory[p.inv.activeSlot] != null && i == heldItemSlot) {
                swapStats[PlayerInvUpdate.DAMAGE] -= p.inv.inventory[p.inv.activeSlot].getStat("damage").getValue();
                swapStats[PlayerInvUpdate.STRENGTH] -= p.inv.inventory[p.inv.activeSlot].getStat("strength").getValue();
                swapStats[PlayerInvUpdate.CRITDAMAGE] -= p.inv.inventory[p.inv.activeSlot].getStat("crit-damage").getValue();
                swapStats[PlayerInvUpdate.CRITCHANCE] -= p.inv.inventory[p.inv.activeSlot].getStat("crit-chance").getValue();
                swapStats[PlayerInvUpdate.HEALTH] -= p.inv.inventory[p.inv.activeSlot].getStat("health").getValue();
                swapStats[PlayerInvUpdate.DEFENSE] -= p.inv.inventory[p.inv.activeSlot].getStat("defense").getValue();
                swapStats[PlayerInvUpdate.MANA] -= p.inv.inventory[p.inv.activeSlot].getStat("mana").getValue();
            }
            if (i <= 35) p.inv.inventory[i] = item.unwrap();
            else {
                int index = i-36;
                if (p.inv.armor[index] != null) {
                    swapStats[PlayerInvUpdate.DAMAGE] -= p.inv.armor[index].getStat("damage").getValue();
                    swapStats[PlayerInvUpdate.STRENGTH] -= p.inv.armor[index].getStat("strength").getValue();
                    swapStats[PlayerInvUpdate.CRITDAMAGE] -= p.inv.armor[index].getStat("crit-damage").getValue();
                    swapStats[PlayerInvUpdate.CRITCHANCE] -= p.inv.armor[index].getStat("crit-chance").getValue();
                    swapStats[PlayerInvUpdate.HEALTH] -= p.inv.armor[index].getStat("health").getValue();
                    swapStats[PlayerInvUpdate.DEFENSE] -= p.inv.armor[index].getStat("defense").getValue();
                    swapStats[PlayerInvUpdate.MANA] -= p.inv.armor[index].getStat("mana").getValue();
                }
                p.inv.armor[index] = item.unwrap();
            }
        }
        p.inv.activeSlot = heldItemSlot;
        return swapStats;
    }

    public void deloadPlayer(CPlayer p) {
        playerData.savePlayer(p);
        UUID eid = playerIds.remove(p.getUUID());
        EntityManager
                .INSTANCE
                .unwrap()
                .removeEntity(eid);
    }

    public void deloadPlayer(Player p) {
        deloadPlayer(getPlayer(p.getUniqueId()).unwrap());
    }

    public void writeData(CrumbMMO plugin) {
        getPlayers()
                .collect(Collectors.toList())
                .forEach(this::deloadPlayer);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonPlayerData = gson.toJson(playerData);

        try {
            FileWriter f = new FileWriter(new File(plugin.getDataFolder(), "PlayerData.json"));
            f.write(jsonPlayerData);
            f.close();
        } catch (IOException e) { e.printStackTrace(); }
    }
}
































