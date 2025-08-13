package com.projectkorra.rpg.modules.worldevents.runtime;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.worldevents.manager.WorldEventManager;
import com.projectkorra.rpg.modules.worldevents.models.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.display.TickingDisplay;
import com.projectkorra.rpg.modules.worldevents.display.WorldEventDisplay;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class WorldEventRuntime {
    private final ProjectKorraRPG plugin;
    private final WorldEvent worldEvent;
    private final WorldEventManager manager;
    private final Set<UUID> viewers = new HashSet<>();

    private BukkitTask task;

    public WorldEventRuntime(ProjectKorraRPG plugin, WorldEvent worldEvent, WorldEventManager manager) {
        this.plugin = plugin;
        this.worldEvent = worldEvent;
        this.manager = manager;
    }

    public void start() {
        if (task != null) return;

        final long duration = worldEvent.getDuration();
        final long startTime = System.currentTimeMillis();
        final long period = computePeriod(worldEvent);

        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                double elapsed = System.currentTimeMillis() - startTime;
                double progress = 1.0 - (elapsed / (double) duration);

                if (progress <= 0.0) {
                    for (WorldEventDisplay display : worldEvent.getDisplayMethods()) {
                        if (display instanceof TickingDisplay tickingDisplay) tickingDisplay.updateTick(worldEvent, 0.0);
                    }

                    manager.stop(worldEvent);
                    return;
                }

                double clamped = Math.max(0.0, Math.min(1.0, progress)); // Guard because BossBar can only have values between 0 - 1
                for (WorldEventDisplay display : worldEvent.getDisplayMethods()) {
                    if (display instanceof TickingDisplay tickingDisplay) tickingDisplay.updateTick(worldEvent, clamped);
                }
            }
        }.runTaskTimer(plugin, 0L, period);
    }

    public void cancel() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public boolean isRunning() {
        return task != null;
    }

    public boolean addViewer(UUID uuid) {
        return viewers.add(uuid);
    }

    public boolean removeViewer(UUID uuid) {
        return viewers.remove(uuid);
    }

    private static long computePeriod(WorldEvent worldEvent) {
        return worldEvent.getDisplayMethods().stream()
                .filter(display -> display instanceof TickingDisplay)
                .map(display -> (TickingDisplay) display)
                .mapToLong(TickingDisplay::tickPeriod) // Is smooth?
                .map(period -> Math.max(period, 1L))
                .min()
                .orElse(20L);
    }

    public ProjectKorraRPG getPlugin() {
        return plugin;
    }

    public WorldEvent getWorldEvent() {
        return worldEvent;
    }

    public Set<UUID> getViewers() {
        return Collections.unmodifiableSet(viewers);
    }
}
