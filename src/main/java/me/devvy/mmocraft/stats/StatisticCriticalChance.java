package me.devvy.mmocraft.stats;

import org.bukkit.entity.Entity;

public class StatisticCriticalChance extends BaseStatistic {

    public double getChance() {
        return getTotalValue() / 100.0;
    }

    public boolean roll() {
        return Math.random() < getChance();
    }

    @Override
    public StatisticType getType() {
        return StatisticType.CRITICAL_CHANCE;
    }

    @Override
    public void apply(Entity entity) {
        // Don't need to do anything, this is handled somewhere else
    }
}
