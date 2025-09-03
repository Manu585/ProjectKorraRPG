package com.projectkorra.rpg.modules.leveling;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.Module;
import com.projectkorra.rpg.modules.leveling.commands.LevelCommand;
import com.projectkorra.rpg.modules.leveling.service.LevelingService;

public class LevelingModule extends Module {
    private LevelingService levelingService;

    public LevelingModule(ProjectKorraRPG plugin) {
        super(plugin, "Leveling");
    }

    @Override
    public void enable() {
        this.getPlugin().getLogger().info("Enabling " + getName() + " module...");


        this.levelingService = new LevelingService(this.getPlugin());

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
}
