package com.projectkorra.rpg.modules.worldevents;

import com.projectkorra.rpg.modules.Module;
import com.projectkorra.rpg.modules.worldevents.commands.WorldEventCommand;
import com.projectkorra.rpg.modules.worldevents.listeners.WorldEventModificationListener;
import com.projectkorra.rpg.modules.worldevents.listeners.WorldEventScheduleListener;
import com.projectkorra.rpg.modules.worldevents.methods.WorldEventModificationService;
import com.projectkorra.rpg.modules.worldevents.schedule.WorldEventScheduler;
import com.projectkorra.rpg.modules.worldevents.schedule.storage.ScheduleStorage;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

public class WorldEventModule extends Module {

	private WorldEventRegistry registry;
	private WorldEventModificationListener modificationListener;
	private WorldEventModificationService modificationService;
	private WorldEventScheduleListener scheduleListener;
	private WorldEventScheduler worldEventScheduler;
	private ScheduleStorage scheduleStorage;

	public WorldEventModule(Plugin plugin) {
		super(plugin, "WorldEvents");
	}

	@Override
	public void enable() {
		getPlugin().getLogger().info("Enabling WorldEvent module...");

		// Create the registry and load all events
		this.registry = new WorldEventRegistry(getPlugin());
		this.registry.loadAllEvents(); // March 10th, Manu - Perhaps CompletableFuture for non-blocking I/O

		// Create services
		this.modificationService = new WorldEventModificationService(getPlugin(), registry);
		this.scheduleStorage = new ScheduleStorage(getPlugin());

		// Create listeners and scheduler
		this.scheduleListener = new WorldEventScheduleListener();
		this.worldEventScheduler = new WorldEventScheduler(getPlugin(), registry, scheduleStorage);
		this.scheduleListener.setScheduler(this.worldEventScheduler);

		this.modificationListener = new WorldEventModificationListener(this.modificationService);

		// Register commands
		new WorldEventCommand(registry);

		// Register listeners
		registerListeners(this.modificationListener, this.scheduleListener);

		getPlugin().getLogger().info("WorldEvent module enabled successfully!");
	}

	@Override
	public void disable() {
		getPlugin().getLogger().info("Disabling WorldEvent module...");

		if (this.worldEventScheduler != null) {
			this.worldEventScheduler.cleanup();
			this.worldEventScheduler = null;
		}

		if (this.registry != null) {
			this.registry.clear();
		}

		if (this.modificationListener != null) {
			HandlerList.unregisterAll(this.modificationListener);
			this.modificationListener = null;
		}

		if (this.scheduleListener != null) {
			HandlerList.unregisterAll(this.scheduleListener);
			this.scheduleListener = null;
		}

		getPlugin().getLogger().info("WorldEvent module disabled successfully!");
	}

	public WorldEventRegistry getRegistry() {
		return registry;
	}

	public WorldEventModificationService getModificationService() {
		return modificationService;
	}

	public WorldEventScheduler getWorldEventScheduler() {
		return this.worldEventScheduler;
	}

	public ScheduleStorage getScheduleStorage() {
		return this.scheduleStorage;
	}
}
