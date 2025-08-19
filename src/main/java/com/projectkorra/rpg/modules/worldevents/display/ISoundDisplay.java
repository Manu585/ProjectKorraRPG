package com.projectkorra.rpg.modules.worldevents.display;

import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;

public interface ISoundDisplay {
    void playStartSound(Collection<Player> players);
    void playStopSound(Collection<Player> players);
}
