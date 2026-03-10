package com.projectkorra.rpg.modules;

import com.projectkorra.rpg.modules.elementassignments.ElementAssignModule;
import com.projectkorra.rpg.modules.leveling.LevelingModule;
import com.projectkorra.rpg.modules.randomavatar.AvatarCycleModule;
import com.projectkorra.rpg.modules.worldevents.WorldEventModule;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.plugin.Plugin;

public class ModuleManager {

	private final WorldEventModule worldEventModule;
	private final LevelingModule levelingModule;
	private final AvatarCycleModule avatarCycleModule;
	private final ElementAssignModule elementAssignModule;

	private final Set<Module> modules = new HashSet<>();

	public ModuleManager(Plugin plugin) {
		modules.add(worldEventModule = new WorldEventModule(plugin));
		modules.add(levelingModule = new LevelingModule(plugin));
		modules.add(avatarCycleModule = new AvatarCycleModule(plugin));
		modules.add(elementAssignModule = new ElementAssignModule(plugin));
	}

	public void enableModules() {
		for (Module module : modules) {
			if (module.isEnabled()) {
				try {
					module.enable();
				} catch (Exception e) {
					module.getPlugin().getLogger().severe("Failed to enable module " + module.getName() + ": " + e.getMessage());
				}
			}
		}
	}

	public void disableModules() {
		for (Module module : modules) {
			try {
				module.disable();
			} catch (Exception e) {
				module.getPlugin().getLogger().severe("Failed to disable module " + module.getName() + ": " + e.getMessage());
			}
		}
	}

	public Set<Module> getModules() {
		return modules;
	}

	public WorldEventModule getWorldEventsModule() {
		return worldEventModule;
	}

	public LevelingModule getLevelingModule() {
		return levelingModule;
	}

	public AvatarCycleModule getAvatarCycleModule() {
		return avatarCycleModule;
	}

	public ElementAssignModule getElementAssignmentsModule() {
		return elementAssignModule;
	}

}
