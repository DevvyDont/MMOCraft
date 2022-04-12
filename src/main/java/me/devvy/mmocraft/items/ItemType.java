package me.devvy.mmocraft.items;

import me.devvy.mmocraft.stats.StatisticType;

// Used so that our plugin knows what kind of item something is. This is to allow for items to act like others for example
// We can make a custom blaze rod have an item type of a sword, and our plugin will know to treat it as such.
public enum ItemType {
    ITEM,  // Default, this is an item that is nothing special and most likely acts as if it were vanilla
    SWORD,     // This item should be treated as a sword
    BOW,         // This item will be treated as a crossbow/bow
    CROSSBOW,    // This item will be treated as a crossbow/bow
    TRIDENT,    // This item will be treated as a crossbow/bow
    HELMET,    // This item will be treated as something you can wear on your head
    CHESTPLATE,    // This item will be treated as something you can wear on your head
    LEGGINGS,    // This item will be treated as something you can wear on your head
    BOOTS,    // This item will be treated as something you can wear on your head
    SHIELD;    // This item will be treated as a shield

    public static boolean isArmor(ItemType type) {
        return switch (type) {
            case HELMET, CHESTPLATE, LEGGINGS, BOOTS -> true;
            default -> false;
        };
    }

    public static boolean isBow(ItemType type) {
        return switch (type) {
            case BOW, CROSSBOW -> true;
            default -> false;
        };
    }

    public static boolean ignoreThisStatIfHeld(ItemType type, StatisticType stat) {

        // If its armor, we never update stats
        if (isArmor(type))
            return true;

        // If its a bow, we don't update stats that affect our damage
        if (isBow(type)) {
            switch (stat) {
                case DAMAGE:
                case CRITICAL_DAMAGE:
                case CRITICAL_CHANCE:
                case STRENGTH:
                case ATTACK_SPEED:
                    return true;
            }
        }

        // should be ok to add the stat
        return false;

    }

}
