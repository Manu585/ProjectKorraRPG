package com.projectkorra.rpg.modules.worldevents.builder;

import com.projectkorra.rpg.modules.worldevents.display.WorldEventDisplay;
import com.projectkorra.rpg.modules.worldevents.models.AttributeRules;
import com.projectkorra.rpg.modules.worldevents.models.ScheduleSpecifications;
import com.projectkorra.rpg.modules.worldevents.models.WorldEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.World;

import java.util.*;
import java.util.function.Consumer;

public class WorldEventBuilder {
    private NamespacedKey key;
    private String title;
    private Long duration;
    private World world;
    private final List<WorldEventDisplay> displays = new ArrayList<>();
    private final List<World> disabledWorlds = new ArrayList<>();
    private ScheduleSpecifications schedule;
    private AttributeRules attributeRules;

    private WorldEventBuilder() {}

    public static WorldEventBuilder create() {
        return new WorldEventBuilder();
    }

    public WorldEventBuilder key(final NamespacedKey key) {
        this.key = key;
        return this;
    }

    public WorldEventBuilder title(final String title) {
        this.title = title;
        return this;
    }

    public WorldEventBuilder duration(final long durationMillis) {
        this.duration = durationMillis;
        return this;
    }

    public WorldEventBuilder world(final World world) {
        this.world = world;
        return this;
    }

    public WorldEventBuilder addDisplay(final WorldEventDisplay display) {
        if (display != null) this.displays.add(display);
        return this;
    }

    public WorldEventBuilder displays(final Collection<? extends WorldEventDisplay> displays) {
        if (displays != null) {
            for (WorldEventDisplay display : displays) {
                if (display  != null) this.displays.add(display);
            }
        }
        return this;
    }

    public WorldEventBuilder disabledWorlds(final Collection<World> worlds) {
        if (worlds != null) {
            for (World world : worlds) {
                if (world != null) this.disabledWorlds.add(world);
            }
        }
        return this;
    }

    public WorldEventBuilder schedule(final ScheduleSpecifications schedule) {
        this.schedule = schedule;
        return this;
    }

    public WorldEventBuilder attributes(final AttributeRules attributeRules) {
        this.attributeRules = attributeRules;
        return this;
    }

    public Optional<WorldEvent> tryBuild(Consumer<String> onError) {
        List<String> errors = new ArrayList<>();

        if (key == null || key.getKey().isBlank()) errors.add("key is missing");
        if (title == null || title.isBlank()) errors.add("Title is missing / blank");
        if (duration == null || duration <= 0) errors.add("duration is missing / <= 0");
        if (world == null) errors.add("world is missing");

        if (!errors.isEmpty()) {
            if (onError != null) {
                for (String error : errors) onError.accept(error);
            }
            return Optional.empty();
        }

        return Optional.of(new WorldEvent(
                        key,
                        title,
                        duration,
                        world,
                        List.copyOf(displays),
                        List.copyOf(disabledWorlds),
                        schedule,
                        attributeRules
        ));
    }

    public WorldEvent buildOrThrow() {
        return tryBuild(msg -> {}).orElseThrow(() -> new IllegalStateException("WorldEventBuilder has invalid state!"));
    }
}
