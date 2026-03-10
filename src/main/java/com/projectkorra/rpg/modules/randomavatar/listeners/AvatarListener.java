package com.projectkorra.rpg.modules.randomavatar.listeners;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.rpg.modules.randomavatar.manager.AvatarManager;
import com.projectkorra.rpg.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class AvatarListener implements Listener {

    private final AvatarManager avatarManager;

    public AvatarListener(AvatarManager avatarManager) {
        this.avatarManager = avatarManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(event.getPlayer().getUniqueId());
        avatarManager.getRecentPlayers().remove(player);
        avatarManager.getRecentPlayers().add(player);
    }

    @EventHandler
    public void onBendingPlayerDeath(final PlayerDeathEvent event) {
        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(event.getEntity());
        if (bPlayer == null) return;

        if (avatarManager.isCurrentRPGAvatar(bPlayer.getUUID())) {
            if (avatarManager.handleAvatarDeath(bPlayer)) {
                ChatUtil.sendBrandingMessage(bPlayer.getPlayer(), Element.AVATAR.getColor() + "You have lost Avatar");
            }
        }
    }

}
