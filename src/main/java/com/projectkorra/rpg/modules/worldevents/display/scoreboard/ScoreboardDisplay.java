package com.projectkorra.rpg.modules.worldevents.display.scoreboard;

import com.projectkorra.rpg.modules.worldevents.models.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.display.WorldEventDisplay;
import org.bukkit.World;

/**
 * TODO: Don't know if we even want a scoreboard display since many users use custom scoreboard plugins or packet based plugins
 */
public class ScoreboardDisplay implements WorldEventDisplay {
	@Override
	public void startDisplay(WorldEvent event, World world) {}

	@Override
	public void stopDisplay(WorldEvent event, World world) {}
}
