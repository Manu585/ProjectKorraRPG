package com.projectkorra.rpg.modules.worldevents.models;

import com.projectkorra.rpg.modules.worldevents.display.IBossBarDisplay;
import com.projectkorra.rpg.modules.worldevents.display.IChatDisplay;
import com.projectkorra.rpg.modules.worldevents.display.ISoundDisplay;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public class ActiveWorldEvent {
    private final WorldEvent worldEvent;
    private final Set<UUID> viewers = new HashSet<>();

    private final IChatDisplay chatDisplay;
    private final IBossBarDisplay bossBarDisplay;
    private final ISoundDisplay soundDisplay;

    private final World runtimeWorld;
    private final long updateEveryTicks;
    private final boolean requiresTicking;

    private long startTime;
    private long nextUpdateAtTickNo = 0L;

    public ActiveWorldEvent(final WorldEvent worldEvent, final World runtimeWorld) {
        this.worldEvent = worldEvent;
        this.chatDisplay = worldEvent.getChatDisplay();
        this.bossBarDisplay = worldEvent.getBossBarDisplay();
        this.soundDisplay = worldEvent.getSoundDisplay();
        this.runtimeWorld = runtimeWorld;

        this.requiresTicking = bossBarDisplay != null;
        this.updateEveryTicks = computePeriod(bossBarDisplay);
    }

    public void start() {
        this.startTime = System.currentTimeMillis();
        this.startDisplays();
    }

    private void startDisplays() {
        if (chatDisplay != null) {
            chatDisplay.sendStartMessage(runtimeWorld.getPlayers());
        }

        if (bossBarDisplay != null) {
            bossBarDisplay.start(worldEvent);
            bossBarDisplay.addViewers(runtimeWorld.getPlayers());
        }

        if (soundDisplay != null) {
            soundDisplay.playStartSound(runtimeWorld.getPlayers());
        }
    }

    public void stop() {
        this.stopDisplays();
        viewers.clear();
    }

    private void stopDisplays() {
        if (chatDisplay != null) {
            chatDisplay.sendStopMessage(runtimeWorld.getPlayers());
        }

        if (bossBarDisplay != null) {
            bossBarDisplay.stop(worldEvent);
            bossBarDisplay.removeViewers(runtimeWorld.getPlayers());
        }

        if (soundDisplay != null) {
            soundDisplay.playStopSound(runtimeWorld.getPlayers());
        }
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
            if (bossBarDisplay != null) {
                bossBarDisplay.updateTick(worldEvent, 0.0);
            }
            return true; // Expired
        }

        double clamped = Math.min(progress, 1.0);
        if (bossBarDisplay != null) {
            bossBarDisplay.updateTick(worldEvent, clamped);
        }

        return false;
    }

    public void addViewer(Player viewer) {
        if (viewer == null || !viewer.isOnline()) return;
        if (viewers.add(viewer.getUniqueId())) {
            if (bossBarDisplay != null) {
                bossBarDisplay.addViewer(viewer);
            }
        }
    }

    public void removeViewer(Player viewer) {
        if (viewer == null || !viewer.isOnline()) return;
        if (viewers.remove(viewer.getUniqueId())) {
            if (bossBarDisplay != null) {
                bossBarDisplay.removeViewer(viewer);
            }
        }
    }

    private static long computePeriod(IBossBarDisplay display) {
        if (display == null) return 20L;

        long min = Long.MAX_VALUE;
        long p = display.tickPeriod();
        if (p < 1L) p = 1L;
        if (p < min) min = p;

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
