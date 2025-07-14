package com.projectkorra.rpg.modules.leveling;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.Module;
import com.projectkorra.rpg.modules.leveling.commands.LevelCommand;
import com.projectkorra.rpg.modules.leveling.gui.GuiFactory;

public class LevelingModule extends Module {
    public LevelingModule(ProjectKorraRPG plugin) {
        super(plugin, "Leveling");
    }

    @Override
    public void enable() {
        GuiFactory factory = new GuiFactory(this.getPlugin().getInventoryService());
        new LevelCommand(factory);
    }

    @Override
    public void disable() {

    }
}
