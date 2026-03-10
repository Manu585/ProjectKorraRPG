package com.projectkorra.rpg;

import java.time.Duration;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RPGMethods {

	private static final Pattern DURATION_PATTERN = Pattern.compile("(\\d+)([wdhms])");

	/**
	 * Converts a period string like "3d4h5m10s" to a Duration object.
	 * Supports: w (weeks), d (days), h (hours), m (minutes), s (seconds).
	 */
	public static Duration periodStringToDuration(String period) {
		Duration duration = Duration.ZERO;
		if (period == null || period.isEmpty()) {
			Logger.getLogger("ProjectKorraRPG").warning("Invalid period string: " + period);
			return duration;
		}

		Matcher matcher = DURATION_PATTERN.matcher(period.toLowerCase().trim());
		while (matcher.find()) {
			long value = Long.parseLong(matcher.group(1));
			String unit = matcher.group(2);
			duration = switch (unit) {
				case "w" -> duration.plusDays(value * 7);
				case "d" -> duration.plusDays(value);
				case "h" -> duration.plusHours(value);
				case "m" -> duration.plusMinutes(value);
				case "s" -> duration.plusSeconds(value);
				default -> duration;
			};
		}

		if (duration.isZero()) {
			Logger.getLogger("ProjectKorraRPG").warning("Could not parse any duration from: " + period);
		}

		return duration;
	}

}
