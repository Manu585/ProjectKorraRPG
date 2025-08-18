package com.projectkorra.rpg.modules.worldevents.models;

import com.projectkorra.rpg.modules.worldevents.display.TickingDisplay;
import com.projectkorra.rpg.modules.worldevents.display.ViewerDisplay;
import com.projectkorra.rpg.modules.worldevents.display.WorldEventDisplay;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public class ActiveWorldEvent {
    private final Set<UUID> viewers = new HashSet<>();
    private final List<TickingDisplay> tickingDisplays = new ArrayList<>();

    private final WorldEvent worldEvent;
    private final World runtimeWorld;
    private final long updateEveryTicks;
    private final boolean requiresTicking;

    private long startTime;
    private long nextUpdateAtTickNo = 0L;

    public ActiveWorldEvent(final WorldEvent worldEvent, final World runtimeWorld) {
        this.worldEvent = worldEvent;
        this.runtimeWorld = runtimeWorld;

        for (WorldEventDisplay display : worldEvent.getDisplayMethods()) {
            if (display instanceof TickingDisplay ticking) {
                tickingDisplays.add(ticking);
            }
        }

        this.requiresTicking = !tickingDisplays.isEmpty();
        this.updateEveryTicks = computePeriod(tickingDisplays);
    }

    public void start() {
        this.startTime = System.currentTimeMillis();

        // Start Display: Create BossBar, send chat, etc.
        for (WorldEventDisplay display : worldEvent.getDisplayMethods()) {
            display.startDisplay(worldEvent, runtimeWorld);
        }

        // Actually put viewers in viewers map (Display BossBar)
        for (Player viewer : runtimeWorld.getPlayers()) {
            addViewer(viewer);
        }
    }

    public void stop() {
        for (WorldEventDisplay display : worldEvent.getDisplayMethods()) {
            display.stopDisplay(worldEvent, runtimeWorld);
        }
        viewers.clear();
    }

    /**
     * @return true if expired and should be stopped by Manager
     */
    public boolean tick(long tickNo, long nowMillis) {
        // Ticking not required?
        if (!requiresTicking) {
            return (nowMillis - startTime) >= worldEvent.getDuration();
        }

        // Throttle updates to configured option (smooth, nonsmooth)
        if (tickNo < nextUpdateAtTickNo) return false;
        nextUpdateAtTickNo = tickNo + updateEveryTicks;

        double elapsed = nowMillis - startTime;
        double progress = 1.0 - (elapsed / (double) worldEvent.getDuration());

        if (progress <= 0.0) {
            for (TickingDisplay display : tickingDisplays) {
                display.updateTick(worldEvent, 0.0);
            }
            return true; // Expired
        }

        double clamped = Math.min(progress, 1.0);
        for (TickingDisplay display : tickingDisplays) {
            display.updateTick(worldEvent, clamped);
        }

        return false;
    }

    public void addViewer(Player viewer) {
        if (viewer == null || !viewer.isOnline()) return;
        if (viewers.add(viewer.getUniqueId())) {
            for (WorldEventDisplay display : worldEvent.getDisplayMethods()) {
                if (display instanceof ViewerDisplay viewerDisplay) {
                    viewerDisplay.addViewer(viewer);
                }
            }
        }
    }

    public void removeViewer(Player viewer) {
        if (viewer == null || !viewer.isOnline()) return;
        if (viewers.remove(viewer.getUniqueId())) {
            for (WorldEventDisplay display : worldEvent.getDisplayMethods()) {
                if (display instanceof ViewerDisplay viewerDisplay) {
                    viewerDisplay.removeViewer(viewer);
                }
            }
        }
    }

    private static long computePeriod(List<TickingDisplay> list) {
        if (list.isEmpty()) return 20L;
        long min = Long.MAX_VALUE;
        for (TickingDisplay td : list) {
            long p = td.tickPeriod();
            if (p < 1L) p = 1L;
            if (p < min) min = p;
        }
        return (min == Long.MAX_VALUE) ? 20L : min;
    }

    public boolean requiresTicking() {
        return requiresTicking;
    }

    public WorldEvent getWorldEvent() {
        return worldEvent;
    }

    public World getRuntimeWorld() {
        return runtimeWorld;
    }

    public int getViewerCount() {
        return viewers.size();
    }

    @Override
    public String toString() {
        return "ActiveWorldEvent{key=" + worldEvent.getKey() +
                ", world=" + (runtimeWorld != null ? runtimeWorld.getName() : "null") +
                ", viewers=" + viewers.size() +
                ", updateEveryTicks=" + updateEveryTicks + "}";
    }
}
