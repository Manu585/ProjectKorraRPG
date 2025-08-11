package com.projectkorra.rpg.modules.worldevents.commands;

import com.projectkorra.rpg.commands.RPGCommand;
import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Stream;

public class WorldEventCommand extends RPGCommand {
	public WorldEventCommand() {
		super("event", "/bending rpg event start <id> | /bending rpg event stop <id>", "Manage worldevents", new String[]{"event", "e", "ev"});
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
        if (args.isEmpty() || args.size() < 2) {
            help(sender, true);
            return;
        }

        String sub = args.getFirst().toLowerCase(Locale.ROOT);

        switch (sub) {
            case "start" -> {
                if (args.size() != 2) {
                    help(sender, false);
                    return;
                }

                String idPath = args.get(1);
                WorldEvent we = WorldEvent.getByPath(idPath).orElse(null);
                if (we == null) {
                    sender.sendMessage("WorldEvent " + idPath + " not found!");
                    return;
                }
                if (WorldEvent.getActiveEvents().contains(we)) {
                    sender.sendMessage("Worldevent " + idPath + " is already active!");
                    return;
                }
                we.startEvent();
            }

            case "stop" -> {
                if (args.size() == 1) {
                    ArrayList<WorldEvent> snapshot = new ArrayList<>(WorldEvent.getActiveEvents());
                    if (snapshot.isEmpty()) {
                        sender.sendMessage("No active WorldEvents to stop");
                        return;
                    }

                    snapshot.forEach(WorldEvent::stopEvent);
                    return;
                }

                if (args.size() == 2) {
                    String idPath = args.get(1);
                    WorldEvent we = WorldEvent.getByPath(idPath).orElse(null);
                    if (we == null) {
                        sender.sendMessage("WorldEvent " + idPath + " not found!");
                        return;
                    }
                    if (!WorldEvent.getActiveEvents().contains(we)) {
                        sender.sendMessage("WorldEvent " + idPath + " is not active!");
                        return;
                    }
                    we.stopEvent();
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

        String first = args.getFirst().toLowerCase(Locale.ROOT);

        Set<WorldEvent> activeSet = new HashSet<>(WorldEvent.getActiveEvents());

        List<String> activeIds = WorldEvent.getActiveEvents().stream()
                .map(WorldEvent::getKey).map(NamespacedKey::getKey)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();

        List<String> inactiveIds = WorldEvent.getAllEvents().entrySet().stream()
                .filter(e -> !activeSet.contains(e.getValue()))
                .map(e -> e.getKey().getKey())
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();

        if (args.size() == 1) {
            if ("start".equals(first)) return inactiveIds;
            if ("stop".equals(first)) return activeIds;

            return Stream.of("start", "stop")
                    .filter(s -> s.startsWith(first))
                    .toList();
        }

        return Collections.emptyList();
    }
}
