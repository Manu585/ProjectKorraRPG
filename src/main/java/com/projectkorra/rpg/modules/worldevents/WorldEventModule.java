package com.projectkorra.rpg.modules.worldevents;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.Module;
import com.projectkorra.rpg.modules.worldevents.commands.WorldEventCommand;
import com.projectkorra.rpg.modules.worldevents.listener.HandleWorldEventDisplayListener;
import com.projectkorra.rpg.modules.worldevents.listener.WorldEventModificationListener;
import com.projectkorra.rpg.modules.worldevents.loader.WorldEventLoader;
import com.projectkorra.rpg.modules.worldevents.manager.WorldEventManager;
import com.projectkorra.rpg.modules.worldevents.methods.WorldEventModificationService;
import org.bukkit.event.HandlerList;

public class WorldEventModule extends Module {
    private WorldEventManager worldEventManager;
	private WorldEventModificationListener modificationListener;
    private HandleWorldEventDisplayListener handleWorldEventDisplayListener;

	public WorldEventModule(ProjectKorraRPG plugin) {
		super(plugin, "WorldEvents");
	}

	@Override
	public void enable() {
		this.getPlugin().getLogger().info("Enabling WorldEvent module...");

        // Handles CRUD functionality for WorldEvents and handles general State / Memory
        this.worldEventManager = new WorldEventManager(this.getPlugin());

        // Register all valid WorldEvents from configurations
        this.worldEventManager.registerAll(new WorldEventLoader(getPlugin()).loadEventsFromFolder().values());

		// Create Listeners
		this.modificationListener = new WorldEventModificationListener(new WorldEventModificationService(this.worldEventManager));
        this.handleWorldEventDisplayListener = new HandleWorldEventDisplayListener(this.worldEventManager);

        // Register Listeners
		registerListeners(
				this.modificationListener,
                this.handleWorldEventDisplayListener
		);

        // Register Commands
        new WorldEventCommand(this.worldEventManager);

		this.getPlugin().getLogger().info("WorldEvent module enabled successfully!");
	}

	@Override
	public void disable() {
		this.getPlugin().getLogger().info("Disabling WorldEvent module...");

        if (this.worldEventManager != null) this.worldEventManager.stopAll();

		// Unregister ModificationListener
		if (this.modificationListener != null) {
			HandlerList.unregisterAll(this.modificationListener);
			this.modificationListener = null;
		}

        if (this.handleWorldEventDisplayListener != null) {
            HandlerList.unregisterAll(this.handleWorldEventDisplayListener);
            this.handleWorldEventDisplayListener = null;
        }

		this.getPlugin().getLogger().info("WorldEvent module disabled successfully!");
	}

    public WorldEventManager getWorldEventManager() {
        return worldEventManager;
    }

    public WorldEventModificationListener getModificationListener() {
		return modificationListener;
	}

    public HandleWorldEventDisplayListener getPlayerSwitchWorldListener() {
        return handleWorldEventDisplayListener;
    }
}
