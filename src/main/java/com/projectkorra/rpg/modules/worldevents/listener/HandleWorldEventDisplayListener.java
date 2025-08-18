package com.projectkorra.rpg.modules.worldevents.listener;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.worldevents.event.WorldEventStartEvent;
import com.projectkorra.rpg.modules.worldevents.event.WorldEventStopEvent;
import com.projectkorra.rpg.modules.worldevents.models.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.service.WorldEventService;
import com.projectkorra.rpg.modules.worldevents.storage.ActiveWorldEventIndex;
import com.projectkorra.rpg.modules.worldevents.storage.WorldEventRegistry;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * Handle players switching worlds to remove <br>
 * player BossBar and generally from a {@link WorldEvent}
 */
public class HandleWorldEventDisplayListener implements Listener {
    private final ProjectKorraRPG plugin;
    private final WorldEventService service;
    private final ActiveWorldEventIndex index;
    private final WorldEventRegistry registry;

    public HandleWorldEventDisplayListener(final ProjectKorraRPG plugin, WorldEventService service) {
        this.plugin = plugin;
        this.service = service;
        this.index = service.getActiveEventsIndex();
        this.registry = plugin.getModuleManager().getWorldEventsModule().getWorldEventRegistry();
    }

    @EventHandler
    public void onWorldSwitch(final PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();

        for (WorldEvent worldEvent : index.getActiveIn(event.getFrom())) {
            service.removeViewer(worldEvent, player);
        }

        for (WorldEvent worldEvent : index.getActiveIn(player.getWorld())) {
            service.addViewer(worldEvent, player);
        }
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        for (WorldEvent worldEvent : index.getActiveIn(event.getPlayer().getWorld())) {
            service.addViewer(worldEvent, event.getPlayer());
        }
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        for (WorldEvent worldEvent : index.getActiveIn(event.getPlayer().getWorld())) {
            service.removeViewer(worldEvent, event.getPlayer());
        }
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        for (WorldEvent worldEvent : new ArrayList<>(index.getActiveIn(event.getWorld()))) {
            service.stop(worldEvent);
        }
    }

    @EventHandler
    public void onWorldEventStart(WorldEventStartEvent event) {
        // ChatGPT Debugging
        plugin.getLogger().info("------------------------------");
        plugin.getLogger().info("STARTED WORLD EVENT " + event.getWorldEvent().getKey());
        plugin.getLogger().info(event.getWorldEvent().toString());

        plugin.getLogger().info("---------------ACTIVE INDEX---------------");
        // List active spec keys
        plugin.getLogger().info("ACTIVE EVENTS: " +
                service.getActiveEventsIndex().activeWorldEvents().stream()
                        .map(we -> we.getKey().toString())
                        .sorted(String.CASE_INSENSITIVE_ORDER)
                        .toList()
        );

        // Snapshot (uses ActiveWorldEvent#toString now)
        plugin.getLogger().info("ACTIVE SNAPSHOT: " + Arrays.toString(service.getActiveEventsIndex().snapshot()));

        // Per-world dump with world names and event keys
        plugin.getLogger().info("BY WORLD:");
        service.getActiveEventsIndex().debugActiveByWorld().forEach((uuid, set) -> {
            String worldName = org.bukkit.Bukkit.getWorld(uuid) != null
                    ? Objects.requireNonNull(Bukkit.getWorld(uuid)).getName()
                    : uuid.toString();
            String events = set.stream()
                    .map(we -> we.getKey().toString())
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("(none)");
            plugin.getLogger().info("  - " + worldName + ": " + events);
        });
        plugin.getLogger().info("------------------------------------------");

        plugin.getLogger().info("---------------REGISTRY---------------");
        plugin.getLogger().info("ALL: " +
                registry.getAll().keySet().stream()
                        .map(NamespacedKey::toString)
                        .sorted(String.CASE_INSENSITIVE_ORDER)
                        .toList()
        );
        plugin.getLogger().info("--------------------------------------");
        plugin.getLogger().info("------------------------------");
    }

    @EventHandler
    public void onWorldEventStop(WorldEventStopEvent event) {
        // ChatGPT Debugging
        plugin.getLogger().info("------------------------------");
        plugin.getLogger().info("STOPPED WORLD EVENT " + event.getWorldEvent().getKey());
        plugin.getLogger().info(event.getWorldEvent().toString());

        plugin.getLogger().info("---------------ACTIVE INDEX---------------");
        plugin.getLogger().info("ACTIVE EVENTS: " +
                service.getActiveEventsIndex().activeWorldEvents().stream()
                        .map(we -> we.getKey().toString())
                        .sorted(String.CASE_INSENSITIVE_ORDER)
                        .toList()
        );
        plugin.getLogger().info("ACTIVE SNAPSHOT: " + Arrays.toString(service.getActiveEventsIndex().snapshot()));
        plugin.getLogger().info("BY WORLD:");
        service.getActiveEventsIndex().debugActiveByWorld().forEach((uuid, set) -> {
            String worldName = org.bukkit.Bukkit.getWorld(uuid) != null
                    ? Objects.requireNonNull(Bukkit.getWorld(uuid)).getName()
                    : uuid.toString();
            String events = set.stream()
                    .map(we -> we.getKey().toString())
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("(none)");
            plugin.getLogger().info("  - " + worldName + ": " + events);
        });
        plugin.getLogger().info("------------------------------------------");

        plugin.getLogger().info("---------------REGISTRY---------------");
        plugin.getLogger().info("ALL: " +
                registry.getAll().keySet().stream()
                        .map(NamespacedKey::toString)
                        .sorted(String.CASE_INSENSITIVE_ORDER)
                        .toList()
        );
        plugin.getLogger().info("--------------------------------------");
        plugin.getLogger().info("------------------------------");
    }

    public WorldEventService getService() {
        return service;
    }
}
