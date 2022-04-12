package me.devvy.mmocraft.entity;

import me.devvy.mmocraft.experience.Experience;
import me.devvy.mmocraft.stats.*;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;

import static me.devvy.mmocraft.experience.PersistentExperienceContainer.EXPERIENCE_CONTAINER;
import static me.devvy.mmocraft.experience.PersistentExperienceContainer.EXPERIENCE_CONTAINER_KEY;

// Contains an entity (mob, player, etc) and has extra functionality associated
// Mainly used to interact with things such as statistics, modify abilities etc
public class MMOEntity {

    private final Entity entity;

    // Experience tables to store on this entity
    private Experience mainLevelExperience;

    // An entity can have a statistic pool attached to it, like strength, speed, etc
    private StatisticPool statPool;

    // When given just an entity, load its stats from its persistent data container
    // If the entity does not have stats defined, it will resort to default stats defined in StatisticType
    public MMOEntity(Entity entity) {
        this.entity = entity;

        // Load any stats or attributes and initialize instances based on data that is stored
        // If anything is missing, this will apply default values and save
        this.loadContainers();

        // Apply the stats
        this.applyStats(true);
    }

    // Initializes containers stored on the entity
    public void loadContainers() {
        // Init any experience trackers we need
        this.mainLevelExperience = entity.getPersistentDataContainer().getOrDefault(EXPERIENCE_CONTAINER_KEY, EXPERIENCE_CONTAINER, new Experience(this));
        this.mainLevelExperience.setOwner(this);

        // Load the stat pool using this entity's data stored
        this.statPool = entity.getPersistentDataContainer().getOrDefault(PersistentBaseStatisticContainer.BASE_STATISTIC_CONTAINER_KEY, PersistentBaseStatisticContainer.BASE_STATISTIC_CONTAINER, getDefaultBaseStats());

        // Hack to save containers if any of them didn't exist, typically happens if someone just joined
        // or an entity just spawned
        saveContainers();
    }

    // Saves the instances of data we need to store persistently
    public void saveContainers() {
        entity.getPersistentDataContainer().set(EXPERIENCE_CONTAINER_KEY, EXPERIENCE_CONTAINER, mainLevelExperience);
        entity.getPersistentDataContainer().set(PersistentBaseStatisticContainer.BASE_STATISTIC_CONTAINER_KEY, PersistentBaseStatisticContainer.BASE_STATISTIC_CONTAINER, statPool);
    }

    public Entity getEntity() {
        return entity;
    }

    // Returns this entities pool of stats, this can be used to modify their base stat, or add bonuses
    public StatisticPool getStatPool() {
        return this.statPool;
    }

    // Given a stat pool, set this entity's stats, used when we create entities usually
    public void applyNewStatPool(StatisticPool statPool) {
        this.statPool = statPool;
        applyStats(true);
    }

    // Goes through and makes all stats take effect, call this if you ever make any change to stats
    // This includes adding bonuses
    public void applyStats(boolean setToMaxHP) {

        for (BaseStatistic stat : this.statPool.getAllStatistics())
            stat.apply(this.entity);

        // Save the stats that were applied
        saveContainers();

        if (setToMaxHP && entity instanceof Damageable) {
            double hpToSet = Math.max(statPool.getStatistic(StatisticType.HEALTH).getTotalValue(), 1);
            ((Damageable) entity).setHealth(hpToSet);
        }
    }

    public void applyStats() {
        applyStats(false);
    }

    public Experience getLevelExperience() {
        return mainLevelExperience;
    }

    // Returns a Statistic pool of default stats for this entity, used if they haven't had stats defined yet
    // Or didn't get a pool passed in in the constructor
    public StatisticPool getDefaultBaseStats() {
        // For any entity, we just return default values for some and whatever minecraft has defined for others
        StatisticPool stats = new StatisticPool();

        // Now handle logic of what to actually set the stat to
        // Entities will default to what we set for some stats, and 0/vanilla mc values for others
        // This method is overridden for players as their base stats are different
        for (BaseStatistic s : stats.getAllStatistics()) {

            switch (s.getType()) {

                // Health is a weird one, generally a vanilla mob should have 5x default HP since our numbers are a lil inflated
                case HEALTH:
                    // If entity can have a max HP, set their base hp stat to 5x their default vanilla value, otherwise default player health
                    if (entity instanceof Attributable)
                        s.setBaseValue((int) (((Attributable) entity).getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue() * 5));
                    else
                        s.setBaseValue(s.getType().DEFAULT);
                    break;

                // Now some minecraft attributes that shouldn't change unless we change them
                case SPEED:
                case ATTACK_SPEED:
                case DAMAGE:
                case STRENGTH:
                    s.setBaseValue(s.getType().DEFAULT);
                    break;

                // Now some stats that should only apply to general entities if wanted, players will have different logic
                case ENERGY:
                case DEFENSE:
                case CRITICAL_CHANCE:
                case CRITICAL_DAMAGE:
                    s.zero();
                    break;

                default:
                    throw new IllegalStateException("Forgot to register default statistic value for general entity " + s.getType().toString());
            }

        }

        return stats;

    }

}
