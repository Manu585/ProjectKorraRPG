package com.projectkorra.rpg.modules.leveling.commands;

import com.projectkorra.rpg.commands.RPGCommand;
import com.projectkorra.rpg.modules.leveling.gui.MainMenu;
import com.projectkorra.rpg.ui.menu.Menu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class LevelCommand extends RPGCommand {
	private final Menu mainMenu = new MainMenu();

	public LevelCommand() {
		super("level", "/bending level", "Opens the leveling menu", new String[]{"level", "l", "le"});
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!(sender instanceof Player player)) {
			sender.sendMessage("Console may not execute this type of command!");
			return;
		}

		if (args.isEmpty()) {
			mainMenu.open(player);
		} else {
			help(sender, true);
		}
	}

	@Override
	protected List<String> getTabCompletion(CommandSender sender, List<String> args) {
		return Collections.emptyList();
	}
}
