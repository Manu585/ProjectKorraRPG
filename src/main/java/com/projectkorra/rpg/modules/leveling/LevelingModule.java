package com.projectkorra.rpg.modules.leveling;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.Module;
import com.projectkorra.rpg.modules.leveling.commands.LevelCommand;
import com.projectkorra.rpg.modules.leveling.service.LevelingService;
import com.projectkorra.rpg.modules.leveling.storage.registries.models.RpgPlayerRegistry;

public class LevelingModule extends Module {
    private LevelingService levelingService;
    private RpgPlayerRegistry rpgPlayerRegistry;

    public LevelingModule(ProjectKorraRPG plugin) {
        super(plugin, "Leveling");
    }

    @Override
    public void enable() {
        this.getPlugin().getLogger().info("Enabling " + getName() + " module...");

        this.rpgPlayerRegistry = new RpgPlayerRegistry();
        this.levelingService = new LevelingService(this.getPlugin(), rpgPlayerRegistry);

        new LevelCommand();

        this.getPlugin().getLogger().info(getName() + " module enabled successfully!");
    }

    @Override
    public void disable() {
        this.getPlugin().getLogger().info("Disabling " + getName() + " module...");

        this.getPlugin().getLogger().info(getName() + " module disabled successfully!");
    }

    public LevelingService getLevelingService() {
        return levelingService;
    }

    public RpgPlayerRegistry getRpgPlayerRegistry() {
        return rpgPlayerRegistry;
    }
}
