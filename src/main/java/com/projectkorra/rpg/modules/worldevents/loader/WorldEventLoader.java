package com.projectkorra.rpg.modules.worldevents.loader;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.RPGMethods;
import com.projectkorra.rpg.modules.worldevents.builder.WorldEventBuilder;
import com.projectkorra.rpg.modules.worldevents.display.WorldEventDisplay;
import com.projectkorra.rpg.modules.worldevents.display.bossbar.BossBarDisplay;
import com.projectkorra.rpg.modules.worldevents.display.chat.ChatDisplay;
import com.projectkorra.rpg.modules.worldevents.display.scoreboard.ScoreboardDisplay;
import com.projectkorra.rpg.modules.worldevents.display.sound.SoundDisplay;
import com.projectkorra.rpg.modules.worldevents.models.AttributeRules;
import com.projectkorra.rpg.modules.worldevents.models.ScheduleSpecifications;
import com.projectkorra.rpg.modules.worldevents.models.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.schedule.ScheduleType;
import com.projectkorra.rpg.modules.worldevents.schedule.util.ScheduleParser;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Consumer;

public class WorldEventLoader {
    private final ProjectKorraRPG plugin;

    public WorldEventLoader(ProjectKorraRPG plugin) {
        this.plugin = plugin;
    }

    public Map<NamespacedKey, WorldEvent> loadEventsFromFolder() {
        Map<NamespacedKey, WorldEvent> result = new HashMap<>();

        File directory = new File(plugin.getDataFolder(), "WorldEvents");
        if (!directory.isDirectory()) {
            plugin.getLogger().warning("'WorldEvents' folder was not found!");
            return result;
        }

        File[] files = directory.listFiles((file, name) -> name.toLowerCase(Locale.ROOT).endsWith(".yml"));
        if (files == null || files.length == 0) {
            plugin.getLogger().info("'WorldEvents' folder does not contain any WorldEvent files.");
            return result;
        }

        int loaded = 0, skipped = 0;

        for (File file : files) {
            try {
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);

                // GATHER VALUES FROM CONFIG
                NamespacedKey key = new NamespacedKey(plugin, file.getName().substring(0, file.getName().length() - 4).toLowerCase(Locale.ROOT));
                String title = config.getString("Title");
                long duration = config.getLong("Duration");
                String worldName = config.getString("World");
                World world = (worldName == null || worldName.isBlank()) ? null : Bukkit.getWorld(worldName);
                List<WorldEventDisplay> displays = parseDisplays(config, key, title);
                List<World> disabledWorlds = config.getStringList("DisabledWorlds").stream().map(Bukkit::getWorld).filter(Objects::nonNull).toList();
                AttributeRules attributeRules = parseAttributeRules(config);
                ScheduleSpecifications scheduleSpecifications = parseSchedule(config);

                // CREATE BUILDER
                WorldEventBuilder builder = WorldEventBuilder.create()
                        .key(key)
                        .title(title)
                        .duration(duration)
                        .world(world)
                        .displays(displays)
                        .disabledWorlds(disabledWorlds)
                        .schedule(scheduleSpecifications)
                        .attributes(attributeRules);

                Consumer<String> log = msg -> plugin.getLogger().warning("Skipping '" + file.getName() + "': " + msg);

                // TRY TO BUILD
                Optional<WorldEvent> built = builder.tryBuild(log);
                if (built.isPresent()) {
                    // NON-NULL VALUES = Success
                    result.put(key, built.get());
                    loaded++;
                } else {
                    skipped++;
                }
            } catch (Exception exception) {
                plugin.getLogger().severe("Failed to load " + file.getName() + ": " + exception.getMessage());
                skipped++;
            }
        }

