package com.projectkorra.rpg.modules;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.elementassignments.ElementAssignModule;
import com.projectkorra.rpg.modules.leveling.LevelingModule;
import com.projectkorra.rpg.modules.randomavatar.AvatarCycleModule;
import com.projectkorra.rpg.modules.worldevents.WorldEventModule;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
	private final List<Module> modules = new ArrayList<>();

	private final WorldEventModule worldEventModule;
	private final LevelingModule levelingModuleModule;
	private final AvatarCycleModule avatarCycleModule;
	private final ElementAssignModule elementAssignModule;

	public ModuleManager(ProjectKorraRPG plugin) {
		modules.add(worldEventModule = new WorldEventModule(plugin));
		modules.add(levelingModuleModule = new LevelingModule(plugin));
		modules.add(avatarCycleModule = new AvatarCycleModule(plugin));
		modules.add(elementAssignModule = new ElementAssignModule(plugin));
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
