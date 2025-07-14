package com.projectkorra.rpg.modules;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.elementassignments.ElementAssignModule;
import com.projectkorra.rpg.modules.leveling.LevelingModule;
import com.projectkorra.rpg.modules.randomavatar.AvatarCycleModule;
import com.projectkorra.rpg.modules.worldevents.WorldEventModule;

import java.util.List;

public class ModuleManager {
	private final List<Module> modules;

	private final WorldEventModule worldEventModule;
	private final LevelingModule levelingModuleModule;
	private final AvatarCycleModule avatarCycleModule;
	private final ElementAssignModule elementAssignModule;

	public ModuleManager(ProjectKorraRPG plugin) {
		this.worldEventModule = new WorldEventModule(plugin);
		this.levelingModuleModule = new LevelingModule(plugin);
		this.avatarCycleModule = new AvatarCycleModule(plugin);
		this.elementAssignModule = new ElementAssignModule(plugin);

		this.modules = List.of(
				worldEventModule,
				levelingModuleModule,
				avatarCycleModule,
				elementAssignModule
		);
	}

	public void enableModules() {
		for (Module module : modules) {
			if (module.isEnabled()) {
				module.enable();
			}
		}
	}

	public void disableModules() {
		for (Module module : modules) {
			module.disable();
		}
	}

	public List<Module> getModules() {
		return modules;
	}

	public WorldEventModule getWorldEventsModule() {
		if (worldEventModule.isEnabled()) {
			return worldEventModule;
		}
		throw new IllegalStateException("WorldEvents Module is disabled! Enable it in config.yml");
	}

	public LevelingModule getRpgLevelingModule() {
		if (levelingModuleModule.isEnabled()) {
			return levelingModuleModule;
		}
		throw new IllegalStateException("Level Module is disabled! Enable it in config.yml");
	}

	public AvatarCycleModule getRandomAvatarModule() {
		if (avatarCycleModule.isEnabled()) {
			return avatarCycleModule;
		}
		throw new IllegalStateException("AvatarCycle Module is disabled! Enable it in config.yml");
	}

	public ElementAssignModule getElementAssignmentsModule() {
		if (elementAssignModule.isEnabled()) {
			return elementAssignModule;
		}
		throw new IllegalStateException("ElementAssign Module is disabled! Enable it in config.yml");
	}
}
