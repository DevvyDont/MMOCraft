package me.devvy.mmocraft.stats;

import me.devvy.mmocraft.util.SpeedConstants;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;

public class StatisticMovementSpeed extends BaseStatistic {

    @Override
    public StatisticType getType() {
        return StatisticType.SPEED;
    }

    // Since our system uses percentages, 100% is default, translate based on the default speed of this entity
    public double toMinecraftValue(Attributable entity, int n) {
        double def = SpeedConstants.getDefaultEntitySpeed((Entity) entity);
        // If 100 is normal speed, 100 should be translated to 1.0 meaning divide OUR stat by 100
        double percentIncrease = (double) n / 100.0;
        return def * percentIncrease;
    }

    @Override
    public void apply(Entity entity) {
        if (entity instanceof Attributable)
            ((Attributable) entity).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(this.toMinecraftValue((Attributable) entity, this.getTotalValue()));
    }
}
