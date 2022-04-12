package me.devvy.mmocraft.stats;

import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;

public class StatisticAttackSpeed extends BaseStatistic {

    @Override
    public StatisticType getType() {
        return StatisticType.ATTACK_SPEED;
    }

    // Since our system uses percentages, 100% is default, translate based on the default attack of this entity
    public double toMinecraftValue(Attributable entity, int n) {
        double def = entity.getAttribute(Attribute.GENERIC_ATTACK_SPEED).getDefaultValue();
        // If 100 is normal attack speed, 100 should be translated to 1.0 meaning divide OUR stat by 100
        double percentIncrease = (double) n / 100.0;
        return def * percentIncrease;
    }

    @Override
    public void apply(Entity entity) {

        // If our entity can have attributes, set their attack speed to what this stat is
        if (entity instanceof Attributable) {
            // Ensure they have the attribute
            if (((Attributable) entity).getAttribute(Attribute.GENERIC_ATTACK_SPEED) == null)
                ((Attributable) entity).registerAttribute(Attribute.GENERIC_ATTACK_SPEED);

            // Set the attribute
            ((Attributable) entity).getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(this.toMinecraftValue((Attributable) entity, this.getTotalValue()));
        }
    }
}
