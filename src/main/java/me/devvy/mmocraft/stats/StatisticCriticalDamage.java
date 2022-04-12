package me.devvy.mmocraft.stats;

import org.bukkit.entity.Entity;

public class StatisticCriticalDamage extends BaseStatistic {

    public double getDamageMultiplier() {
        return 1 + (getTotalValue() / 100.0);
    }

    @Override
    public StatisticType getType() {
        return StatisticType.CRITICAL_DAMAGE;
    }

    @Override
    public void apply(Entity entity) {
        // Handled somewhere else
    }
}
