package com.projectkorra.rpg.gui;

import org.bukkit.inventory.ItemStack;

public record MenuItem(ItemStack item, String name, Runnable runnable) {}
