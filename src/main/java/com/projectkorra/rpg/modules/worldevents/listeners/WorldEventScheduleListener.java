package com.projectkorra.rpg.modules.worldevents.listeners;

import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.event.WorldEventStartEvent;
import com.projectkorra.rpg.modules.worldevents.event.WorldEventStopEvent;
import com.projectkorra.rpg.modules.worldevents.schedule.WorldEventScheduler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class WorldEventScheduleListener implements Listener {

	private WorldEventScheduler scheduler;

	public WorldEventScheduleListener() {}

	public void setScheduler(WorldEventScheduler scheduler) {
		this.scheduler = scheduler;
	}

	@EventHandler
	public void onWorldEventStart(WorldEventStartEvent event) {
		if (scheduler != null) {
			scheduler.setEventActive(event.getWorldEvent(), true);
		}
	}

	@EventHandler
	public void onWorldEventStop(WorldEventStopEvent event) {
		if (scheduler == null) return;

		WorldEvent worldEvent = event.getWorldEvent();
		scheduler.setEventActive(worldEvent, false);
		scheduler.rescheduleEvent(worldEvent);
	}

}
