package com.projectkorra.rpg.modules.leveling.service;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.leveling.models.RpgPlayer;
import com.projectkorra.rpg.modules.leveling.storage.registries.models.RpgPlayerRegistry;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class LevelingService {
    private final ProjectKorraRPG plugin;
    private final RpgPlayerRegistry rpgPlayerRegistry;

    public LevelingService(final ProjectKorraRPG plugin, final RpgPlayerRegistry rpgPlayerRegistry) {
        this.plugin = plugin;
        this.rpgPlayerRegistry = rpgPlayerRegistry;
    }

    public void levelUp(final UUID target) {
        RpgPlayer player = rpgPlayerRegistry.getIfPresent(target);

    }

    public void grantXp(final UUID target, final  double amount) {

    }

    public void revokeXp(final UUID target, final double amount) {

    }

    public void grantLevel(final UUID target, final int amount) {

    }

    public void revokeLevel(final UUID target, final int amount) {

    }

    public CompletableFuture<RpgPlayer> getPlayer(final UUID target) {
        RpgPlayer get = rpgPlayerRegistry.getIfPresent(target);
        if (get != null) return CompletableFuture.completedFuture(get);

        return rpgPlayerRegistry.getOrLoad(target);
    }
}
