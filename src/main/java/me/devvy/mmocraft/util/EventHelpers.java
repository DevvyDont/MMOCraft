package me.devvy.mmocraft.util;

import org.bukkit.event.entity.EntityDamageEvent;

public class EventHelpers {

    public static boolean isEntityVsEntityCause(EntityDamageEvent.DamageCause cause) {

        // Taken from above, we need to only consider events that we left for entity vs entity
        switch (cause) {

            // Ignored cases, this is handled from entity vs entity damage event
            case THORNS:
            case ENTITY_ATTACK:
            case PROJECTILE:
            case FALLING_BLOCK:
            case ENTITY_EXPLOSION:
            case DRAGON_BREATH:
            case ENTITY_SWEEP_ATTACK:
            case MAGIC:
                return true;
        }

        return false;

    }

}
