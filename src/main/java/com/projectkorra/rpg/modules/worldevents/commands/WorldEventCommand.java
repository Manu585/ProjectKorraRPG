package com.projectkorra.rpg.modules.worldevents.commands;

import com.projectkorra.rpg.commands.RPGCommand;
import com.projectkorra.rpg.modules.worldevents.manager.WorldEventManager;
import com.projectkorra.rpg.modules.worldevents.models.WorldEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Collectors;

public class WorldEventCommand extends RPGCommand {
    private final WorldEventManager manager;

	public WorldEventCommand(final WorldEventManager manager) {
		super("event", "/bending rpg event start <id> | /bending rpg event stop <id>", "Manage worldevents", new String[]{"event", "e", "ev"});
	    this.manager = manager;
    }

	@Override
	public void execute(CommandSender sender, List<String> args) {
        if (args.isEmpty()) {
            help(sender, true);
            return;
        }

        final String sub = args.getFirst().toLowerCase(Locale.ROOT);

        switch (sub) {
            case "start" -> {
                if (args.size() != 2) {
                    help(sender, false);
                    return;
                }

                final String idPath = args.get(1);
                final WorldEvent worldEvent = manager.findEvent(idPath).orElse(null);
                if (worldEvent == null) {
                    sender.sendMessage("WorldEvent " + idPath + " not found!");
                    return;
                }
                if (manager.getActiveEvents().contains(worldEvent)) {
                    sender.sendMessage("Worldevent " + idPath + " is already active!");
                    return;
                }

                // START EVENT
                if (manager.start(worldEvent)) {
                    sender.sendMessage("Started WorldEvent '" + worldEvent.getKey().getKey() + "'");
                } else {
                    sender.sendMessage("Could not start WorldEvent '" + idPath + "'. Check logs for more details.");
                }
            }

            case "stop" -> {
                // STOP ALL
                if (args.size() == 1) {
                    if (manager.getActiveEvents().isEmpty()) {
                        sender.sendMessage("No active WorldEvents to stop.");
                        return;
                    }
                    manager.stopAll();
                    sender.sendMessage("Stopped all active WorldEvents.");
                    return;
                }

                // STOP SPECIFIC
                if (args.size() == 2) {
                    final String idPath = args.get(1);
                    final WorldEvent we = manager.findEvent(idPath).orElse(null);
                    if (we == null) {
                        sender.sendMessage("WorldEvent '" + idPath + "' not found.");
                        return;
                    }
                    if (!manager.getActiveEvents().contains(we)) {
                        sender.sendMessage("WorldEvent '" + idPath + "' is not active.");
                        return;
                    }
                    if (manager.stop(we)) {
                        sender.sendMessage("Stopped world event '" + we.getKey().getKey() + "'.");
                    } else {
                        sender.sendMessage("Could not stop world event '" + idPath + "'. Check logs for details.");
                    }
                } else {
                    help(sender, false);
                }
            }

            default -> help(sender, true);
        }
    }

    @Override
    protected List<String> getTabCompletion(CommandSender sender, List<String> args) {
        if (args.isEmpty()) {
            return List.of("start", "stop");
        }

        if (args.size() == 1) {
            final String first = args.getFirst().toLowerCase(Locale.ROOT);
            final String partialId = (args.size() > 1 ? args.get(1) : "").toLowerCase(Locale.ROOT);

            if ("start".equals(first)) {
                // Inactive events only
                final Set<WorldEvent> active = manager.getActiveEvents();
                return manager.getLoadedWorldEvents().entrySet().stream()
                        .filter(e -> !active.contains(e.getValue()))
                        .map(e -> e.getKey().getKey())
                        .filter(id -> id.toLowerCase(Locale.ROOT).startsWith(partialId))
                        .sorted(String.CASE_INSENSITIVE_ORDER)
                        .collect(Collectors.toList());
            }

            if ("stop".equals(first)) {
                // Active events only
                return manager.getActiveEvents().stream()
                        .map(WorldEvent::getKey)
                        .map(NamespacedKey::getKey)
                        .filter(id -> id.toLowerCase(Locale.ROOT).startsWith(partialId))
                        .sorted(String.CASE_INSENSITIVE_ORDER)
                        .collect(Collectors.toList());
            }
        }

        return Collections.emptyList();
    }
}
