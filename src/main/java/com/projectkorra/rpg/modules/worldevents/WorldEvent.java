package com.projectkorra.rpg.modules.worldevents;

import com.projectkorra.rpg.modules.worldevents.event.WorldEventStartEvent;
import com.projectkorra.rpg.modules.worldevents.event.WorldEventStopEvent;
import com.projectkorra.rpg.modules.worldevents.util.display.WorldEventDisplay;
import com.projectkorra.rpg.modules.worldevents.util.display.bossbar.BossBarDisplay;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class WorldEvent {

	private final Plugin plugin;
	private final WorldEventRegistry registry;
	private final NamespacedKey worldEventNamespacedKey;

	private final String key;
	private final String title;
	private final long duration;
	private final World world;
	private final List<WorldEventDisplay> displayMethods;
	private final List<World> disabledWorlds;
	private final FileConfiguration config;

	private final Set<Player> affectedPlayers = new HashSet<>();
	private BukkitTask timerTask;

	public WorldEvent(Plugin plugin, WorldEventRegistry registry, String key, String title, long duration,
					  List<World> disabledWorlds, FileConfiguration config, World world,
					  List<WorldEventDisplay> displayMethods) {
		this.plugin = plugin;
		this.registry = registry;
		this.key = key;
		this.title = title;
		this.duration = duration;
		this.disabledWorlds = disabledWorlds;
		this.config = config;
		this.world = world;
		this.displayMethods = (displayMethods == null || displayMethods.isEmpty())
				? Collections.emptyList()
				: List.copyOf(displayMethods);
		this.worldEventNamespacedKey = new NamespacedKey(plugin, key);
	}

	public void startEvent() {
		if (disabledWorlds.contains(world)) {
			plugin.getLogger().info("Couldn't start worldevent '" + key + "' because its world is disabled!");
			return;
		}

		registry.addActiveEvent(this);
		Bukkit.getPluginManager().callEvent(new WorldEventStartEvent(this));

		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getWorld() == this.world) {
				affectedPlayers.add(player);
				playStartSound(player);
			}
		}

		for (WorldEventDisplay display : displayMethods) {
			display.startDisplay(this);
		}

		startWorldEventTimer();
	}

	public void stopEvent() {
		Bukkit.getPluginManager().callEvent(new WorldEventStopEvent(this));
		registry.removeActiveEvent(this);

		for (Player player : getWorld().getPlayers()) {
			if (affectedPlayers.contains(player)) {
				playStopSound(player);
			}
		}

		for (WorldEventDisplay display : displayMethods) {
			display.stopDisplay(this);
		}

		if (timerTask != null && !timerTask.isCancelled()) {
			timerTask.cancel();
			timerTask = null;
		}

		affectedPlayers.clear();
	}

	public void updateDisplay(double progress) {
		for (WorldEventDisplay display : displayMethods) {
			display.updateDisplay(this, progress);
		}
	}

	private void startWorldEventTimer() {
		final long startTime = System.currentTimeMillis();
		long tickInterval = findBossBarDisplay().map(BossBarDisplay::isSmooth).orElse(false) ? 1 : 20;

		timerTask = new BukkitRunnable() {
			@Override
			public void run() {
				double elapsed = System.currentTimeMillis() - startTime;
				double progress = 1.0 - (elapsed / (double) duration);

				if (progress <= 0.0) {
					updateDisplay(0.0);
					stopEvent();
					return;
				}

				updateDisplay(progress);
			}
		}.runTaskTimer(plugin, 0, tickInterval);
	}

	private Optional<BossBarDisplay> findBossBarDisplay() {
		return displayMethods.stream()
				.filter(d -> d instanceof BossBarDisplay)
				.map(d -> (BossBarDisplay) d)
				.findFirst();
	}

	private void playStartSound(Player player) {
		if (!config.getBoolean("PlayEventStartSound", false)) return;
		playSound(player, "EventStart");
	}

	private void playStopSound(Player player) {
		if (!config.getBoolean("PlayEventStopSound", false)) return;
		playSound(player, "EventStop");
	}

	private void playSound(Player player, String configSection) {
		try {
			String soundName = config.getString(configSection + ".Sound", "ENTITY_EXPERIENCE_ORB_PICKUP");
			Sound sound = Sound.valueOf(soundName.toUpperCase());
			float volume = (float) config.getDouble(configSection + ".Volume", 1.0);
			float pitch = (float) config.getDouble(configSection + ".Pitch", 1.0);
			player.getWorld().playSound(player.getLocation(), sound, volume, pitch);
		} catch (IllegalArgumentException e) {
			plugin.getLogger().warning("Invalid sound configured for " + configSection + " in event " + key);
		}
	}

	// --- Getters ---

	public NamespacedKey getWorldEventNamespacedKey() {
		return worldEventNamespacedKey;
	}

	public String getKey() {
		return key;
	}

	public String getTitle() {
		return title;
	}

	public long getDuration() {
		return duration;
	}

	public World getWorld() {
		return world;
	}

	public List<WorldEventDisplay> getDisplayMethods() {
		return displayMethods;
	}

	public List<World> getDisabledWorlds() {
		return disabledWorlds;
	}

	public FileConfiguration getConfig() {
		return config;
	}

	public Set<Player> getAffectedPlayers() {
		return affectedPlayers;
	}

	public WorldEventRegistry getRegistry() {
		return registry;
	}

}
