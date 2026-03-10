package com.projectkorra.rpg.modules.worldevents.display.chat;

import com.projectkorra.rpg.modules.worldevents.WorldEvent;
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
		for (Player player : event.getAffectedPlayers()) {
			ChatUtil.sendBrandingMessage(player, startMessage);
		}
	}

	@Override
	public void stopDisplay(WorldEvent event) {
		for (Player player : event.getAffectedPlayers()) {
			ChatUtil.sendBrandingMessage(player, stopMessage);
		}
	}

}
