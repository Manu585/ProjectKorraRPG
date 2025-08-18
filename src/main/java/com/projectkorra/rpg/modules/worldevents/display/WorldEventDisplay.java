package com.projectkorra.rpg.modules.worldevents.display;

import com.projectkorra.rpg.modules.worldevents.models.WorldEvent;
import org.bukkit.World;

/**
 * Handles Lifecycle of messages / displays for {@link WorldEvent}
 */
public interface WorldEventDisplay {
	void startDisplay(WorldEvent event, World world);
	void stopDisplay(WorldEvent event, World world);
}

