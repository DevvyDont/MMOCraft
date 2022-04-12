package me.devvy.mmocraft.storage;

import me.devvy.mmocraft.items.ItemRarity;
import me.devvy.mmocraft.items.ItemType;
import org.bukkit.Material;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemAttributesStorage {

    private static final Map<String, ItemAttributes> itemAttributesMap = new HashMap<>();

    public static void storeFromCSVRow(List<String> parsedValues) {

        System.out.println(parsedValues);

        ItemAttributes attributes = new ItemAttributes();

        attributes.ID_NAME = parsedValues.get(0);
        attributes.DISPLAY_NAME = parsedValues.get(1);
        attributes.MATERIAL = Material.valueOf(parsedValues.get(2));
        attributes.CUSTOM_TYPE = ItemType.valueOf(parsedValues.get(3));
        attributes.RARITY = ItemRarity.valueOf(parsedValues.get(4));
        attributes.GLINT = Boolean.parseBoolean(parsedValues.get(5));
        attributes.IGNORE_VANILLA_RECIPES = Boolean.parseBoolean(parsedValues.get(6));
        attributes.LEVEL_REQUIREMENT = Integer.parseInt(parsedValues.get(7));
        attributes.HEALTH = Integer.parseInt(parsedValues.get(8));
        attributes.DAMAGE = Integer.parseInt(parsedValues.get(9));
        attributes.STRENGTH = Integer.parseInt(parsedValues.get(10));
        attributes.DEFENSE = Integer.parseInt(parsedValues.get(11));
        attributes.ATTACK_SPEED = Integer.parseInt(parsedValues.get(12));
        attributes.CRITICAL_CHANCE = Integer.parseInt(parsedValues.get(13));
        attributes.CRITICAL_DAMAGE = Integer.parseInt(parsedValues.get(14));
        attributes.SPEED = Integer.parseInt(parsedValues.get(15));

        attributes.DESCRIPTION = parsedValues.get(16);
        if (attributes.DESCRIPTION.equalsIgnoreCase("BLANK"))
            attributes.DESCRIPTION = "";

        // Store these attributes
        itemAttributesMap.put(attributes.ID_NAME, attributes);
    }

    public static boolean itemRegistered(String uniqueIdentifier) {
        return itemAttributesMap.containsKey(uniqueIdentifier);
    }

    public static ItemAttributes getItemAttributes(String uniqueIdentifier) {

        if (!itemAttributesMap.containsKey(uniqueIdentifier))
            throw new IllegalArgumentException("There is no registered item with custom ID: '" + uniqueIdentifier + "' please check the ID_NAME column in items.csv and try again");


        return itemAttributesMap.get(uniqueIdentifier);
    }

    public static Collection<ItemAttributes> getAllRegisteredItemAttributes() {
        return itemAttributesMap.values();
    }

}
