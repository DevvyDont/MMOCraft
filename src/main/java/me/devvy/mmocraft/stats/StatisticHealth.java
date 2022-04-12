package me.devvy.mmocraft.stats;

import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class StatisticHealth extends BaseStatistic {

    @Override
    public StatisticType getType() {
        return StatisticType.HEALTH;
    }

    @Override
    public void apply(Entity entity) {

        // If this entity can have attributes, set their max HP
        if (entity instanceof Attributable){
            double hpToSet = Math.max(1, getTotalValue());
            ((Attributable) entity).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(hpToSet);
            // If this is a player, only show 10 hearts
            if (entity instanceof Player){
                ((Player) entity).setHealthScaled(true);
                ((Player) entity).setHealthScale(20);
            }
        }

    }
}
