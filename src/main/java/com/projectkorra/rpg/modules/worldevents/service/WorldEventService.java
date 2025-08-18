package com.projectkorra.rpg.modules.worldevents.service;

import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.util.PassiveManager;
import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.worldevents.event.WorldEventStartEvent;
import com.projectkorra.rpg.modules.worldevents.event.WorldEventStopEvent;
import com.projectkorra.rpg.modules.worldevents.models.ActiveWorldEvent;
import com.projectkorra.rpg.modules.worldevents.models.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.storage.ActiveWorldEventIndex;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class WorldEventService {
    private final ProjectKorraRPG plugin;
    private final ActiveWorldEventIndex activeEventsIndex;

    private final Map<WorldEvent, BukkitTask> nonTickingStops = new HashMap<>();
    private BukkitTask ticker;
    private long tickNo;

    public WorldEventService(ProjectKorraRPG plugin, ActiveWorldEventIndex activeEventsIndex) {
        this.plugin = plugin;
        this.activeEventsIndex = activeEventsIndex;
    }

    public boolean start(WorldEvent worldEvent, World runtimeWorld) {
        if (worldEvent == null || runtimeWorld == null) return false;
        if (activeEventsIndex.contains(worldEvent)) {
            plugin.getLogger().warning("WorldEvent already running: " + worldEvent.getKey());
            return false;
        }
        if (worldEvent.isWorldDisabled(runtimeWorld)) {
            plugin.getLogger().info("Cannot start WorldEvent " + worldEvent.getKey() + " world is null or disabled");
            return false;
        }

        ActiveWorldEvent active = new ActiveWorldEvent(worldEvent, runtimeWorld);

        recalcAllAbilities();

        activeEventsIndex.add(worldEvent, active);
        active.start();
        ensureTicker();

        if (active.requiresTicking()) {
            ensureTicker(); // TaskTimer because of BossBar
        } else {
            scheduleNonTickingStop(worldEvent); // Scheduler to prevent useless ticking without given BossBar
        }

        Bukkit.getPluginManager().callEvent(new WorldEventStartEvent(worldEvent));
        return true;
    }

    /**
     * Temp method, will lead to issues with coming Scheduler
     */
    public boolean start(WorldEvent worldEvent) {
        World runtimeWorld = null;

        if (worldEvent != null && !worldEvent.getScheduledWorlds().isEmpty()) {
            runtimeWorld = worldEvent.getScheduledWorlds().getFirst();
        }

        if (runtimeWorld == null && !plugin.getServer().getWorlds().isEmpty()) {
            runtimeWorld = plugin.getServer().getWorlds().getFirst();
        }

        return start(worldEvent, runtimeWorld);
    }

    public boolean stop(WorldEvent worldEvent) {
        BukkitTask delayed = nonTickingStops.remove(worldEvent);
        if (delayed != null) delayed.cancel();

        ActiveWorldEvent active = activeEventsIndex.remove(worldEvent);
        if (active == null) {
            plugin.getLogger().warning("Tried to stop non running WorldEvent: " + worldEvent.getKey());
            return false;
        }

        active.stop();
        recalcAllPassives();
        tryStopTicker();

        Bukkit.getPluginManager().callEvent(new WorldEventStopEvent(worldEvent));
        return true;
    }

    public void stopAll() {
        for (BukkitTask task : nonTickingStops.values()) task.cancel();

        nonTickingStops.clear();

        for (WorldEvent worldEvent : new ArrayList<>(activeEventsIndex.activeWorldEvents())) {
            stop(worldEvent);
        }
    }

    public void shutdown() {
        stopAll();
        if (ticker != null) {
            ticker.cancel();
            ticker = null;
        }
    }

    private void recalcAllAbilities() {
        for (CoreAbility ability : CoreAbility.getAbilitiesByInstances()) {
            ability.recalculateAttributes();
        }
    }

    private void recalcAllPassives() {
        PassiveManager.getPassives().values().forEach(passive -> {
            for (CoreAbility ability : CoreAbility.getAbilities(passive.getClass())) {
                ability.recalculateAttributes();
            }
        });
    }

    public void addViewer(WorldEvent worldEvent, Player viewer) {
        ActiveWorldEvent active = activeEventsIndex.getActive(worldEvent);
        if (active != null) active.addViewer(viewer);
    }

    public void removeViewer(WorldEvent worldEvent, Player viewer) {
        ActiveWorldEvent active = activeEventsIndex.getActive(worldEvent);
        if (active != null) active.removeViewer(viewer);
    }

    private void scheduleNonTickingStop(WorldEvent worldEvent) {
        long delayTicks = Math.max(1L, worldEvent.getDuration() / 50L);
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                stop(worldEvent);
            }
        }.runTaskLater(plugin, delayTicks);

        nonTickingStops.put(worldEvent, task);
    }

    private void ensureTicker() {
        if (ticker != null) return;
        if (!activeEventsIndex.hasTicking()) return;

        tickNo = 0;
        ticker = new BukkitRunnable() {
            @Override
            public void run() {
                tickNo++;
                long now = System.currentTimeMillis();

                // Use snapshot to iterate to defend against mutation in some cases
                ActiveWorldEvent[] snapshot = activeEventsIndex.snapshot();
                if (snapshot.length == 0) {
                    tryStopTicker();
                    return;
                }

                List<WorldEvent> expired = null;
                for (ActiveWorldEvent active : snapshot) {
                    if (active.tick(tickNo, now)) {
                        if (expired == null) expired = new ArrayList<>();
                        expired.add(active.getWorldEvent());
                    }
                }
                if (expired != null) {
                    for (WorldEvent worldEvent : expired) {
                        stop(worldEvent);
                    }
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }

    private void tryStopTicker() {
        if (activeEventsIndex.isEmpty() && ticker != null) {
            ticker.cancel();
            ticker = null;
        }
    }

    public ActiveWorldEventIndex getActiveEventsIndex() {
        return activeEventsIndex;
    }
}
