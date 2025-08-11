package com.projectkorra.rpg.modules.worldevents.util.display.bossbar;

import com.projectkorra.projectkorra.util.ChatUtil;
import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.util.display.ITickingDisplay;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BossBarDisplay implements ITickingDisplay {
    private final NamespacedKey key;
    private final String title;
    private final BarColor barColor;
    private final BarStyle barStyle;
    private final boolean smooth;

    private KeyedBossBar bossBar;

	/**
	 *
	 * @param barColor Color of BossBar
	 * @param barStyle Style of BossBar
	 * @param smooth   <code>true</code> Refresh every tick <code>else</code> every second
	 */
	public BossBarDisplay(NamespacedKey key, String title, BarColor barColor, BarStyle barStyle, boolean smooth) {
        this.key = key;
        this.title = title;
        this.barColor = barColor;
        this.barStyle = barStyle;
        this.smooth = smooth;
	}

    @Override
    public long tickPeriod() {
        return smooth ? 1L : 20L;
    }

	@Override
	public void startDisplay(WorldEvent event) {
        KeyedBossBar existing = Bukkit.getBossBar(key);
        this.bossBar = (existing != null) ? existing : Bukkit.createBossBar(key, ChatUtil.color(title), barColor, barStyle);

        bossBar.setTitle(ChatUtil.color(title));
        bossBar.setColor(barColor);
        bossBar.setStyle(barStyle);
        bossBar.setProgress(1.0);

        for (UUID uuid : event.getAffectedPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                bossBar.addPlayer(player);
            }
        }
	}

	@Override
	public void updateDisplay(WorldEvent event, double progress) {
        if (bossBar == null) return;
        bossBar.setProgress(Math.max(0.0, Math.min(1.0, progress)));
	}

	@Override
	public void stopDisplay(WorldEvent event) {
        if (bossBar == null) return;

        for (UUID uuid : event.getAffectedPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
                if (player != null && player.isOnline()) {
                    bossBar.removePlayer(player);
                }
            }

        Bukkit.removeBossBar(key);
        bossBar = null;
	}
}
