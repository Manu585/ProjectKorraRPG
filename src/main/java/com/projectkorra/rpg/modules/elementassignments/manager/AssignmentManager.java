package com.projectkorra.rpg.modules.elementassignments.manager;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.rpg.configuration.ConfigManager;
import com.projectkorra.rpg.modules.elementassignments.model.AssignmentGroup;
import com.projectkorra.rpg.permissions.PermissionService;
import com.projectkorra.rpg.util.ChatUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class AssignmentManager {

    private final Plugin plugin;
    private final PermissionService permissionService;

    private double chance = 0.2;
    private double totalWeight = 0;
    private boolean changeOnDeathEnabled = true;
    private boolean changeOnDeathBypass = false;
    private String changeOnDeathPermission;

    private final Set<String> permissionGroups = new HashSet<>();
    private final List<AssignmentGroup> groups = new ArrayList<>();

    public AssignmentManager(Plugin plugin, PermissionService permissionService) {
        this.plugin = plugin;
        this.permissionService = permissionService;
        loadConfiguration();
    }

    private void loadConfiguration() {
        var config = ConfigManager.defaultConfig.get();

        changeOnDeathEnabled = config.getBoolean("Modules.ElementAssignments.ChangeOnDeath.Enabled");
        changeOnDeathBypass = config.getBoolean("Modules.ElementAssignments.ChangeOnDeath.Bypass");
        changeOnDeathPermission = config.getString("Modules.ElementAssignments.ChangeOnDeath.Permission");
        chance = config.getDouble("Modules.ElementAssignments.ChangeOnDeath.Chance");

        ConfigurationSection groupsSection = config.getConfigurationSection("Modules.ElementAssignments.Groups");
        if (groupsSection == null) return;

        for (String groupKey : groupsSection.getKeys(false)) {
            boolean groupEnabled = config.getBoolean("Modules.ElementAssignments.Groups." + groupKey + ".Enabled");
            if (!groupEnabled) continue;

            double weight = config.getDouble("Modules.ElementAssignments.Groups." + groupKey + ".Weight");
            List<String> elements = config.getStringList("Modules.ElementAssignments.Groups." + groupKey + ".Elements");
            String prefix = config.getString("Modules.ElementAssignments.Groups." + groupKey + ".Prefix");
            List<String> commandsToRun = config.getStringList("Modules.ElementAssignments.Groups." + groupKey + ".Commands");
            String permissionGroup = config.getString("Modules.ElementAssignments.Groups." + groupKey + ".PermissionGroup");

            permissionGroups.add(permissionGroup);
            totalWeight += weight;

            groups.add(new AssignmentGroup(groupKey, elements, weight, true, prefix, commandsToRun, permissionGroup));
            plugin.getLogger().info("ElementAssignments: " + groupKey + " is enabled with weight: " + weight);
        }
    }

    public AssignmentGroup getRandomGroup() {
        if (groups.isEmpty() || totalWeight <= 0) {
            return null;
        }
        double randomValue = Math.random() * totalWeight;
        double cumulative = 0;

        for (AssignmentGroup group : groups) {
            cumulative += group.getWeight();
            if (randomValue < cumulative) {
                return group;
            }
        }
        return null;
    }

    public void assignGroup(AssignmentGroup assignmentGroup, BendingPlayer bendingPlayer) {
        if (bendingPlayer == null) return;

        bendingPlayer.getElements().clear();

        for (Element element : assignmentGroup.getElements()) {
            if (element instanceof Element.SubElement) {
                bendingPlayer.addSubElement((Element.SubElement) element);
            } else {
                bendingPlayer.addElement(element);
            }
            ChatUtil.sendBrandingMessage(bendingPlayer.getPlayer(), element.getColor() + "You are now a " + element.getName() + "bender.");
        }

        for (String command : assignmentGroup.getCommandsToRun()) {
            String formattedCommand = command.replace("%player%", bendingPlayer.getName());
            if (bendingPlayer.isOnline()) {
                plugin.getServer().dispatchCommand(ProjectKorra.plugin.getServer().getConsoleSender(), formattedCommand);
            }
        }

        Player player = bendingPlayer.getPlayer();

        if (!assignmentGroup.getPermissionGroup().isEmpty()) {
            for (String group : permissionGroups) {
                if (!group.equalsIgnoreCase(assignmentGroup.getPermissionGroup()) && player.hasPermission("group." + group)) {
                    permissionService.removePermission(player, "group." + group);
                }
            }
            permissionService.addPermission(player, "group." + assignmentGroup.getPermissionGroup());
        }

        if (bendingPlayer.isOnline()) {
            plugin.getLogger().info(player.getName() + " has been assigned the " + assignmentGroup.getName() + " group.");
        }
    }

    public void assignRandomGroup(BendingPlayer bp, boolean onDeath) {
        if (onDeath) {
            if (!changeOnDeathEnabled) return;
            if (!(changeOnDeathBypass && bp.getPlayer().hasPermission(changeOnDeathPermission))) {
                if (Math.random() > chance) return;
            }
        }

        AssignmentGroup group = getRandomGroup();
        if (group != null) {
            assignGroup(group, bp);
        } else if (bp.isOnline()) {
            ChatUtil.sendBrandingMessage(bp.getPlayer(), "No group could be assigned.");
        }
    }

    public List<AssignmentGroup> getGroups() {
        return groups;
    }

    public double getTotalWeight() {
        return totalWeight;
    }

}
