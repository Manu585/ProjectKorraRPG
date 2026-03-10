package com.projectkorra.rpg.modules.leveling.commands;

import com.projectkorra.rpg.commands.RPGCommand;
import com.projectkorra.rpg.modules.leveling.gui.master.MainGui;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LevelCommand extends RPGCommand {
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
			new MainGui(player).show(player);
		} else {
			help(sender, true);
		}
	}

	@Override
	protected List<String> getTabCompletion(CommandSender sender, List<String> args) {
		return Collections.emptyList();
	}

}
