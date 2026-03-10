package com.projectkorra.rpg.permissions;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;

/**
 * Manages player permissions using Bukkit's {@link PermissionAttachment} system
 */
public class PermissionService implements Listener {

    private final Plugin plugin;

    private final ConcurrentHashMap<UUID, PermissionAttachment> attachments = new ConcurrentHashMap<>();

    public PermissionService(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Adds a permission to a player.
     */
    public void addPermission(Player player, String permission) {
        if (player == null || !player.isOnline() || permission == null || permission.isEmpty()) return;

        PermissionAttachment attachment = getOrCreateAttachment(player);
        attachment.setPermission(permission, true);
        player.recalculatePermissions();
    }

    /**
     * Removes a permission from a player.
     */
    public void removePermission(Player player, String permission) {
        if (player == null || !player.isOnline() || permission == null || permission.isEmpty()) return;

        PermissionAttachment attachment = attachments.get(player.getUniqueId());
        if (attachment == null) return;

        attachment.unsetPermission(permission);
        player.recalculatePermissions();
    }

    /**
     * Clears all RPG-managed permissions for a player.
     */
    public void clearPermissions(Player player) {
        PermissionAttachment attachment = attachments.remove(player.getUniqueId());
        if (attachment != null && player.isOnline()) {
            player.removeAttachment(attachment);
            player.recalculatePermissions();
        }
    }

    private PermissionAttachment getOrCreateAttachment(Player player) {
        return attachments.computeIfAbsent(player.getUniqueId(), uuid -> player.addAttachment(plugin));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        attachments.remove(event.getPlayer().getUniqueId());
    }

}
