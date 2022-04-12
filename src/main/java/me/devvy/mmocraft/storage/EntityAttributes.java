package me.devvy.mmocraft.storage;

import me.devvy.mmocraft.stats.StatisticPool;
import me.devvy.mmocraft.stats.StatisticType;
import org.bukkit.entity.EntityType;

// Storage for a specific entity
public class EntityAttributes {

    public enum CreatureType {
        ENEMY,
        ELITE,
        MINIBOSS;
    }


    public String ID_NAME;
    public CreatureType CREATURE_TYPE;
    public String DISPLAY_NAME;
    public EntityType MINECRAFT_TYPE;
    public int START_LEVEL;
    public int END_LEVEL;
    public int BASE_HEALTH;
    public int BASE_DAMAGE;
    public int BASE_STRENGTH;
    public int BASE_DEFENSE;
    public int BASE_ATTACK_SPEED;
    public int BASE_CRITICAL_CHANCE;
    public int BASE_CRITICAL_DAMAGE;
    public int BASE_SPEED;
    public int HEALTH_PER_LEVEL;
    public int DAMAGE_PER_LEVEL;
    public int STRENGTH_PER_LEVEL;
    public int DEFENSE_PER_LEVEL;
    public int ATTACK_SPEED_PER_LEVEL;
    public int CRITICAL_CHANCE_PER_LEVEL;
    public int CRITICAL_DAMAGE_PER_LEVEL;
    public int SPEED_PER_LEVEL;
    public String LOOT_TABLE;

    public StatisticPool constructStatPool(int level) {

        int levelsAboveMin = level - START_LEVEL;
        // If they are above the minimum level they can be, they are going to have bonus stats
        int bonusHealth = levelsAboveMin * HEALTH_PER_LEVEL;
        int bonusDamage = levelsAboveMin * DAMAGE_PER_LEVEL;
        int bonusStrength = levelsAboveMin * STRENGTH_PER_LEVEL;
        int bonusDefense = levelsAboveMin * DEFENSE_PER_LEVEL;
        int bonusAttackSpeed = levelsAboveMin * ATTACK_SPEED_PER_LEVEL;
        int bonusCritChance = levelsAboveMin * CRITICAL_CHANCE_PER_LEVEL;
        int bonusCritDamage = levelsAboveMin * CRITICAL_DAMAGE_PER_LEVEL;
        int bonusSpeed = levelsAboveMin * SPEED_PER_LEVEL;

        StatisticPool pool = new StatisticPool();
        pool.getStatistic(StatisticType.HEALTH).setBaseValue(BASE_HEALTH+bonusHealth);
        pool.getStatistic(StatisticType.DAMAGE).setBaseValue(BASE_DAMAGE+bonusDamage);
        pool.getStatistic(StatisticType.STRENGTH).setBaseValue(BASE_STRENGTH+bonusStrength);
        pool.getStatistic(StatisticType.DEFENSE).setBaseValue(BASE_DEFENSE+bonusDefense);
        pool.getStatistic(StatisticType.ATTACK_SPEED).setBaseValue(BASE_ATTACK_SPEED+bonusAttackSpeed);
        pool.getStatistic(StatisticType.CRITICAL_CHANCE).setBaseValue(BASE_CRITICAL_CHANCE+bonusCritChance);
        pool.getStatistic(StatisticType.CRITICAL_DAMAGE).setBaseValue(BASE_CRITICAL_DAMAGE+bonusCritDamage);
        pool.getStatistic(StatisticType.SPEED).setBaseValue(BASE_SPEED+bonusSpeed);
        return pool;
    }

    // If we aren't given a level, assume the lowest level this mob can be
    public StatisticPool constructStatPool() {
        return constructStatPool(START_LEVEL);
    }

    @Override
    public String toString() {
        return "EntityAttributes{" +
                "ID_NAME='" + ID_NAME + '\'' +
                ", CREATURE_TYPE=" + CREATURE_TYPE +
                ", DISPLAY_NAME='" + DISPLAY_NAME + '\'' +
                ", MINECRAFT_TYPE=" + MINECRAFT_TYPE +
                ", START_LEVEL=" + START_LEVEL +
                ", END_LEVEL=" + END_LEVEL +
                ", BASE_HEALTH=" + BASE_HEALTH +
                ", BASE_DAMAGE=" + BASE_DAMAGE +
                ", BASE_STRENGTH=" + BASE_STRENGTH +
                ", BASE_DEFENSE=" + BASE_DEFENSE +
                ", BASE_ATTACK_SPEED=" + BASE_ATTACK_SPEED +
                ", BASE_CRITICAL_CHANCE=" + BASE_CRITICAL_CHANCE +
                ", BASE_CRITICAL_DAMAGE=" + BASE_CRITICAL_DAMAGE +
                ", BASE_SPEED=" + BASE_SPEED +
                ", HEALTH_PER_LEVEL=" + HEALTH_PER_LEVEL +
                ", DAMAGE_PER_LEVEL=" + DAMAGE_PER_LEVEL +
                ", STRENGTH_PER_LEVEL=" + STRENGTH_PER_LEVEL +
                ", DEFENSE_PER_LEVEL=" + DEFENSE_PER_LEVEL +
                ", ATTACK_SPEED_PER_LEVEL=" + ATTACK_SPEED_PER_LEVEL +
                ", CRITICAL_CHANCE_PER_LEVEL=" + CRITICAL_CHANCE_PER_LEVEL +
                ", CRITICAL_DAMAGE_PER_LEVEL=" + CRITICAL_DAMAGE_PER_LEVEL +
                ", SPEED_PER_LEVEL=" + SPEED_PER_LEVEL +
                ", LOOT_TABLE='" + LOOT_TABLE + '\'' +
                '}';
    }
}