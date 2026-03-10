package com.projectkorra.rpg.configuration;

import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class Config {

    private final Plugin plugin;
    private final File file;
    private final FileConfiguration config;

    public Config(Plugin plugin, File file) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder() + File.separator + file);
        this.config = YamlConfiguration.loadConfiguration(this.file);
        reload();
    }

    public void create() {
        if (!file.getParentFile().exists()) {
            try {
                if (file.getParentFile().mkdir()) {
                    plugin.getLogger().info("Generating new directory for " + file.getName() + "!");
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to generate directory!" + e.getMessage());
            }
        }

        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    plugin.getLogger().info("Generating new " + file.getName() + "!");
                }
            } catch (Exception e) {
                plugin.getLogger().info("Failed to generate " + file.getName() + "!" + e.getMessage());
            }
        }
    }

    public FileConfiguration get() {
        return config;
    }

    public void reload() {
        create();
        try {
            config.load(file);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to reload " + file.getName() + "!" + e.getMessage());
        }
    }

    public void save() {
        try {
            config.options().copyDefaults(true);
            config.options().parseComments(true);
            config.save(file);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to save " + file.getName() + "!" + e.getMessage());
        }
    }
}
