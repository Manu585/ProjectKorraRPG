package com.projectkorra.rpg.modules.worldevents.models;

import com.projectkorra.rpg.modules.worldevents.display.WorldEventDisplay;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class WorldEvent implements Keyed {
    private final NamespacedKey key;
    private final String title;
    private final long duration;
    private final World world;
    private final List<WorldEventDisplay> displayMethods;
    private final List<World> disabledWorlds;

    private final ScheduleSpecifications scheduleSpecifications;
    private final AttributeRules attributeRules;

    public WorldEvent(NamespacedKey key, String title, long duration, World world, List<WorldEventDisplay> displayMethods, List<World> disabledWorlds, ScheduleSpecifications scheduleSpecifications, AttributeRules attributeRules) {
        this.key = key;
        this.title = title;
        this.duration = duration;
        this.world = world;
        this.displayMethods = displayMethods == null ? List.of() : List.copyOf(displayMethods);
        this.disabledWorlds = disabledWorlds == null ? List.of() : List.copyOf(disabledWorlds);
        this.scheduleSpecifications = scheduleSpecifications;
        this.attributeRules = attributeRules;
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return this.key;
    }

    public String getTitle() {
        return title;
    }

    public long getDuration() {
        return duration;
    }

    public World getWorld() {
        return world;
    }

    public List<WorldEventDisplay> getDisplayMethods() {
        return displayMethods;
    }

    public List<World> getDisabledWorlds() {
        return disabledWorlds;
    }

    public ScheduleSpecifications getScheduleSpecifications() {
        return scheduleSpecifications;
    }

    public AttributeRules getAttributeRules() {
        return attributeRules;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorldEvent other)) return false;
        return key.equals(other.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
