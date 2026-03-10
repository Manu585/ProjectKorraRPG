package com.projectkorra.rpg.modules.worldevents.event;

import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NonNull;

public class WorldEventStartEvent extends Event {

	private final WorldEvent worldEvent;

	private static final HandlerList HANDLERS = new HandlerList();

	public WorldEventStartEvent(WorldEvent worldEvent) {
		this.worldEvent = worldEvent;
	}

	public WorldEvent getWorldEvent() {
		return worldEvent;
	}

	@Override
	public @NonNull HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

}
