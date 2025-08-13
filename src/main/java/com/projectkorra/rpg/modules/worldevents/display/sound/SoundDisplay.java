package com.projectkorra.rpg.modules.worldevents.display.sound;

import com.projectkorra.rpg.modules.worldevents.models.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.display.WorldEventDisplay;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class SoundDisplay implements WorldEventDisplay {
    private final @Nullable Sound start;
    private final float startVolume;
    private final float startPitch;

    private final @Nullable Sound stop;
    private final float stopVolume;
    private final float stopPitch;

    public SoundDisplay(@Nullable Sound start, float startVolume, float startPitch,
                        @Nullable Sound stop, float stopVolume, float stopPitch) {
        this.start = start;
        this.startVolume = startVolume;
        this.startPitch = startPitch;

        this.stop = stop;
        this.stopVolume = stopVolume;
        this.stopPitch = stopPitch;
    }

    @Override
    public void startDisplay(WorldEvent event) {
        if (start == null) return;
        for (Player player : event.getWorld().getPlayers()) {
            player.playSound(player.getLocation(), start, SoundCategory.AMBIENT, startVolume, startPitch);
        }
    }

    @Override
    public void stopDisplay(WorldEvent event) {
        if (stop == null) return;
        for (Player player : event.getWorld().getPlayers()) {
            player.playSound(player.getLocation(), stop, SoundCategory.AMBIENT, stopVolume, stopPitch);
        }
    }
}
