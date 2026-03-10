package com.projectkorra.rpg.modules;

import com.projectkorra.rpg.configuration.ConfigManager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public abstract class Module {

    protected final Plugin plugin;
    protected final String name;
    private final String configPath;

    public Module(Plugin plugin, String name) {
        this.plugin = plugin;
        this.name = name;
        this.configPath = "Modules." + name + ".Enabled";
    }

    public abstract void enable();

    public abstract void disable();

    /**
     * Returns whether this module is enabled in the config.
     */
    public boolean isEnabled() {
        return ConfigManager.getDefaultFileConfig().getBoolean(configPath, false);
    }

    public void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public String getName() {
        return this.name;
    }

}
