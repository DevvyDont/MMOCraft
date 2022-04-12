package me.devvy.mmocraft.stats;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;

// More defense = take less damage from most sources, % dmg you take is 100/(def + 100)%, damage you resist is 1-%dmg you take
public class StatisticDefense extends BaseStatistic {

    // Gets how much percent we should resist from a damage source
    public double getResistPercent(EntityDamageEvent.DamageCause cause) {
        // How much damage do we resist based on our defense stat?
        double resistPercent = 1 - (100.0 / (getTotalValue() + 100));
        // How much should we multiply this percent by based on a cause, this will always be a number that is 1 or less
        resistPercent *= getCauseMultiplier(cause);
        return resistPercent;
    }

    // Similar to getResistPercent, but the other way around, gets how much % of damage we should take
    public double getPercentDamageTaken(EntityDamageEvent.DamageCause cause) {
        return 1 - getResistPercent(cause);
    }

    // Given a damage cause, returns a number that is less than or equal to 1 that multiplies the resist percent
    // due to a specific cause. For example, defense does not work on fall damage. therefore we return 1. Defense also helps
    // With fire damage, but not too well, so we can multiply by .5
    public double getCauseMultiplier(EntityDamageEvent.DamageCause cause) {
        switch (cause) {

            // What stuff do we want defense to not affect?
            case FALL:
            case MAGIC:
            case CUSTOM:
            case DROWNING:
            case DRYOUT:
            case FLY_INTO_WALL:
            case MELTING:
            case CRAMMING:
            case VOID:
            case POISON:
            case SUICIDE:
            case LIGHTNING:
            case STARVATION:
            case SUFFOCATION:
            case WITHER:
            case FALLING_BLOCK:
                return 0.0;


            // What stuff should defense slightly affect?
            case BLOCK_EXPLOSION:
            case CONTACT:
            case ENTITY_EXPLOSION:
            case DRAGON_BREATH:
                return 0.25;

            case LAVA:
            case FIRE:
            case FIRE_TICK:
            case HOT_FLOOR:
                return 0.5;
        }

        return 1.0;  // Case not specified, we resist full damage
    }

    @Override
    public StatisticType getType() {
        return StatisticType.DEFENSE;
    }

    @Override
    public void apply(Entity entity) {
        // Handled somewhere else
    }
}
