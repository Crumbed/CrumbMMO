package com.crumbed.crumbmmo.managers;

import com.crumbed.crumbmmo.utils.Timeable;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TimerManager {
    public static TimerManager INSTANCE = null;

    private ArrayList<Timeable> timeables;

    private TimerManager(Plugin p) {
        timeables = new ArrayList<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (var i = 0; i < timeables.size(); i++) {
                    var t = timeables.get(i);
                    if (t.getRemainingTicks() <= 0) {
                        t.afterTimer();
                        timeables.set(i, null);
                        continue;
                    }

                    t.subtractTicks(10);
                }
                timeables = timeables
                        .stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toCollection(ArrayList::new));
            }
        }.runTaskTimer(p, 0, 10);
    }

    public static TimerManager init(Plugin p) {
        return switch (INSTANCE) {
            case null -> new TimerManager(p);
            case TimerManager ignored -> null;
        };
    }


    public void addTimeable(Timeable t) { timeables.add(t); }
    public void removeTimeable(Timeable t) { timeables.remove(t); }
    public static TimerManager getInstance() { return INSTANCE; }
}




















