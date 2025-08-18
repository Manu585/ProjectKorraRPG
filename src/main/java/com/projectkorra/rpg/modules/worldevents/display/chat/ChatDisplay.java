package com.projectkorra.rpg.modules.worldevents.display.chat;

import com.projectkorra.rpg.modules.worldevents.models.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.display.WorldEventDisplay;
import com.projectkorra.rpg.util.ChatUtil;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ChatDisplay implements WorldEventDisplay {
	private final String startMessage;
	private final String stopMessage;

	public ChatDisplay(String startMessage, String stopMessage) {
		this.startMessage = startMessage;
		this.stopMessage = stopMessage;
	}

	@Override
	public void startDisplay(WorldEvent event, World world) {
        if (startMessage == null || startMessage.isBlank() || world == null) return;
        for (Player player : world.getPlayers()) {
            ChatUtil.sendBrandingMessage(player, startMessage);
        }
	}

	@Override
	public void stopDisplay(WorldEvent event, World world) {
        if (stopMessage == null || stopMessage.isBlank() || world == null) return;
        for (Player player : world.getPlayers()) {
            ChatUtil.sendBrandingMessage(player, stopMessage);
        }
	}
}
