package com.projectkorra.rpg.modules.worldevents.manager;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.worldevents.event.WorldEventStartEvent;
import com.projectkorra.rpg.modules.worldevents.event.WorldEventStopEvent;
import com.projectkorra.rpg.modules.worldevents.models.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.runtime.WorldEventRuntime;
import com.projectkorra.rpg.modules.worldevents.display.ViewerDisplay;
import com.projectkorra.rpg.modules.worldevents.display.WorldEventDisplay;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public class WorldEventManager {
    private final Map<NamespacedKey, WorldEvent> loadedWorldEvents = new HashMap<>();
    private final Map<WorldEvent, WorldEventRuntime> activeEvents = new HashMap<>();
    private final Map<UUID, Set<WorldEvent>> activeEventsByWorld = new HashMap<>();
    private final Map<String, WorldEvent> loadedByPath = new HashMap<>();

    private final ProjectKorraRPG plugin;

    public WorldEventManager(ProjectKorraRPG plugin) {
        this.plugin = plugin;
    }

    public void register(WorldEvent worldEvent) {
        if (worldEvent == null) return;

        WorldEvent previous = loadedWorldEvents.put(worldEvent.getKey(), worldEvent);
        loadedByPath.put(worldEvent.getKey().getKey().toLowerCase(Locale.ROOT), worldEvent);

        if (previous != null) {
            plugin.getLogger().warning("WorldEvent '" + worldEvent.getKey() + "' replaced an existing registration.");

        }
    }

    public void registerAll(Collection<WorldEvent> worldEvents) {
        if (worldEvents == null) return;
        for (WorldEvent worldEvent : worldEvents) register(worldEvent);
    }

    public void clearAllRegistered() {
        loadedWorldEvents.clear();
        loadedByPath.clear();
    }

    // START / STOP HANDLING
    public boolean start(WorldEvent worldEvent) {
        if (activeEvents.containsKey(worldEvent)) {
            plugin.getLogger().warning("Attempted to start already running WorldEvent! Key: " + worldEvent.getKey());
            return false;
        }

        if (worldEvent.getWorld() == null || worldEvent.getDisabledWorlds().contains(worldEvent.getWorld())) {
            plugin.getLogger().info("Couldn't start WorldEvent because world is null / disabled");
            return false;
        }

        WorldEventRuntime runtime = new WorldEventRuntime(plugin, worldEvent, this);

        indexActiveEvent(worldEvent, runtime);

        // Startup displays (create BossBar, send start message)
        for (WorldEventDisplay display : worldEvent.getDisplayMethods()) {
            display.startDisplay(worldEvent);
        }

        for (Player player : worldEvent.getWorld().getPlayers()) {
            addViewer(worldEvent, player);
        }

        Bukkit.getPluginManager().callEvent(new WorldEventStartEvent(worldEvent));
        runtime.start();
        return true;
    }

    public boolean stop(WorldEvent worldEvent) {
        WorldEventRuntime runtime = activeEvents.remove(worldEvent);
        if (runtime == null) {
            plugin.getLogger().warning("Attempted to stop a not running WorldEvent! Key: " + worldEvent.getKey());
            return false;
        }

        Bukkit.getPluginManager().callEvent(new WorldEventStopEvent(worldEvent));

        runtime.cancel();

        // Stop displays (unregister BossBar, send stop message)
        for (WorldEventDisplay display : worldEvent.getDisplayMethods()) {
            display.stopDisplay(worldEvent);
        }

        deindexActiveEvent(worldEvent);
        return true;
    }

    public void stopAll() {
        for (WorldEvent worldEvent : new ArrayList<>(activeEvents.keySet())) stop(worldEvent);
    }

    public void addViewer(WorldEvent worldEvent, Player viewer) {
        WorldEventRuntime runtime = activeEvents.get(worldEvent);
        if (runtime == null) return;
        if (runtime.addViewer(viewer.getUniqueId())) {
            for (WorldEventDisplay display : worldEvent.getDisplayMethods()) {
                if (display instanceof ViewerDisplay viewerDisplay) viewerDisplay.addViewer(viewer);
            }
        }
    }

    public void removeViewer(WorldEvent worldEvent, Player viewer) {
        WorldEventRuntime runtime = activeEvents.get(worldEvent);
        if (runtime == null) return;
        if (runtime.removeViewer(viewer.getUniqueId())) {
            for (WorldEventDisplay display : worldEvent.getDisplayMethods()) {
                if (display instanceof ViewerDisplay viewerDisplay) viewerDisplay.removeViewer(viewer);
            }
        }
    }

    private void indexActiveEvent(WorldEvent worldEvent, WorldEventRuntime runtime) {
        activeEvents.put(worldEvent, runtime);

        UUID worldId = worldEvent.getWorld().getUID();
        Set<WorldEvent> set = activeEventsByWorld.computeIfAbsent(worldId, __ -> new HashSet<>());
        set.add(worldEvent);
    }

    private void deindexActiveEvent(WorldEvent worldEvent) {
        activeEvents.remove(worldEvent);

        UUID worldId = worldEvent.getWorld().getUID();
        Set<WorldEvent> set = activeEventsByWorld.get(worldId);
        if (set != null) {
            set.remove(worldEvent);
            if (set.isEmpty()) {
                activeEventsByWorld.remove(worldId);
            }
        }
    }

    public Optional<WorldEvent> findEvent(String idOrKey) {
        if (idOrKey == null || idOrKey.isBlank()) return Optional.empty();

        String normalized = idOrKey.toLowerCase(Locale.ROOT);

        WorldEvent byPath = loadedByPath.get(normalized);
        if (byPath != null) return Optional.of(byPath);

        NamespacedKey parsed = NamespacedKey.fromString(normalized);
        if (parsed != null) {
            WorldEvent byFullKey = loadedWorldEvents.get(parsed);
            if (byFullKey != null) return Optional.of(byFullKey);
        }

        NamespacedKey defaulted = new NamespacedKey(plugin, normalized);
        return Optional.ofNullable(loadedWorldEvents.get(defaulted));
    }

    public Map<NamespacedKey, WorldEvent> getLoadedWorldEvents() {
        return Collections.unmodifiableMap(loadedWorldEvents);
    }

    public Set<WorldEvent> getActiveEvents() {
        return Collections.unmodifiableSet(activeEvents.keySet());
    }

    public Set<WorldEvent> getActiveEventsInWorld(World world) {
        Set<WorldEvent> set = activeEventsByWorld.get(world.getUID());
        return (set == null) ? Collections.emptySet() : Collections.unmodifiableSet(set);
    }
}