        plugin.getLogger().info("Loaded " + loaded + " WorldEvent(s), skipped " + skipped + ".");
        return result;
    }

    private List<WorldEventDisplay> parseDisplays(FileConfiguration config, NamespacedKey key, String title) {
        List<WorldEventDisplay> displays = new ArrayList<>();

        // BOSS BAR
        if (config.isConfigurationSection("DisplayMethods.BossBar") && config.isBoolean("DisplayMethods.BossBar.Enabled") && config.getBoolean("DisplayMethods.BossBar.Enabled")) {
            String colorRaw = config.getString("DisplayMethods.BossBar.Color");
            String styleRaw = config.getString("DisplayMethods.BossBar.Style");

            if (colorRaw == null || colorRaw.isBlank() || styleRaw == null || styleRaw.isBlank()) {
                plugin.getLogger().warning("WorldEvent '" + key + "': BossBar enabled but Color/Style missing. Skipping BossBar display.");
            } else {
                BarColor color = RPGMethods.convertStringToBarColor(colorRaw);
                BarStyle style = RPGMethods.convertStringToBarStyle(styleRaw);
                boolean smooth = config.isBoolean("DisplayMethods.BossBar.Smooth") && config.getBoolean("DisplayMethods.BossBar.Smooth");

                displays.add(new BossBarDisplay(key, title, color, style, smooth));
            }
        }

        // CHAT
        if (config.isConfigurationSection("DisplayMethods.Chat") && config.isBoolean("DisplayMethods.Chat.Enabled") && config.getBoolean("DisplayMethods.Chat.Enabled")) {
            String startMsg = config.getString("DisplayMethods.Chat.EventStartMessage");
            String stopMsg = config.getString("DisplayMethods.Chat.EventStopMessage");

            if (startMsg == null || startMsg.isBlank() || stopMsg == null || stopMsg.isBlank()) {
                plugin.getLogger().warning("WorldEvent '" + key + "': Chat enabled but start/stop message missing. Skipping Chat display.");
            } else {
                displays.add(new ChatDisplay(startMsg, stopMsg));
            }
        }

        // SCOREBOARD
        if (config.isConfigurationSection("DisplayMethods.Scoreboard") && config.isBoolean("DisplayMethods.Scoreboard.Enabled") && config.getBoolean("DisplayMethods.Scoreboard.Enabled")) {
            displays.add(new ScoreboardDisplay());
        }

        // SOUND
        Sound startSound = null; float startVolume = 1F; float startPitch = 1F;
        if (config.getBoolean("PlayEventStartSound", false)) {
            String id = config.getString("EventStart.Sound");
            if (id != null && !id.isBlank()) {
                startSound = RPGMethods.resolveSound(id);
                startVolume = (float) config.getDouble("EventStart.Volume", 1F);
                startPitch = (float) config.getDouble("EventStart.Pitch", 1F);
            }
        }

        Sound stopSound = null; float stopVolume = 1F; float stopPitch = 1F;
        if (config.getBoolean("PlayEventStopSound", false)) {
            String id = config.getString("EventStop.Sound");
            if (id != null && !id.isBlank()) {
                stopSound = RPGMethods.resolveSound(id);
                stopVolume = (float) config.getDouble("EventStop.Volume", 1F);
                stopPitch = (float) config.getDouble("EventStop.Pitch", 1F);
            }
        }

        if (startSound != null || stopSound != null) {
            displays.add(new SoundDisplay(startSound, startVolume, startPitch, stopSound, stopVolume, stopPitch));
        }

        return displays;
    }

    private AttributeRules parseAttributeRules(FileConfiguration config) {
        ConfigurationSection root = config.getConfigurationSection("Abilities");
        if (root == null) {
            return new AttributeRules(Map.of(), Map.of(), Map.of());
        }

        Map<String, Object> global = Map.of();
        ConfigurationSection globalSec = root.getConfigurationSection("_All");
        if (globalSec != null) {
            global = globalSec.getValues(false);
        }

        Map<String, Map<String, Object>> byElement = new HashMap<>();
        Map<String, Map<String, Map<String, Object>>> byAbility = new HashMap<>();

        for (String elemKey : root.getKeys(false)) {
            if ("_All".equalsIgnoreCase(elemKey)) continue;

            ConfigurationSection elemSec = root.getConfigurationSection(elemKey);
            if (elemSec == null) continue;

            ConfigurationSection elemAll = elemSec.getConfigurationSection("_All");
            if (elemAll != null) {
                byElement.put(elemKey, elemAll.getValues(false));
            }

            Map<String, Map<String, Object>> abilityMap = new HashMap<>();
            for (String child : elemSec.getKeys(false)) {
                if ("_All".equalsIgnoreCase(child)) continue;
                ConfigurationSection abilitySec = elemSec.getConfigurationSection(child);
                if (abilitySec == null) continue;

                Map<String, Object> attrs = abilitySec.getValues(false);
                if (!attrs.isEmpty()) {
                    abilityMap.put(child, attrs);
                }
            }
            if (!abilityMap.isEmpty()) {
                byAbility.put(elemKey, abilityMap);
            }
        }

        return new AttributeRules(global, byElement, byAbility);
    }

    /**
     * TEMP METHOD
     */
    private ScheduleSpecifications parseSchedule(FileConfiguration config) {
        ConfigurationSection sec = config.getConfigurationSection("Schedule");
        if (sec == null) {
            return null;
        }

        String rawCalendar = sec.getString("Calendar", "REALTIME");
        ScheduleType type = ScheduleType.fromString(rawCalendar);
        ScheduleSpecifications.Calendar calendar = (type == ScheduleType.IN_GAME_DAYS)
                ? ScheduleSpecifications.Calendar.IN_GAME_DAYS
                : ScheduleSpecifications.Calendar.REAL_DAYS;

        LocalTime at = ScheduleParser.parseTimeOfDay(sec.getString("At"), LocalTime.of(7, 0));
        Duration repeat = ScheduleParser.parseDuration(sec.getString("Repeat"), Duration.ofDays(7));
        Duration offset = ScheduleParser.parseDuration(sec.getString("Offset"), Duration.ZERO);
        Duration cooldown = ScheduleParser.parseDuration(sec.getString("Cooldown"), Duration.ofDays(1));
        double chance = sec.getDouble("TriggerChance", 0.5D);

        return new ScheduleSpecifications(calendar, at, repeat, offset, cooldown, chance);
    }
}
