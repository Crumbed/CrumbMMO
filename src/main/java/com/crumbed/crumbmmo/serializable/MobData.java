package com.crumbed.crumbmmo.serializable;

import com.crumbed.crumbmmo.CrumbMMO;
import com.crumbed.crumbmmo.ecs.CEntity;
import com.crumbed.crumbmmo.ecs.components.EntityName;
import com.crumbed.crumbmmo.ecs.components.NameTag;
import com.crumbed.crumbmmo.ecs.components.RawEntity;
import com.crumbed.crumbmmo.managers.EntityManager;
import com.crumbed.crumbmmo.stats.*;
import com.crumbed.crumbmmo.ecs.components.EntityStats;
import com.crumbed.crumbmmo.utils.None;
import com.crumbed.crumbmmo.utils.Option;
import com.crumbed.crumbmmo.utils.Some;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.crumbed.crumbmmo.utils.Namespaces.*;

public class MobData {
    @SerializedName("mob-types")
    public final String[] mobTypes;
    @SerializedName("vanilla-mobs")
    public final HashMap<EntityType, VanillaMob> vanillaMobs;


    public MobData() {
        mobTypes = new String[0];
        vanillaMobs = new HashMap<>();
    }


    public static MobData loadMobData(CrumbMMO plugin) {
        var f = new File(plugin.getDataFolder(), "MobData.json");

        if (!f.exists()) try {
            f.createNewFile();
            return new MobData();
        } catch (IOException ignored){}
        else try (Stream<String> lines = Files.lines(f.toPath())) {
            var jsonMobData = String.join("\n", lines
                    .collect(Collectors.toList()));
            var gson = new Gson();
            return gson.fromJson(jsonMobData, MobData.class);
        } catch (IOException ignored){}
        return null;
    }

    public void saveMobData(CrumbMMO plugin) {
        var f = new File(plugin.getDataFolder(), "MobData.json");
        try {
            var writer = new FileWriter(f);
            var gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(this));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CEntity generateMob(LivingEntity entity, int level, Option<String> name) {
        var vanillaMob = vanillaMobs.get(entity.getType());
        if (vanillaMob == null) {
            var healthAttribute = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            var damageAttribute = entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);

            vanillaMob = new VanillaMob(
                    (healthAttribute != null) ? (int) (healthAttribute.getBaseValue() * 5) : 0,
                    (damageAttribute != null) ? (int) (damageAttribute.getBaseValue() * 5) : 0
            );
            vanillaMobs.put(entity.getType(), vanillaMob);
        }

        var entitiyData = entity.getPersistentDataContainer();
        var levelMult = (level - vanillaMob.baseLevel) * 0.02;
        var stats = new EntityStats(
                new Damage(vanillaMob.stats.get(GenericStat.Damage) + levelMult),
                new Strength(0D),
                new CritDamage(0D),
                new CritChance(0D),
                new Health(vanillaMob.stats.get(GenericStat.Health) + levelMult),
                new Defense(0D),
                new Mana(0D)
        );
        var entName = switch (name) {
            case Some<String> s -> s.inner();
            case None<String> ignored -> entity.getName();
        };
        //var tag = entity.getWorld().spawn(entity.getEyeLocation(), TextDisplay.class);
        //tag.setText(String.format(
        //        "%s[Lv%d] %s",
        //        ChatColor.GRAY,
        //        level, entName
        //));
        //tag.setAlignment(TextDisplay.TextAlignment.CENTER);

        var newEnt = new CEntity.Builder()
                .with(stats)
                .with(new EntityName(entName))
                .with(new RawEntity(entity.getUniqueId()))
                //.with(new NameTag(tag))
                .create(EntityManager.INSTANCE);

        entitiyData.set(MOB_LEVEL, PersistentDataType.INTEGER, level);
        entitiyData.set(MOB_NAME, PersistentDataType.STRING, entity.getName());
        entitiyData.set(MOB_ID, PersistentDataType.INTEGER, newEnt.id);
        entitiyData.set(SESSION, PersistentDataType.STRING, CrumbMMO.getSessionId().toString());

        return newEnt;
    }

    public int getBaseLevel(EntityType type) {
        var mob = vanillaMobs.get(type);
        return (mob == null) ? 1 : mob.baseLevel;
    }


    public static class VanillaMob {
        @SerializedName("base-level")
        public final int baseLevel;
        public final HashMap<GenericStat, Double> stats;

        public VanillaMob() {
            baseLevel = 1;
            stats = new HashMap<>();
        }

        public VanillaMob(double maxHealth, double damage) {
            baseLevel = 1;
            stats = new HashMap<>();
            stats.put(GenericStat.Health, maxHealth);
            stats.put(GenericStat.Damage, damage);
        }
    }



}








