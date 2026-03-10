package com.projectkorra.rpg;

import com.projectkorra.projectkorra.event.BendingReloadEvent;
import com.projectkorra.rpg.commands.HelpCommand;
import com.projectkorra.rpg.commands.RPGCommand;
import com.projectkorra.rpg.commands.RPGCommandBase;
import com.projectkorra.rpg.configuration.ConfigManager;
import com.projectkorra.rpg.modules.ModuleManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class RPGListener implements Listener {

	private final Plugin plugin;
	private final ModuleManager moduleManager;

	public RPGListener(Plugin plugin, ModuleManager moduleManager) {
		this.plugin = plugin;
		this.moduleManager = moduleManager;
	}

	@EventHandler
	public void onBendingConfigReload(BendingReloadEvent event) {
		moduleManager.disableModules();

		ConfigManager.defaultConfig.reload();
		ConfigManager.languageConfig.reload();

		new BukkitRunnable() {
			@Override
			public void run() {
				RPGCommand.instances.clear();
				new RPGCommandBase();
				new HelpCommand();
			}
		}.runTaskLater(plugin, 20);

		moduleManager.enableModules();
	}

}
