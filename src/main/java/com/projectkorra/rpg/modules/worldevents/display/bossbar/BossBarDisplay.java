package com.projectkorra.rpg.modules.worldevents.display.bossbar;

import com.projectkorra.projectkorra.util.ChatUtil;
import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.display.WorldEventDisplay;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class BossBarDisplay implements WorldEventDisplay {

	private final BarColor barColor;
	private final BarStyle barStyle;
	private final boolean smooth;
	private final String title;

	private BossBar bossBar;

	public BossBarDisplay(String title, BarColor barColor, BarStyle barStyle, boolean smooth) {
		this.title = title;
		this.barColor = barColor;
		this.barStyle = barStyle;
		this.smooth = smooth;
	}

	@Override
	public void startDisplay(WorldEvent event) {
		this.bossBar = Bukkit.createBossBar(ChatUtil.color(title), barColor, barStyle);

		for (Player player : event.getAffectedPlayers()) {
			bossBar.addPlayer(player);
		}
	}

	@Override
	public void updateDisplay(WorldEvent event, double progress) {
		if (bossBar != null) {
			bossBar.setProgress(Math.max(0.0, Math.min(1.0, progress)));
		}
	}

	@Override
	public void stopDisplay(WorldEvent event) {
		if (bossBar != null) {
			bossBar.removeAll();
			bossBar = null;
		}
	}

	public boolean isSmooth() {
		return smooth;
	}

}
