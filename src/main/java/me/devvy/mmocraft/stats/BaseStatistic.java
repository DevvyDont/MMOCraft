package me.devvy.mmocraft.stats;

import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseStatistic {

    // The base value of this stat, this is what would be persistently stored on a player for example
    int base = 0;
    // A list of strings that map to bonus amounts of this stat, a string is considered a reason, mapping to an int
    // that represents how much is actually given from that reason
    // This is done so that we can't infinitely stack bonuses from the same source
    private Map<String, Integer> bonuses = new HashMap<>();

    // The base value is the statistic that is stored on the entity, this stat is analogous to HP of a character
    // WITHOUT any bonuses from armor, temporary buffs, etc. base HP usually gets increased from leveling up
    public int getBaseValue() {
        return base;
    }

    // Returns the total amount of this stat generated from bonuses, like wearing armor, active abilities, etc
    public int getBonusValue() {
        int sum = 0;

        for (int n: bonuses.values())
            sum += n;

        return sum;
    }

    public void addBonus(String uniqueReason, int amount) {
        bonuses.put(uniqueReason, amount);
    }

    public void removeBonus(String uniqueReason) {
        bonuses.remove(uniqueReason);
    }

    public void clearBonuses() {
        bonuses.clear();
    }

    // Completely sets a statistic to 0
    public void zero() {
        setBaseValue(0);
        clearBonuses();
    }

    public void setBaseValue(int n) {
        this.base = n;
    }

    // The complete total of this statistic, base + bonus
    public int getTotalValue() {
        return getBaseValue() + getBonusValue();
    }

    // Get the type of statistic that this is associated with
    public abstract StatisticType getType();

    // Logic for actually applying the statistic to the entity
    public abstract void apply(Entity entity);

    @Override
    public String toString() {
        return getType() + "{" +
                "base=" + base +
                ", bonuses=" + bonuses +
                '}';
    }
}
