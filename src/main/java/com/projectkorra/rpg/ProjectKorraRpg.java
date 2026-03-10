package com.projectkorra.rpg;

import com.projectkorra.rpg.plugin.PluginBootstrap;
import com.projectkorra.rpg.plugin.ProjectKorraRpgPlugin;
import org.bukkit.plugin.java.JavaPlugin;

public class ProjectKorraRpg extends JavaPlugin {

	private final PluginBootstrap pluginBootstrap = new ProjectKorraRpgPlugin(this);

	@Override
	public void onLoad() {
		pluginBootstrap.onLoad();
	}

	@Override
	public void onEnable() {
		pluginBootstrap.onEnable();
	}

	@Override
	public void onDisable() {
		pluginBootstrap.onDisable();
	}

}
