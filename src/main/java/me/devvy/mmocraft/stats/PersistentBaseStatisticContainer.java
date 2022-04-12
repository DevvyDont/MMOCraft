package me.devvy.mmocraft.stats;

import me.devvy.mmocraft.MMOCraft;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class PersistentBaseStatisticContainer implements PersistentDataType<int[], StatisticPool> {

    public static final PersistentBaseStatisticContainer BASE_STATISTIC_CONTAINER = new PersistentBaseStatisticContainer();
    public static final NamespacedKey BASE_STATISTIC_CONTAINER_KEY = new NamespacedKey(MMOCraft.getInstance(), "base_stats");

    @Override
    public @NotNull Class<int[]> getPrimitiveType() {
        return int[].class;
    }

    @Override
    public @NotNull Class<StatisticPool> getComplexType() {
        return StatisticPool.class;
    }

    @Override
    public int @NotNull [] toPrimitive(@NotNull StatisticPool complex, @NotNull PersistentDataAdapterContext context) {

        // Construct an array of stats with a spot for every single stat
        int[] arrayOfStats = new int[StatisticType.values().length];

        // Loop through all stats, using the enum as index for array we want to return
        for (StatisticType type : StatisticType.values())
            // Pull the value from the pool, and store it
            arrayOfStats[type.ordinal()] = complex.getStatistic(type).getBaseValue();

        return arrayOfStats;
    }

    @Override
    public @NotNull StatisticPool fromPrimitive(int @NotNull [] primitive, @NotNull PersistentDataAdapterContext context) {
        // Given an array of stats, construct a stat pool object
        StatisticPool statPool = new StatisticPool();
        // If the length that is stored does not match the amount of attributes that we have defined,
        // assume default value for that stat
        int[] statsStored = primitive;
        if (primitive.length != statPool.getAllStatistics().size())
            statsStored = fillInBlanks(statPool, statsStored);

        for (StatisticType type : StatisticType.values()) {
            int baseStatStored = statsStored[type.ordinal()];
            statPool.getStatistic(type).setBaseValue(baseStatStored);
        }

        // Now that we retrieved all the stats, return the stat pool instance
        return statPool;
    }

    public int[] fillInBlanks(StatisticPool statPool, int[] oldArray) {

        // Construct a fixed array to return with appropiate length
        int[] fixedArray = new int[StatisticType.values().length];

        // Copy over the elements in the old array
        System.arraycopy(oldArray, 0, fixedArray, 0, oldArray.length);

        // Now copy over the rest of the elements that are missing
        for (int i = oldArray.length; i < fixedArray.length; i++)
            // Grabs stat from the stat pool, and updates the value in the array to whatever is stored
            fixedArray[i] = statPool.getStatistic(StatisticType.values()[i]).getBaseValue();

        return fixedArray;
    }
}
