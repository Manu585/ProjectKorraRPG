package com.projectkorra.rpg.modules.elementassignments;

import com.projectkorra.rpg.modules.Module;
import com.projectkorra.rpg.modules.elementassignments.listeners.AssignmentListener;
import com.projectkorra.rpg.modules.elementassignments.manager.AssignmentManager;
import com.projectkorra.rpg.modules.randomavatar.manager.AvatarManager;
import com.projectkorra.rpg.permissions.PermissionService;
import java.util.function.Supplier;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

public class ElementAssignModule extends Module {

	// Manu - Perhaps find a better solution
	private Supplier<AvatarManager> avatarManagerSupplier;

	private AssignmentManager assignmentManager;
	private AssignmentListener assignmentListener;
	private PermissionService permissionService;

	public ElementAssignModule(Plugin plugin) {
		super(plugin, "ElementAssignments");
	}

	public void setAvatarManagerSupplier(Supplier<AvatarManager> avatarManagerSupplier) {
		this.avatarManagerSupplier = avatarManagerSupplier;
	}

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	@Override
	public void enable() {
		this.assignmentManager = new AssignmentManager(plugin, permissionService);
		this.assignmentListener = new AssignmentListener(assignmentManager, avatarManagerSupplier);

		registerListeners(this.assignmentListener);
	}

	@Override
	public void disable() {
		this.assignmentManager = null;

		if (this.assignmentListener != null) {
			HandlerList.unregisterAll(this.assignmentListener);
			this.assignmentListener = null;
		}
	}

	public AssignmentManager getAssignmentManager() {
		return assignmentManager;
	}

}
