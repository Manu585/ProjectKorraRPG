package com.projectkorra.rpg.modules.worldevents.commands;

import com.projectkorra.rpg.commands.RPGCommand;
import com.projectkorra.rpg.modules.worldevents.gui.WorldEventAttributionGui;
import com.projectkorra.rpg.modules.worldevents.models.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.service.WorldEventService;
import com.projectkorra.rpg.modules.worldevents.storage.ActiveWorldEventIndex;
import com.projectkorra.rpg.modules.worldevents.storage.WorldEventRegistry;
import com.projectkorra.rpg.util.ChatUtil;
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
                    ChatUtil.sendBrandingMessage(sender, "&cWorldEvent '" + idPath + "' not found.");
                    return;
                }
                if (activeEventsIndex.activeWorldEvents().contains(worldEvent)) {
                    ChatUtil.sendBrandingMessage(sender, "&cWorldEvent '" + idPath + "' is already active!");
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
                    ChatUtil.sendBrandingMessage(sender, "&aStarted WorldEvent '" + worldEvent.getKey().getKey() + "'.");
                } else {
                    ChatUtil.sendBrandingMessage(sender, "&cCould not start WorldEvent '" + idPath + "'. Check logs for details.");
                }
            }

            case "stop" -> {
                // STOP ALL
                if (args.size() == 1) {
                    if (activeEventsIndex.activeWorldEvents().isEmpty()) {
                        ChatUtil.sendBrandingMessage(sender, "&cNo active WorldEvents to stop.");
                        return;
                    }
                    service.stopAll();
                    ChatUtil.sendBrandingMessage(sender, "&aStopped all active WorldEvents.");
                    return;
                }

                // STOP SPECIFIC
                if (args.size() == 2) {
                    final String idPath = args.get(1);
                    final WorldEvent we = registry.findByPath(idPath).orElse(null);
                    if (we == null) {
                        ChatUtil.sendBrandingMessage(sender, "&cWorldEvent '" + idPath + "' not found.");
                        return;
                    }
                    if (!activeEventsIndex.activeWorldEvents().contains(we)) {
                        ChatUtil.sendBrandingMessage(sender, "&cWorldEvent '" + idPath + "' is not active.");
                        return;
                    }
                    if (service.stop(we)) {
                        ChatUtil.sendBrandingMessage(sender, "&aStopped world event '" + we.getKey().getKey() + "'.");
                    } else {
                        ChatUtil.sendBrandingMessage(sender, "&cCould not stop world event '" + idPath + "'. Check logs for details.");
                    }
                } else {
                    help(sender, false);
                }
            }

            case "edit" -> {
                if (args.size() == 1) {
                    ChatUtil.sendBrandingMessage(sender, "&cSpecify WorldEvent to edit!");
                    return;
                }

                if (args.size() == 2) {
                    final String idPath = args.get(1);
                    final WorldEvent worldEvent = registry.findByPath(idPath).orElse(null);
                    if (worldEvent == null) {
                        ChatUtil.sendBrandingMessage(sender, "&cWorldEvent '" + idPath + "' not found.");
                        return;
                    }
                    if (!isPlayer(sender)) {
                        ChatUtil.sendBrandingMessage(sender, "&cOnly players can edit WorldEvents in game. Console has to do via. configuration files.");
                        return;
                    }

                    new WorldEventAttributionGui().open((Player) sender);
                }
            }

            default -> help(sender, true);
        }
    }

    @Override
    protected List<String> getTabCompletion(CommandSender sender, List<String> args) {
        if (args.isEmpty()) {
            return List.of("start", "stop", "edit");
        }

        if (args.size() == 1) {
            final String first = args.getFirst().toLowerCase(Locale.ROOT);
            final String partialId = (args.size() > 1 ? args.get(1) : "").toLowerCase(Locale.ROOT);

            switch (first) {
                case "start", "edit" -> {
                    return getAllWorldEvents(partialId);
                }

                case "stop" -> {
                    return activeEventsIndex.activeWorldEvents().stream().parallel()
                            .map(WorldEvent::getKey)
                            .map(NamespacedKey::getKey)
                            .filter(id -> id.toLowerCase(Locale.ROOT).startsWith(partialId))
                            .sorted(String.CASE_INSENSITIVE_ORDER)
                            .collect(Collectors.toList());
                }
            }
        }

        return Collections.emptyList();
    }

    private List<String> getAllWorldEvents(String partialId) {
        final Set<WorldEvent> active = activeEventsIndex.activeWorldEvents();
        return registry.getAll().entrySet().stream().parallel()
                .filter(e -> !active.contains(e.getValue()))
                .map(e -> e.getKey().getKey())
                .filter(id -> id.toLowerCase(Locale.ROOT).startsWith(partialId))
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
    }
}
