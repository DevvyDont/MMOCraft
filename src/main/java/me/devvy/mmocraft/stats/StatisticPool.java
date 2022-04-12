package me.devvy.mmocraft.stats;

import me.devvy.mmocraft.MMOCraft;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

// A container for any base stats that an entity/item may have
public class StatisticPool {

    // A map of stat type to its actual instance to be retrieved from
    private final Map<StatisticType, BaseStatistic> statMap;

    // If we aren't given any parameters, this means we are usually just constructing our own stat pool
    // Leave all stats blank so we can set it ourselves
    // Stat pools should only be constructed in two instances:
    // - You are making a stat pool to put on an item or mob/player
    // - You are loading a stat pool via persistent data container via container.get(key, PersistentBaseStatisticContainer.CONTAINER)
    public StatisticPool() {
        // Create the stat pool map
        statMap = new HashMap<>();
        // Set all statistics to a flat 0 base 0 bonuses
        initializeZeroPool();
    }

    // Initializes all statistics to zero
    private void initializeZeroPool() {
        for (StatisticType type : StatisticType.values())
            addEmptyStatistic(type);
    }

    // Adds a statistic instance to this pool of stats zero'd out, should only be called when this obj is constructed
    private void addEmptyStatistic(StatisticType type) {
        try {
            BaseStatistic instance = type.CLAZZ.getDeclaredConstructor().newInstance();
            statMap.put(type, instance);
        } catch (NoSuchMethodException| InvocationTargetException| InstantiationException| IllegalAccessException e){
            MMOCraft.getInstance().getLogger().severe("Failed to add statistic " + type);
            e.printStackTrace();
        }
    }

    // Adds a statistic instance to this pool of stats, used if we want to make our own stat class
    // replaces the statistic already stored
    public void addStatistic(BaseStatistic statistic) {
        statMap.put(statistic.getType(), statistic);
    }

    // Sets a statistic to 0
    public void removeStatistic(StatisticType statisticType) {
        getStatistic(statisticType).zero();
    }

    public BaseStatistic getStatistic(StatisticType statType) {
        // With how we are designing this pool, it should be impossible for a statistic to not be present
        // unless the developer screwed up
        return statMap.get(statType);
    }

    public Collection<BaseStatistic> getAllStatistics() {
        return getAllStatistics(false);
    }

    public Collection<BaseStatistic> getAllStatistics(boolean ordered) {
        if (!ordered)
            return statMap.values();

        List<BaseStatistic> orderedList = new ArrayList<>();
        for (StatisticType type : StatisticType.values())
            orderedList.add(getStatistic(type));

        return orderedList;
    }

    public Collection<BaseStatistic> getAllNonzeroStatistics() {

        List<BaseStatistic> orderedList = new ArrayList<>();
        // Loop through all stats
        for (StatisticType type : StatisticType.values())
            // If it is non zero, add it
            if (getStatistic(type).getTotalValue() != 0)
                orderedList.add(getStatistic(type));

        return orderedList;
    }

    public StatisticPool copy() {
        StatisticPool pool = new StatisticPool();
        for (BaseStatistic stat : getAllStatistics())
            pool.addStatistic(stat);
        return pool;
    }

    public void clearBonusesOfType(String reason) {

        for (BaseStatistic stat : getAllStatistics())
            stat.removeBonus(reason);

    }

    @Override
    public String toString() {
        return "StatisticPool{" +
                "statMap=" + getAllNonzeroStatistics() +
                '}';
    }
}
