package com.projectkorra.rpg.modules.worldevents.display.chat;

import com.projectkorra.rpg.modules.worldevents.display.IChatDisplay;
import com.projectkorra.rpg.util.ChatUtil;
import org.bukkit.entity.Player;

import java.util.Collection;

public class ChatDisplay implements IChatDisplay {
	private final String startMessage;
	private final String stopMessage;

	public ChatDisplay(String startMessage, String stopMessage) {
		this.startMessage = startMessage;
		this.stopMessage = stopMessage;
	}

    @Override
    public void sendStartMessage(Player player) {
        if (startMessage == null || startMessage.isBlank()) return;
        ChatUtil.sendBrandingMessage(player, startMessage);
    }

    @Override
    public void sendStopMessage(Player player) {
        if (stopMessage == null || stopMessage.isBlank()) return;
        ChatUtil.sendBrandingMessage(player, stopMessage);
    }

    @Override
    public void sendStartMessage(Collection<Player> players) {
        if (startMessage == null || startMessage.isBlank()) return;
        players.forEach(player -> ChatUtil.sendBrandingMessage(player, startMessage));
    }

    @Override
    public void sendStopMessage(Collection<Player> players) {
        if (stopMessage == null || stopMessage.isBlank()) return;
        players.forEach(player -> ChatUtil.sendBrandingMessage(player, stopMessage));
    }

    @Override
    public void sendCurrentlyActiveMessage(Player player) {

    }
}
