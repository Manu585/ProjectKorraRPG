package com.projectkorra.rpg.modules.elementassignments.model;

import com.projectkorra.projectkorra.Element;
import java.util.ArrayList;
import java.util.List;

public class AssignmentGroup {

    private final String name;
    private final List<Element> elements;
    private final double weight;
    private final boolean enabled;
    private final String prefix;
    private final String permissionGroup;

    private final List<String> commandsToRun;

    public AssignmentGroup(String name, List<String> elements, double weight, boolean enabled, String prefix, List<String> commandsToRun, String permissionGroup) {
        this.name = name;
        this.weight = weight;
        this.enabled = enabled;
        this.prefix = prefix;
        this.permissionGroup = permissionGroup;
        this.commandsToRun = commandsToRun;
        this.elements = new ArrayList<>();
        for (String elementName : elements) {
            Element element = Element.getElement(elementName);
            if (element != null) {
                this.elements.add(element);
            }
        }
    }

    public String getName() {
        return name;
    }

    public List<Element> getElements() {
        return elements;
    }

    public void addElement(Element element) {
        this.elements.add(element);
    }

    public void removeElement(Element element) {
        this.elements.remove(element);
    }

    public double getWeight() {
        return weight;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getPermissionGroup() {
        return permissionGroup;
    }

    public List<String> getCommandsToRun() {
        return commandsToRun;
    }

}
