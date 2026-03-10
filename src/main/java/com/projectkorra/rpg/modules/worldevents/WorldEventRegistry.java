package com.projectkorra.rpg.modules.worldevents;

import com.projectkorra.rpg.modules.worldevents.util.BossBarUtil;
import com.projectkorra.rpg.modules.worldevents.display.WorldEventDisplay;
import com.projectkorra.rpg.modules.worldevents.display.bossbar.BossBarDisplay;
import com.projectkorra.rpg.modules.worldevents.display.chat.ChatDisplay;
import com.projectkorra.rpg.modules.worldevents.display.scoreboard.ScoreboardDisplay;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * Central registry for all world events. Manages the lifecycle of event registration,
 * active event tracking, and event lookup.
 */
public class WorldEventRegistry {

    private final Plugin plugin;

    private final Map<String, WorldEvent> allEvents = new ConcurrentHashMap<>();
    private final Set<WorldEvent> activeEvents = ConcurrentHashMap.newKeySet();

    public WorldEventRegistry(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Loads all WorldEvent configurations from the WorldEvents directory.
     */
    public void loadAllEvents() {
        allEvents.clear();

        // Check if WorldEvents folder exists
        File worldEventsFolder = new File(plugin.getDataFolder(), "WorldEvents");
        if (!worldEventsFolder.exists() || !worldEventsFolder.isDirectory()) {
            plugin.getLogger().warning("WorldEvents folder was not found!");
            return;
        }

        // Check if there are yml files inside WorldEvents folder
        File[] worldEventsFiles = worldEventsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (worldEventsFiles == null || worldEventsFiles.length == 0) {
            plugin.getLogger().info("No WorldEvents were found.");
            return;
        }

        for (File file : worldEventsFiles) {
            Optional<WorldEvent> optWorldEvent = loadEvent(file);
            if (optWorldEvent.isEmpty()) {
                plugin.getLogger().severe("Failed to load world event from " + file.getName());
                continue;
            }

            WorldEvent event = optWorldEvent.get();
            allEvents.put(event.getKey(), event);
        }

        plugin.getLogger().info("Loaded " + allEvents.size() + " world event(s).");
    }

    private Optional<WorldEvent> loadEvent(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        String worldEventKey = file.getName().toLowerCase().replace(".yml", "");
        String eventTitle = config.getString("Title", "&cConfig Title not defined!");
        long duration = config.getLong("Duration", 5000);

        String configWorldName = config.getString("World", null);
        World world = (configWorldName == null ? null : Bukkit.getWorld(configWorldName));

        List<WorldEventDisplay> displayMethods = loadDisplayMethods(config, eventTitle);

        List<String> disabledWorldsStringList = config.getStringList("DisabledWorlds");
        List<World> disabledWorlds = new ArrayList<>();
        for (String worldName : disabledWorldsStringList) {
            World w = Bukkit.getWorld(worldName);
            if (w != null) {
                disabledWorlds.add(w);
            }
        }

        return Optional.of(new WorldEvent(plugin, this, worldEventKey, eventTitle, duration, disabledWorlds, config, world, displayMethods));
    }

    private List<WorldEventDisplay> loadDisplayMethods(FileConfiguration config, String eventTitle) {
        List<WorldEventDisplay> displayMethods = new ArrayList<>();

        if (config.getBoolean("DisplayMethods.BossBar.Enabled", false)) {
            BarColor bossBarColor = BossBarUtil.convertStringToBarColor(config.getString("DisplayMethods.BossBar.Color", "RED"));
            BarStyle bossBarStyle = BossBarUtil.convertStringToBarStyle(config.getString("DisplayMethods.BossBar.Style", "SOLID"));
            boolean smoothBossBar = config.getBoolean("DisplayMethods.BossBar.Smooth", true);
            displayMethods.add(new BossBarDisplay(eventTitle, bossBarColor, bossBarStyle, smoothBossBar));
        }

        if (config.getBoolean("DisplayMethods.Chat.Enabled", false)) {
            String eventStartMessage = config.getString("DisplayMethods.Chat.EventStartMessage", "&cEventStartMessage not defined!");
            String eventStopMessage = config.getString("DisplayMethods.Chat.EventStopMessage", "&cEventStopMessage not defined!");
            displayMethods.add(new ChatDisplay(eventStartMessage, eventStopMessage));
        }

        if (config.getBoolean("DisplayMethods.Scoreboard.Enabled", false)) {
            displayMethods.add(new ScoreboardDisplay());
        }

        return displayMethods;
    }

    public void addActiveEvent(WorldEvent event) {
        activeEvents.add(event);
    }

    public void removeActiveEvent(WorldEvent event) {
        activeEvents.remove(event);
    }

    public Map<String, WorldEvent> getAllEvents() {
        return Collections.unmodifiableMap(allEvents);
    }

    public Set<WorldEvent> getActiveEvents() {
        return Collections.unmodifiableSet(activeEvents);
    }

    public WorldEvent getEvent(String key) {
        return allEvents.get(key.toLowerCase());
    }

    /**
     * Stops all active events and clears all registrations.
     */
    public void clear() {
        new ArrayList<>(activeEvents).forEach(WorldEvent::stopEvent);
        activeEvents.clear();
        allEvents.clear();
    }

}
