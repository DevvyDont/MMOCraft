package me.devvy.mmocraft.storage;

import me.devvy.mmocraft.MMOCraft;
import org.bukkit.entity.EntityType;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// A class that takes an entity ID (found in creatures.csv) and returns an instance of contained attributes
public class EntityAttributesStorage {

    private static final Map<String, EntityAttributes> entityAttributesMap = new HashMap<>();

    public static void storeFromCSVRow(List<String> parsedValues) {

        System.out.println(parsedValues);

        EntityAttributes attributes = new EntityAttributes();
        // Kinda stupid how we have to do this, but set all attributes based on the list we are provided
        // Do any casting as well for integer values
        attributes.ID_NAME = parsedValues.get(0);  // The unique identifier for this creature
        attributes.CREATURE_TYPE = EntityAttributes.CreatureType.valueOf(parsedValues.get(1));  // enemy/boss/miniboss
        attributes.DISPLAY_NAME = parsedValues.get(2);  // What to show on the nametag and in chat
        attributes.MINECRAFT_TYPE = EntityType.valueOf(parsedValues.get(3));  // The actual minecraft entity to utilize
        attributes.START_LEVEL = Integer.parseInt(parsedValues.get(4));  // What level can this mob start at
        attributes.END_LEVEL = Integer.parseInt(parsedValues.get(5));  // What level can this mob end at
        attributes.BASE_HEALTH = Integer.parseInt(parsedValues.get(6)); // How much health does this mob have at its starting level
        attributes.BASE_DAMAGE = Integer.parseInt(parsedValues.get(7));  // Same logic as above for all base stats
        attributes.BASE_STRENGTH = Integer.parseInt(parsedValues.get(8));
        attributes.BASE_DEFENSE = Integer.parseInt(parsedValues.get(9));
        attributes.BASE_ATTACK_SPEED = Integer.parseInt(parsedValues.get(10));
        attributes.BASE_CRITICAL_CHANCE = Integer.parseInt(parsedValues.get(11));
        attributes.BASE_CRITICAL_DAMAGE = Integer.parseInt(parsedValues.get(12));
        attributes.BASE_SPEED = Integer.parseInt(parsedValues.get(13));
        attributes.HEALTH_PER_LEVEL = Integer.parseInt(parsedValues.get(14));  // For every level above its start, how much do we give per level?
        attributes.DAMAGE_PER_LEVEL = Integer.parseInt(parsedValues.get(15));
        attributes.STRENGTH_PER_LEVEL = Integer.parseInt(parsedValues.get(16));
        attributes.DEFENSE_PER_LEVEL = Integer.parseInt(parsedValues.get(17));
        attributes.ATTACK_SPEED_PER_LEVEL = Integer.parseInt(parsedValues.get(18));
        attributes.CRITICAL_CHANCE_PER_LEVEL = Integer.parseInt(parsedValues.get(19));
        attributes.CRITICAL_DAMAGE_PER_LEVEL = Integer.parseInt(parsedValues.get(20));
        attributes.SPEED_PER_LEVEL = Integer.parseInt(parsedValues.get(21));
        attributes.LOOT_TABLE = parsedValues.get(22);  // The unique identifier for the loot table of this creature

        // Store these attributes
        entityAttributesMap.put(attributes.ID_NAME, attributes);
    }

    public static boolean entityRegistered(String uniqueIdentifier) {
        return entityAttributesMap.containsKey(uniqueIdentifier);
    }

    public static EntityAttributes getEntityAttributes(String uniqueIdentifier) {

        if (!entityAttributesMap.containsKey(uniqueIdentifier))
            throw new IllegalArgumentException("There is no registered entity with custom ID: '" + uniqueIdentifier + "' please check the ID_NAME column in creatures.csv and try again");


        return entityAttributesMap.get(uniqueIdentifier);
    }

    public static Collection<EntityAttributes> getAllRegisteredEntityAttributes() {
        return entityAttributesMap.values();
    }



}
