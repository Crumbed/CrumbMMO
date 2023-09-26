package com.crumbed.crumbmmo.serializable;

import com.crumbed.crumbmmo.CrumbMMO;
import com.crumbed.crumbmmo.stats.GenericStat;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MobData {
    @SerializedName("mob-types")
    public final String[] mobTypes;
    @SerializedName("vanilla-mobs")
    public final HashMap<EntityType, VanillaMob> vanillaMobs;
    @SerializedName("mob-variants")
    public final HashMap<String, MobVariant> mobVariants;


    public MobData() {
        mobTypes = new String[0];
        vanillaMobs = new HashMap<>();
        mobVariants = new HashMap<>();
    }


    public static MobData loadMobData(CrumbMMO plugin) {
        File f = new File(plugin.getDataFolder(), "MobData.json");

        if (!f.exists()) try {
            f.createNewFile();
            return new MobData();
        } catch (IOException ignored){}
        else try (Stream<String> lines = Files.lines(f.toPath())) {
            String jsonMobData = String.join("\n", lines
                    .collect(Collectors.toList()));
            Gson gson = new Gson();
            return gson.fromJson(jsonMobData, MobData.class);
        } catch (IOException ignored){}
        return null;

    }

    public void buffVanillaMob(LivingEntity e) {

    }







    public static class MobVariant {
        public final String name;
        public final HashMap<GenericStat, Double> stats;

        public MobVariant() {
            name = "";
            stats = new HashMap<>();
        }
    }

    public static class VanillaMob {
        @SerializedName("base-level")
        public final int baseLevel;
        public final HashMap<Integer, MobVariant> variants;

        public VanillaMob() {
            baseLevel = 0;
            variants = new HashMap<>();
        }
    }



}








