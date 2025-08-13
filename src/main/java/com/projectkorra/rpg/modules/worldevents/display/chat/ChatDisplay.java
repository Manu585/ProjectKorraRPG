package com.projectkorra.rpg.modules.worldevents.display.chat;

import com.projectkorra.rpg.modules.worldevents.models.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.display.WorldEventDisplay;
import com.projectkorra.rpg.util.ChatUtil;
import org.bukkit.entity.Player;

public class ChatDisplay implements WorldEventDisplay {
	private final String startMessage;
	private final String stopMessage;

	public ChatDisplay(String startMessage, String stopMessage) {
		this.startMessage = startMessage;
		this.stopMessage = stopMessage;
	}

	@Override
	public void startDisplay(WorldEvent event) {
        if (startMessage == null || startMessage.isBlank()) return;
        for (Player player : event.getWorld().getPlayers()) {
            ChatUtil.sendBrandingMessage(player, startMessage);
        }
	}

	@Override
	public void stopDisplay(WorldEvent event) {
        if (stopMessage == null || stopMessage.isBlank()) return;
        for (Player player : event.getWorld().getPlayers()) {
            ChatUtil.sendBrandingMessage(player, stopMessage);
        }
	}
}
