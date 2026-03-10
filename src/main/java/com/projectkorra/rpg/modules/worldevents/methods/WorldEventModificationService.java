package com.projectkorra.rpg.modules.worldevents.methods;

import com.projectkorra.projectkorra.attribute.AttributeModification;
import com.projectkorra.projectkorra.attribute.AttributeModifier;
import com.projectkorra.projectkorra.attribute.AttributeUtil;
import com.projectkorra.projectkorra.event.AbilityRecalculateAttributeEvent;
import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.WorldEventRegistry;
import commonslang3.projectkorra.lang3.tuple.Pair;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * A service class responsible for applying world-event-based attribute modifications to abilities.
 * It processes active world events and applies relevant attribute modifications configured within
 * the world event's configuration file to the abilities based on their context (element, ability name, attribute name).
 * Has to listen to the {@link AbilityRecalculateAttributeEvent} to function.
 */
public class WorldEventModificationService {

	private static final String GLOBAL_PATH_FORMAT = "Abilities._All.%s";
	private static final String ELEMENTS_PATH_FORMAT = "Abilities.%s._All.%s";
	private static final String ABILITIES_PATH_FORMAT = "Abilities.%s.%s.%s";

	private final Plugin plugin;
	private final WorldEventRegistry registry;

	public WorldEventModificationService(Plugin plugin, WorldEventRegistry registry) {
		this.plugin = plugin;
		this.registry = registry;
	}

	public void applyWorldEventMods(AbilityRecalculateAttributeEvent event) {
		AttributeContext context = new AttributeContext(
				event.getAbility().getElement().getName(),
				event.getAbility().getName(),
				event.getAttribute()
		);

		registry.getActiveEvents().forEach(worldEvent -> processWorldEvent(event, worldEvent, context));
	}

	private void processWorldEvent(AbilityRecalculateAttributeEvent event, WorldEvent worldEvent, AttributeContext context) {
		Object rawValue = findConfigurationValue(worldEvent.getConfig(), context);
		if (rawValue == null) return;

		AttributeModification mod = buildModification(rawValue, worldEvent.getWorldEventNamespacedKey());
		if (mod != null) {
			event.addModification(mod);
		}
	}

	private Object findConfigurationValue(FileConfiguration config, AttributeContext context) {
		String abilitySpecificPath = String.format(ABILITIES_PATH_FORMAT, context.element(), context.abilityName(), context.attributeName());
		Object value = config.get(abilitySpecificPath);
		if (value != null) return value;

		String elementPath = String.format(ELEMENTS_PATH_FORMAT, context.element(), context.attributeName());
		value = config.get(elementPath);
		if (value != null) return value;

		String globalPath = String.format(GLOBAL_PATH_FORMAT, context.attributeName());
		return config.get(globalPath);
	}

	private AttributeModification buildModification(Object raw, NamespacedKey nsKey) {
		if (raw instanceof Boolean) {
			return AttributeModification.setter((Boolean) raw, AttributeModification.PRIORITY_NORMAL, nsKey);
		}

		if (raw instanceof Number) {
			return AttributeModification.of(AttributeModifier.SET, (Number) raw, AttributeModification.PRIORITY_NORMAL, nsKey);
		}

		String rawStr = raw.toString().replace(" ", "");
		Pair<AttributeModifier, Number> parsed = AttributeUtil.getModification(rawStr);

		if (parsed != null) {
			return AttributeModification.of(parsed.getLeft(), parsed.getRight(), AttributeModification.PRIORITY_NORMAL, nsKey);
		}

		plugin.getLogger().warning("WorldEvent parse failed for key=" + nsKey.getKey() + " raw=" + rawStr);
		return null;
	}

	record AttributeContext(String element, String abilityName, String attributeName) {}

}
