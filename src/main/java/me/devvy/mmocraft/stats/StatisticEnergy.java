package me.devvy.mmocraft.stats;

import org.bukkit.entity.Entity;

public class StatisticEnergy extends BaseStatistic {
    @Override
    public StatisticType getType() {
        return StatisticType.ENERGY;
    }

    @Override
    public void apply(Entity entity) {
        // Handled somewhere else
    }
}
