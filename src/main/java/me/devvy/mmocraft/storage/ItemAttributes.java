package me.devvy.mmocraft.storage;

import me.devvy.mmocraft.items.ItemRarity;
import me.devvy.mmocraft.items.ItemType;
import me.devvy.mmocraft.stats.StatisticPool;
import me.devvy.mmocraft.stats.StatisticType;
import org.bukkit.Material;

public class ItemAttributes {

    public String ID_NAME;
    public String DISPLAY_NAME;
    public Material MATERIAL;
    public ItemType CUSTOM_TYPE;
    public ItemRarity RARITY;
    public boolean GLINT;
    public boolean IGNORE_VANILLA_RECIPES;
    public int LEVEL_REQUIREMENT;

    public int HEALTH;
    public int DAMAGE;
    public int STRENGTH;
    public int DEFENSE;
    public int ATTACK_SPEED;
    public int CRITICAL_CHANCE;
    public int CRITICAL_DAMAGE;
    public int SPEED;

    public String DESCRIPTION;

    public StatisticPool constructStatPool() {

        StatisticPool pool = new StatisticPool();
        pool.getStatistic(StatisticType.HEALTH).setBaseValue(HEALTH);
        pool.getStatistic(StatisticType.DAMAGE).setBaseValue(DAMAGE);
        pool.getStatistic(StatisticType.STRENGTH).setBaseValue(STRENGTH);
        pool.getStatistic(StatisticType.DEFENSE).setBaseValue(DEFENSE);
        pool.getStatistic(StatisticType.ATTACK_SPEED).setBaseValue(ATTACK_SPEED);
        pool.getStatistic(StatisticType.CRITICAL_CHANCE).setBaseValue(CRITICAL_CHANCE);
        pool.getStatistic(StatisticType.CRITICAL_DAMAGE).setBaseValue(CRITICAL_DAMAGE);
        pool.getStatistic(StatisticType.SPEED).setBaseValue(SPEED);
        return pool;
    }

    @Override
    public String toString() {
        return "ItemAttributes{" +
                "ID_NAME='" + ID_NAME + '\'' +
                ", DISPLAY_NAME='" + DISPLAY_NAME + '\'' +
                ", MATERIAL=" + MATERIAL +
                ", CUSTOM_TYPE=" + CUSTOM_TYPE +
                ", RARITY=" + RARITY +
                ", GLINT=" + GLINT +
                ", IGNORE_VANILLA_RECIPES=" + IGNORE_VANILLA_RECIPES +
                ", LEVEL_REQUIREMENT=" + LEVEL_REQUIREMENT +
                ", HEALTH=" + HEALTH +
                ", DAMAGE=" + DAMAGE +
                ", STRENGTH=" + STRENGTH +
                ", DEFENSE=" + DEFENSE +
                ", ATTACK_SPEED=" + ATTACK_SPEED +
                ", CRITICAL_CHANCE=" + CRITICAL_CHANCE +
                ", CRITICAL_DAMAGE=" + CRITICAL_DAMAGE +
                ", SPEED=" + SPEED +
                ", DESCRIPTION=" + DESCRIPTION +
                '}';
    }
}
