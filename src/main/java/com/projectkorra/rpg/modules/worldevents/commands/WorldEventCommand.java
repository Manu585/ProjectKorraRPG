package com.projectkorra.rpg.modules.worldevents.commands;

import com.projectkorra.rpg.commands.RPGCommand;
import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.WorldEventRegistry;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldEventCommand extends RPGCommand {

	private final WorldEventRegistry registry;

	public WorldEventCommand(WorldEventRegistry registry) {
		super("event", "/bending rpg event start <Event>", "Starts a world event", new String[]{"event", "e", "ev"});
		this.registry = registry;
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!(sender instanceof Player player)) {
			sender.sendMessage("Console may not execute this type of command!");
			return;
		}

		if (args.size() < 2) {
			help(sender, true);
			return;
		}

		String action = args.get(0);
		String eventName = args.get(1);

		if (action.equalsIgnoreCase("start")) {
			WorldEvent we = registry.getEvent(eventName);
			if (we == null) {
				sender.sendMessage("WorldEvent '" + eventName + "' not found.");
			} else {
				we.startEvent();
			}

		} else if (action.equalsIgnoreCase("stop")) {
			WorldEvent we = registry.getEvent(eventName);
			if (we == null) {
				sender.sendMessage("WorldEvent '" + eventName + "' not found.");
			} else {
				we.stopEvent();
			}

		} else {
			help(sender, true);
		}
	}

	@Override
	protected List<String> getTabCompletion(CommandSender sender, List<String> args) {
		if (args.isEmpty()) {
			return Arrays.asList("start", "stop");
		}
		if (args.size() == 1 && (args.getFirst().equalsIgnoreCase("start") || args.getFirst().equalsIgnoreCase("stop"))) {
			return registry.getAllEvents().keySet().stream().sorted().toList();
		}
		return Collections.emptyList();
	}

}
