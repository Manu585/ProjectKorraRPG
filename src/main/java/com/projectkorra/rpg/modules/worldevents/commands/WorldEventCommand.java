package com.projectkorra.rpg.modules.worldevents.commands;

import com.projectkorra.rpg.commands.RPGCommand;
import com.projectkorra.rpg.modules.worldevents.models.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.service.WorldEventService;
import com.projectkorra.rpg.modules.worldevents.storage.ActiveWorldEventIndex;
import com.projectkorra.rpg.modules.worldevents.storage.WorldEventRegistry;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class WorldEventCommand extends RPGCommand {
    private final WorldEventService service;
    private final WorldEventRegistry registry;
    private final ActiveWorldEventIndex activeEventsIndex;

	public WorldEventCommand(final WorldEventService service, final WorldEventRegistry registry) {
		super("event", "/bending rpg event start <id> | /bending rpg event stop <id>", "Manage worldevents", new String[]{"event", "e", "ev"});
	    this.service = service;
        this.registry = registry;
        this.activeEventsIndex = service.getActiveEventsIndex();
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
                final WorldEvent worldEvent = registry.findByPath(idPath).orElse(null);
                if (worldEvent == null) {
                    sender.sendMessage("WorldEvent " + idPath + " not found!");
                    return;
                }
                if (activeEventsIndex.activeWorldEvents().contains(worldEvent)) {
                    sender.sendMessage("WorldEvent " + idPath + " is already active!");
                    return;
                }

                // START EVENT
                boolean started;
                if (sender instanceof Player player) {
                    World world = player.getWorld();
                    started = service.start(worldEvent, world);
                } else {
                    started = service.start(worldEvent);
                }

                if (started) {
                    sender.sendMessage("Started WorldEvent '" + worldEvent.getKey().getKey() + "'.");
                } else {
                    sender.sendMessage("Could not start WorldEvent '" + idPath + "'. Check logs for details.");
                }
            }

            case "stop" -> {
                // STOP ALL
                if (args.size() == 1) {
                    if (activeEventsIndex.activeWorldEvents().isEmpty()) {
                        sender.sendMessage("No active WorldEvents to stop.");
                        return;
                    }
                    service.stopAll();
                    sender.sendMessage("Stopped all active WorldEvents.");
                    return;
                }

                // STOP SPECIFIC
                if (args.size() == 2) {
                    final String idPath = args.get(1);
                    final WorldEvent we = registry.findByPath(idPath).orElse(null);
                    if (we == null) {
                        sender.sendMessage("WorldEvent '" + idPath + "' not found.");
                        return;
                    }
                    if (!activeEventsIndex.activeWorldEvents().contains(we)) {
                        sender.sendMessage("WorldEvent '" + idPath + "' is not active.");
                        return;
                    }
                    if (service.stop(we)) {
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
                final Set<WorldEvent> active = activeEventsIndex.activeWorldEvents();
                return registry.getAll().entrySet().stream().parallel()
                        .filter(e -> !active.contains(e.getValue()))
                        .map(e -> e.getKey().getKey())
                        .filter(id -> id.toLowerCase(Locale.ROOT).startsWith(partialId))
                        .sorted(String.CASE_INSENSITIVE_ORDER)
                        .collect(Collectors.toList());
            }

            if ("stop".equals(first)) {
                // Active events only
                return activeEventsIndex.activeWorldEvents().stream().parallel()
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
