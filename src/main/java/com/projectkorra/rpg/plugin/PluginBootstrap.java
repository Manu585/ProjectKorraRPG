package com.projectkorra.rpg.plugin;

import com.projectkorra.rpg.modules.ModuleManager;

public interface PluginBootstrap {

  void onLoad();

  void onEnable();

  void onDisable();

  ModuleManager getModuleManager();

}
