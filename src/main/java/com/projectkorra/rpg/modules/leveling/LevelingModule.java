package com.projectkorra.rpg.modules.leveling;

import com.projectkorra.rpg.modules.Module;
import com.projectkorra.rpg.modules.leveling.commands.LevelCommand;
import com.projectkorra.rpg.modules.leveling.gui.GuiManager;
import org.bukkit.plugin.Plugin;

public class LevelingModule extends Module {

    private GuiManager guiManager;

    public LevelingModule(Plugin plugin) {
        super(plugin, "Leveling");
    }

    @Override
    public void enable() {
        this.guiManager = new GuiManager(plugin);
        this.guiManager.init();

        new LevelCommand();
    }

    @Override
    public void disable() {
        this.guiManager = null;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

}
