package com.projectkorra.rpg.modules.leveling.models;

import java.util.UUID;

public record RpgPlayer(UUID uuid, double xp, int level) {}