package me.devvy.mmocraft.stats;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;

public class StatisticStrength extends BaseStatistic {

    public double getBonusDamageMultiplier(){
        // Return their strength as a percentage, strength of 100 does 1.0x damage, 200 2.0x damage etc
        // Don't return anything less than .01x damage
        return Math.max(.01, getTotalValue() / 100.0);
    }

    @Override
    public StatisticType getType() {
        return StatisticType.STRENGTH;
    }

    @Override
    public void apply(Entity entity) {
        // used somewhere else
    }
}
