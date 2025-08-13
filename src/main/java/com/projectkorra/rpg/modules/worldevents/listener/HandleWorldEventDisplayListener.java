package com.projectkorra.rpg.modules.worldevents.listener;

import com.projectkorra.rpg.modules.worldevents.manager.WorldEventManager;
import com.projectkorra.rpg.modules.worldevents.models.WorldEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import java.util.ArrayList;

/**
 * Handle players switching worlds to remove <br>
 * player BossBar and generally from a {@link WorldEvent}
 */
public class HandleWorldEventDisplayListener implements Listener {
    private final WorldEventManager manager;

    public HandleWorldEventDisplayListener(final WorldEventManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onWorldSwitch(final PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();

        for (WorldEvent worldEvent : manager.getActiveEventsInWorld(event.getFrom())) {
            manager.removeViewer(worldEvent, player);
        }

        for (WorldEvent worldEvent : manager.getActiveEventsInWorld(player.getWorld())) {
            manager.addViewer(worldEvent, player);
        }
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        for (WorldEvent worldEvent : manager.getActiveEventsInWorld(event.getPlayer().getWorld())) {
            manager.addViewer(worldEvent, event.getPlayer());
        }
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        for (WorldEvent worldEvent : manager.getActiveEventsInWorld(event.getPlayer().getWorld())) {
            manager.removeViewer(worldEvent, event.getPlayer());
        }
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        for (WorldEvent worldEvent : new ArrayList<>(manager.getActiveEventsInWorld(event.getWorld()))) {
            manager.stop(worldEvent);
        }
    }

    public WorldEventManager getManager() {
        return manager;
    }
}
