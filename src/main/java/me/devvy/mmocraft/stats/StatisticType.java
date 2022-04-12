package me.devvy.mmocraft.stats;

import me.devvy.mmocraft.MMOCraft;
import me.devvy.mmocraft.items.ItemType;
import org.bukkit.NamespacedKey;

public enum StatisticType {
    // How much health an entity has
    HEALTH(         100,  0, StatisticHealth.class        ),
    // How much a base damage hit does, base value is fist damage or mob damage, items can increase this value with bonus damage
    DAMAGE(         5,    0, StatisticsDamage.class        ),
    // Final damage multiplier, 100 = you deal 100% damage, 250 = you deal 250% damage
    STRENGTH(       100,  0, StatisticStrength.class      ),
    // More defense = take less damage from most sources, % dmg you take is 100/(def + 100)%, damage you resist is 1-%dmg you take
    DEFENSE(        0,    0, StatisticDefense.class       ),
    // Basically mana, some abilities cost energy
    ENERGY(         100,  0, StatisticEnergy.class        ),
    // How fast you can attack, like strength, its a percent
    ATTACK_SPEED(   100,  0, StatisticAttackSpeed.class   ),
    // % chance to perform a crit
    CRITICAL_CHANCE(20,   0, StatisticCriticalChance.class),
    // How much extra % the crit damage does, 50 means +50% damage
    CRITICAL_DAMAGE(50,   0, StatisticCriticalDamage.class),
    // Percentage buff on how fast you move, 110 means you have a 10% speed buff
    SPEED(          100,  0, StatisticMovementSpeed.class );

    public final int DEFAULT;
    public final int DEFAULT_BONUS;
    public final NamespacedKey KEY;
    public final Class<? extends BaseStatistic> CLAZZ;

    // A defaultValue is typically what stats a level 1 player would have, a defaultBonus is what to default to if this
    // were to be the stats on an item, so we shouldnt modify attack speed by default
    StatisticType(int defaultValue, int defaultBonus, Class<? extends BaseStatistic> clazz) {
        this.DEFAULT = defaultValue;
        this.DEFAULT_BONUS = defaultBonus;
        this.KEY = new NamespacedKey(MMOCraft.getInstance(), this.toString());
        this.CLAZZ = clazz;
    }
}
