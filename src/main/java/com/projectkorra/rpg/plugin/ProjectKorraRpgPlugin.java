package com.projectkorra.rpg.plugin;

import com.projectkorra.rpg.RPGListener;
import com.projectkorra.rpg.commands.HelpCommand;
import com.projectkorra.rpg.commands.RPGCommand;
import com.projectkorra.rpg.commands.RPGCommandBase;
import com.projectkorra.rpg.configuration.ConfigManager;
import com.projectkorra.rpg.modules.ModuleManager;
import com.projectkorra.rpg.modules.randomavatar.AvatarCycleModule;
import com.projectkorra.rpg.permissions.PermissionService;
import com.projectkorra.rpg.storage.TableCreator;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ProjectKorraRpgPlugin implements PluginBootstrap {

  private final JavaPlugin plugin;
  private final ModuleManager moduleManager;
  private final PermissionService permissionService;

  public ProjectKorraRpgPlugin(JavaPlugin plugin) {
    this.plugin = plugin;
    this.moduleManager = new ModuleManager(plugin);
    this.permissionService = new PermissionService(plugin);

    new ConfigManager(plugin);
    new TableCreator();
  }

  @Override
  public void onLoad() {}

  @Override
  public void onEnable() {
    Bukkit.getServer().getPluginManager().registerEvents(permissionService, plugin);
    Bukkit.getServer().getPluginManager().registerEvents(new RPGListener(plugin, moduleManager), plugin);

    RPGCommand.instances.clear();

    new RPGCommandBase();
    new HelpCommand();

    wireModuleDependencies();

    moduleManager.enableModules();
  }

  @Override
  public void onDisable() {
    moduleManager.disableModules();
  }

  @Override
  public ModuleManager getModuleManager() {
    return moduleManager;
  }

  private void wireModuleDependencies() {
    moduleManager.getElementAssignmentsModule().setPermissionService(permissionService);

    moduleManager.getElementAssignmentsModule().setAvatarManagerSupplier(() -> {
      AvatarCycleModule avatarModule = moduleManager.getAvatarCycleModule();
      return avatarModule.isEnabled() ? avatarModule.getAvatarManager() : null;
    });
  }

}
