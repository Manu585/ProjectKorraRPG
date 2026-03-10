package com.projectkorra.rpg.modules.worldevents.util;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

public class DisplayHelper {

	public static BarColor convertStringToBarColor(String colorStr) {
		if (colorStr == null) return BarColor.RED;
		try {
			return BarColor.valueOf(colorStr.toUpperCase());
		} catch (IllegalArgumentException e) {
			return BarColor.RED;
		}
	}

	public static BarStyle convertStringToBarStyle(String styleStr) {
		if (styleStr == null) return BarStyle.SOLID;
		try {
			return BarStyle.valueOf(styleStr.toUpperCase());
		} catch (IllegalArgumentException e) {
			return BarStyle.SOLID;
		}
	}

}
