package com.projectkorra.rpg.modules.worldevents.listeners;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Handle players switching worlds to remove <br>
 * player BossBar and generally from a {@link com.projectkorra.rpg.modules.worldevents.WorldEvent}
 */
public class HandleWorldEventDisplayListener implements Listener {
    private final ProjectKorraRPG plugin;

    public HandleWorldEventDisplayListener(final ProjectKorraRPG plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWorldSwitch(final PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World from = event.getFrom();
        World to = player.getWorld();

        List<WorldEvent> activeEvents = new ArrayList<>(WorldEvent.getActiveEvents());

        activeEvents.forEach(worldEvent -> {
            // Leaving an event world
            if (worldEvent.getWorld() == from && worldEvent.removeAffected(player.getUniqueId())) {
                worldEvent.notifyViewerRemoved(player);
            }

            // Entering an event world
            if (worldEvent.getWorld() == to && worldEvent.addAffected(player.getUniqueId())) {
                worldEvent.notifyViewerAdded(player);
            }
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        List<WorldEvent> activeEvents = new ArrayList<>(WorldEvent.getActiveEvents());

        activeEvents.forEach(worldEvent -> {
            if (player.getWorld() != worldEvent.getWorld()) return;
            worldEvent.notifyViewerAdded(player);
        });
    }

    public ProjectKorraRPG getPlugin() {
        return plugin;
    }
}
