package com.projectkorra.rpg.modules.elementassignments.listeners;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.event.BendingPlayerLoadEvent;
import com.projectkorra.rpg.modules.elementassignments.manager.AssignmentManager;
import com.projectkorra.rpg.modules.randomavatar.manager.AvatarManager;
import java.util.function.Supplier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class AssignmentListener implements Listener {

    private final AssignmentManager assignmentManager;
    private final Supplier<AvatarManager> avatarManagerSupplier;

    public AssignmentListener(AssignmentManager assignmentManager, Supplier<AvatarManager> avatarManagerSupplier) {
        this.assignmentManager = assignmentManager;
        this.avatarManagerSupplier = avatarManagerSupplier;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(BendingPlayerLoadEvent event) {
        if (event.getBendingPlayer().isOnline()) {
            BendingPlayer bPlayer = (BendingPlayer) event.getBendingPlayer();
            if (bPlayer.getElements().isEmpty() && !bPlayer.isPermaRemoved()) {
                assignmentManager.assignRandomGroup(bPlayer, false);
            }
        }
    }

    @EventHandler
    public void onBendingPlayerDeath(final PlayerDeathEvent event) {
        // Skip reassignment if the player is the current RPG Avatar
        AvatarManager avatarManager = avatarManagerSupplier != null ? avatarManagerSupplier.get() : null;
        if (avatarManager != null && avatarManager.isCurrentRPGAvatar(event.getEntity().getUniqueId())) {
            return;
        }

        assignmentManager.assignRandomGroup(BendingPlayer.getBendingPlayer(event.getEntity()), true);
    }

}
