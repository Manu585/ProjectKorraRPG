package com.projectkorra.rpg.modules.leveling.storage.registries.models;

import com.projectkorra.rpg.modules.leveling.models.RpgPlayer;
import com.projectkorra.rpg.modules.leveling.storage.registries.LoadingCache;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

// TODO: Add DAO for DB retrieval
public class RpgPlayerRegistry extends LoadingCache<UUID, RpgPlayer> {

    @Override
    protected CompletableFuture<RpgPlayer> load(UUID key) {
        return null;
    }
}
