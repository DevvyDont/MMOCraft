package me.devvy.mmocraft.stats;

import org.bukkit.entity.Entity;

public class StatisticsDamage extends BaseStatistic {

    @Override
    public StatisticType getType() {
        return StatisticType.DAMAGE;
    }

    @Override
    public void apply(Entity entity) {
        // Handled somewhere else
    }
}
