package com.projectkorra.rpg.modules.worldevents;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.worldevents.event.WorldEventStartEvent;
import com.projectkorra.rpg.modules.worldevents.event.WorldEventStopEvent;
import com.projectkorra.rpg.modules.worldevents.util.DisplayHelper;
import com.projectkorra.rpg.modules.worldevents.util.display.TickingDisplay;
import com.projectkorra.rpg.modules.worldevents.util.display.WorldEventDisplay;
import com.projectkorra.rpg.modules.worldevents.util.display.ViewerDisplay;
import com.projectkorra.rpg.modules.worldevents.util.display.bossbar.BossBarDisplay;
import com.projectkorra.rpg.modules.worldevents.util.display.chat.ChatDisplay;
import com.projectkorra.rpg.modules.worldevents.util.display.scoreboard.ScoreboardDisplay;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public class WorldEvent implements org.bukkit.Keyed {
	private static final HashMap<NamespacedKey, WorldEvent> ALL_EVENTS = new HashMap<>();
    private static final Map<String, NamespacedKey> EVENTS_BY_PATH = new HashMap<>();
    private static final HashSet<WorldEvent> ACTIVE_EVENTS = new HashSet<>();

    private final Set<UUID> affectedPlayers = new HashSet<>();

	private final NamespacedKey key;
	private String title;
	private long duration;
	private World world;

	private List<WorldEventDisplay> displayMethods;
	private List<World> disabledWorlds;

	private final FileConfiguration config;

    private @Nullable BukkitTask timerTask;

	public WorldEvent(NamespacedKey key, String title, long duration, List<World> disabledWorlds, FileConfiguration config, World world, List<WorldEventDisplay> displayMethods) {
		this.key = key;
		this.title = title;
		this.duration = duration;
		this.disabledWorlds = disabledWorlds;
		this.config = config;
		this.world = world;
		this.displayMethods = (displayMethods == null || displayMethods.isEmpty()) ? List.of() : new ArrayList<>(displayMethods);
	}

	public void startEvent() {
		if (world == null || disabledWorlds.contains(world)) {
			ProjectKorraRPG.getPlugin().getLogger().info("Couldn't start worldevent because world is a disabled world!");
			return;
		}

        if (ACTIVE_EVENTS.contains(this)) return;

		ACTIVE_EVENTS.add(this);
		Bukkit.getPluginManager().callEvent(new WorldEventStartEvent(this));

		// Add all online players in the world to the Set
		// And play Sound if user configured
		for (Player player : world.getPlayers()) {
            affectedPlayers.add(player.getUniqueId());
            if (getConfig().getBoolean("PlayEventStartSound")) {
                Sound eventStartSound = resolveSound(getConfig().getString("EventStart.Sound"), Sound.AMBIENT_CAVE);
                float volume = (float) getConfig().getDouble("EventStart.Volume", 1.0F);
                float pitch = (float) getConfig().getDouble("EventStart.Pitch", 1.0F);
                player.getWorld().playSound(player.getLocation(), eventStartSound, volume, pitch);
            }
		}

		// Start the display for the event
		for (WorldEventDisplay display : displayMethods) display.startDisplay(this);
		startWorldEventTimer();
	}

	/**
	 * Stop active WorldEvent
	 */
	public void stopEvent() {
		Bukkit.getPluginManager().callEvent(new WorldEventStopEvent(this));
		ACTIVE_EVENTS.remove(this);

        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }

		// Play EventStop sound for each player in an active WorldEvent world
		for (Player player : world.getPlayers()) {
			if (affectedPlayers.contains(player.getUniqueId())) {
				if (getConfig().getBoolean("PlayEventStopSound")) {
					Sound eventStopSound = resolveSound(getConfig().getString("EventStop.Sound"), Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
					float volume = (float) getConfig().getDouble("EventStop.Volume", 1.0F);
					float pitch = (float) getConfig().getDouble("EventStop.Pitch", 1.0F);
                    player.getWorld().playSound(player.getLocation(), eventStopSound, volume, pitch);
				}
			}
		}

		// Stop the display for the event
		for (WorldEventDisplay display : displayMethods) display.stopDisplay(this);
        affectedPlayers.clear();
	}

	// Updated WorldEvent display
	public void updateDisplay(double progress) {
		for (WorldEventDisplay display : displayMethods) {
			display.updateDisplay(this, progress);
		}
	}

	/**
	 * Puts all WorldEvents from WorldEvents directory into the {@link WorldEvent#getAllEvents()} map
	 */
	public static void initAllWorldEvents(ProjectKorraRPG plugin) {
		File worldEventsFolder = new File(plugin.getDataFolder(), "WorldEvents");
		if (!worldEventsFolder.exists() || !worldEventsFolder.isDirectory()) {
			plugin.getLogger().warning("WorldEvents folder was not found!");
			return;
		}

		File[] worldEventsFiles = worldEventsFolder.listFiles(((dir, name) -> name.endsWith(".yml")));
		if (worldEventsFiles == null || worldEventsFiles.length == 0) {
			plugin.getLogger().info("No WorldEvents were found.");
			return;
		}

		// Iterate through all WorldEvent configurations
		Arrays.stream(worldEventsFiles).forEach(file -> {
			NamespacedKey eventKey = new NamespacedKey(plugin, file.getName().toLowerCase(Locale.ROOT).replace(".yml", "")); // Event key is file name without yml extension

            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			String eventTitle = config.getString("Title", "&cConfig Title not defined!");
			long duration = config.getLong("Duration", 1000);

			String configWorldName = config.getString("World", null);
			World world = (configWorldName == null) ? null : Bukkit.getWorld(configWorldName);

			List<WorldEventDisplay> displayMethods = new ArrayList<>();

			// BossBar-Display
			if (config.getBoolean("DisplayMethods.BossBar.Enabled", false)) {
				BarColor bossBarColor = DisplayHelper.convertStringToBarColor(config.getString("DisplayMethods.BossBar.Color", "RED"));
				BarStyle bossBarStyle = DisplayHelper.convertStringToBarStyle(config.getString("DisplayMethods.BossBar.Style", "SOLID"));
				boolean smoothBossBar = config.getBoolean("DisplayMethods.BossBar.Smooth", true);

				displayMethods.add(new BossBarDisplay(eventKey, eventTitle, bossBarColor, bossBarStyle, smoothBossBar));
			}

			// Chat-Display
			if (config.getBoolean("DisplayMethods.Chat.Enabled", false)) {
				String eventStartMessage = config.getString("DisplayMethods.Chat.EventStartMessage", "&cEventStartMessage not defined!");
				String eventStopMessage = config.getString("DisplayMethods.Chat.EventStopMessage", "&cEventStopMessage not defined!");

				displayMethods.add(new ChatDisplay(eventStartMessage, eventStopMessage));
			}

			// Scoreboard - Display
			if (config.getBoolean("DisplayMethods.Scoreboard.Enabled", false)) {
				displayMethods.add(new ScoreboardDisplay());
			}

			// Parse Disabled Worlds
            List<World> disabled = new ArrayList<>();
            for (String worldName : config.getStringList("DisabledWorlds")) {
                World w = Bukkit.getWorld(worldName);
                if (w != null) disabled.add(w);
            }

			ALL_EVENTS.put(eventKey, new WorldEvent(eventKey, eventTitle, duration, disabled, config, world, displayMethods));
            EVENTS_BY_PATH.put(eventKey.getKey(), eventKey);
		});
	}

	private void startWorldEventTimer() {
		final long duration = this.duration;
		final long startTime = System.currentTimeMillis();

        long period = 20L;
        for (WorldEventDisplay display : displayMethods) {
            if (display instanceof TickingDisplay tickingDisplay) period = Math.min(period, Math.max(1L, tickingDisplay.tickPeriod()));
        }

		this.timerTask = new BukkitRunnable() {
			@Override
			public void run() {
				double elapsed = System.currentTimeMillis() - startTime;
				double progress = 1.0 - (elapsed / (double) duration);

				if (progress <= 0.0) {
					updateDisplay(0.0);
					stopEvent();
					cancel();
					return;
				}

				updateDisplay(Math.max(0.0, Math.min(1.0, progress)));
			}
		}.runTaskTimer(ProjectKorraRPG.getPlugin(), 0L, period);
	}

    public boolean addAffected(UUID uuid) {
        return affectedPlayers.add(uuid);
    }

    public boolean removeAffected(UUID uuid) {
        return affectedPlayers.remove(uuid);
    }

    /**
     * Notify displays that a single viewer was added
     * @param player Viewer
     */
    public void notifyViewerAdded(Player player) {
        for (WorldEventDisplay display : displayMethods) {
            if (display instanceof ViewerDisplay viewerDisplay) viewerDisplay.addViewer(player);
        }
    }

    /**
     * Notify displays that a single viewer was removed
     * @param player Viewer
     */
    public void notifyViewerRemoved(Player player) {
        for (WorldEventDisplay display : displayMethods) {
            if (display instanceof  ViewerDisplay viewerDisplay) viewerDisplay.removeViewer(player);
        }
    }

    private static Sound resolveSound(String raw, Sound fallback) {
        if (raw == null || raw.isBlank()) return fallback;

        String id = raw.trim().toLowerCase(Locale.ROOT);
        NamespacedKey key = (id.contains(":")) ? NamespacedKey.fromString(id) : NamespacedKey.minecraft(id);

        if (key != null) {
            Sound sound = Registry.SOUNDS.get(key);
            if (sound != null) return sound;
        }

        return fallback;
    }

    public static Optional<WorldEvent> getByPath(String path) {
        if (path == null) return Optional.empty();
        NamespacedKey key = EVENTS_BY_PATH.get(path.toLowerCase(Locale.ROOT));
        return (key == null) ? Optional.empty() : Optional.ofNullable(ALL_EVENTS.get(key));
    }

    public static void clearRegistries() {
        ACTIVE_EVENTS.clear();
        ALL_EVENTS.clear();
        EVENTS_BY_PATH.clear();
    }

    public static Map<NamespacedKey, WorldEvent> getAllEvents() {
        return Collections.unmodifiableMap(ALL_EVENTS);
    }

    public static Map<String, NamespacedKey> getEventsByPath() {
        return Collections.unmodifiableMap(EVENTS_BY_PATH);
    }

    public static Set<WorldEvent> getActiveEvents() {
        return Collections.unmodifiableSet(ACTIVE_EVENTS);
    }

    public Set<UUID> getAffectedPlayers() {
        return Collections.unmodifiableSet(affectedPlayers);
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

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public void setDisplayMethods(List<WorldEventDisplay> displayMethods) {
		this.displayMethods = displayMethods;
	}

	public void setDisabledWorlds(List<World> disabledWorlds) {
		this.disabledWorlds = disabledWorlds;
	}

    public String id() {
        return key.getKey();
    }

    public String idFull() {
        return key.toString();
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return this.key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorldEvent other)) return false;
        return key.equals(other.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
