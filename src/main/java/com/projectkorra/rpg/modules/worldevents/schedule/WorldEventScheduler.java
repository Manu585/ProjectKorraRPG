package com.projectkorra.rpg.modules.worldevents.schedule;

import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.WorldEventRegistry;
import com.projectkorra.rpg.modules.worldevents.schedule.storage.ScheduleStorage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.plugin.Plugin;

public class WorldEventScheduler {

	private final Plugin plugin;
	private final WorldEventRegistry registry;
	private final ScheduleStorage scheduleStorage;

	private final Map<WorldEvent, ScheduledEventContext> scheduledEvents = new ConcurrentHashMap<>();

	public WorldEventScheduler(Plugin plugin, WorldEventRegistry registry, ScheduleStorage scheduleStorage) {
		this.plugin = plugin;
		this.registry = registry;
		this.scheduleStorage = scheduleStorage;

		initSchedules();
	}

	private void initSchedules() {
		cleanup();

		this.plugin.getLogger().info("Initializing WorldEventScheduler...");

		for (WorldEvent event : registry.getAllEvents().values()) {
			try {
				scheduleEvent(event);
			} catch (Exception e) {
				this.plugin.getLogger().severe("Failed to schedule event: " + event.getKey() + " - " + e.getMessage());
			}
		}
	}

	private void scheduleEvent(WorldEvent event) {
		cancelEvent(event);

		WorldEventScheduleStrategy scheduleStrategy = WorldEventScheduleStrategyFactory.get(event.getConfig(), this.scheduleStorage);

		ScheduledEventContext context = new ScheduledEventContext(event, scheduleStrategy);
		this.scheduledEvents.put(event, context);

		this.plugin.getLogger().info("Scheduling event: " + event.getKey() + " with strategy: " + scheduleStrategy.getClass().getSimpleName());

		scheduleStrategy.scheduleNext(event, this.plugin);
		context.setActive(true);
	}

	public void rescheduleEvent(WorldEvent event) {
		ScheduledEventContext context = this.scheduledEvents.get(event);
		if (context != null) {
			plugin.getLogger().info("Rescheduling event: " + event.getKey());
			context.getStrategy().scheduleNext(context.getEvent(), this.plugin);
		}
	}

	public void cancelEvent(WorldEvent event) {
		ScheduledEventContext context = this.scheduledEvents.get(event);
		if (context != null) {
			context.getStrategy().cancelSchedule();
			context.setActive(false);
			plugin.getLogger().info("Cancelled schedule for event: " + event.getKey());
		}
	}

	public void setEventActive(WorldEvent event, boolean active) {
		ScheduledEventContext context = this.scheduledEvents.get(event);
		if (context != null) {
			context.setActive(active);
		}
	}

	public boolean isEventScheduled(WorldEvent event) {
		ScheduledEventContext context = this.scheduledEvents.get(event);
		return context != null && context.isActive();
	}

	public void cleanup() {
		this.plugin.getLogger().info("Cleaning up WorldEventScheduler...");

		for (ScheduledEventContext context : scheduledEvents.values()) {
			try {
				context.getStrategy().cancelSchedule();
			} catch (Exception e) {
				this.plugin.getLogger().severe("Failed to cancel strategy for event: " + context.getEvent().getKey() + " - " + e.getMessage());
			}
		}

		scheduledEvents.clear();
	}

	private static class ScheduledEventContext {

		private final WorldEvent event;
		private final WorldEventScheduleStrategy strategy;

		private boolean active = false;

		public ScheduledEventContext(WorldEvent event, WorldEventScheduleStrategy strategy) {
			this.event = event;
			this.strategy = strategy;
		}

		public WorldEvent getEvent() {
			return event;
		}

		public WorldEventScheduleStrategy getStrategy() {
			return strategy;
		}

		public boolean isActive() {
			return active;
		}

		public void setActive(boolean active) {
			this.active = active;
		}

	}

}
