package com.projectkorra.rpg.modules.worldevents.schedule;

import com.projectkorra.rpg.RPGMethods;
import com.projectkorra.rpg.modules.worldevents.schedule.storage.ScheduleStorage;
import com.projectkorra.rpg.modules.worldevents.schedule.strategies.EveryInGameDaysStrategy;
import com.projectkorra.rpg.modules.worldevents.schedule.strategies.EveryRealWorldDaysStrategy;
import com.projectkorra.rpg.modules.worldevents.schedule.strategies.util.ScheduleType;
import java.time.Duration;
import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.configuration.file.FileConfiguration;

public class WorldEventScheduleStrategyFactory {

	public static WorldEventScheduleStrategy get(FileConfiguration config, ScheduleStorage scheduleStorage) {
		String rawType = config.getString("Schedule.Calendar", "REALTIME");
		ScheduleType scheduleType = ScheduleType.fromString(rawType);

		LocalTime timeOfDay     = parseTimeOfDay(config.getString("Schedule.At", "7am"));
		Duration repeatDuration = RPGMethods.periodStringToDuration(config.getString("Schedule.Repeat", "7d"));
		Duration offsetDuration = RPGMethods.periodStringToDuration(config.getString("Schedule.Offset", "1d5h"));
		Duration cooldown       = RPGMethods.periodStringToDuration(config.getString("Schedule.Cooldown", "1d"));
		double chance           = config.getDouble("Schedule.TriggerChance", 0.5);

		return switch (scheduleType) {
			case REAL_DAYS -> new EveryRealWorldDaysStrategy(
					timeOfDay, repeatDuration, offsetDuration, chance, cooldown, scheduleStorage
			);
			case IN_GAME_DAYS -> new EveryInGameDaysStrategy(
					timeOfDay, repeatDuration, offsetDuration, chance, cooldown, scheduleStorage
			);
		};
	}

	/**
	 * Parses a human-readable time of day string like "7am", "3:30pm" into a LocalTime.
	 */
	private static LocalTime parseTimeOfDay(String timeStr) {
		timeStr = timeStr.toLowerCase().trim();

		Matcher matcher = Pattern.compile("(\\d{1,2})(?::(\\d{2}))?(am|pm)?").matcher(timeStr);

		if (matcher.matches()) {
			int hour = Integer.parseInt(matcher.group(1));
			int minute = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : 0;
			String period = matcher.group(3);

			if (period != null) {
				if (period.equals("pm") && hour < 12) {
					hour += 12;
				} else if (period.equals("am") && hour == 12) {
					hour = 0;
				}
			}

			return LocalTime.of(hour, minute);
		}

		return LocalTime.of(7, 0);
	}

}
