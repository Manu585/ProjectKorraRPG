package com.projectkorra.rpg.modules.worldevents.display;

import com.projectkorra.rpg.modules.worldevents.WorldEvent;

/**
 * Represents a display method for a world event (e.g. BossBar, Chat, Scoreboard).
 * Implementations handle how the event is visually presented to players.
 */
public interface WorldEventDisplay {

	void startDisplay(WorldEvent event);

	default void updateDisplay(WorldEvent event, double progress) {
		// Most displays don't need per-tick updates
	}

	default void stopDisplay(WorldEvent event) {
		// Override to clean up display resources
	}

}
