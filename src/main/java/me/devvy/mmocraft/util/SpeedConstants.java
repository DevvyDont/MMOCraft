package me.devvy.mmocraft.util;

import me.devvy.mmocraft.MMOCraft;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Slime;

import java.util.logging.Logger;

public class SpeedConstants {

    // Really fucking annoying that spigot/minecraft has no built in way to do this that i know of
    // AttributeInstance.getDefaultValue() for speed attribute always returns 0.7
    // So we need a version of this but per entity type
    // These are default movement speeds for all entities in a vanilla context so our speed stat knows what
    // percentage to apply on this speed constant
    public static double getDefaultEntitySpeed(Entity entity) {

        // Evaluate the type of entity (taken straight from https://minecraft.fandom.com/wiki/Attribute)
        switch (entity.getType()) {

            case PLAYER:
                return 0.1;

            case DONKEY:
            case LLAMA:
            case MULE:
            case STRIDER:
            case HORSE:  // This one actually has a range of 0.1125-0.3375, but our speed stat can handle this
                return 0.175;

            case COW:
            case MAGMA_CUBE:
            case MUSHROOM_COW:
            case PARROT:
            case SKELETON_HORSE:
            case SNOWMAN:
            case ZOMBIE_HORSE:
                return 0.2;

            case BLAZE:
            case DROWNED:
            case HUSK:
            case SHEEP:
            case ZOMBIE:
            case ZOMBIE_VILLAGER:
            case ZOMBIFIED_PIGLIN:
                return 0.23;

            case CHICKEN:
            case CREEPER:
            case ENDERMITE:
            case IRON_GOLEM:
            case PIG:
            case POLAR_BEAR:
            case SILVERFISH:
            case SKELETON:
            case STRAY:
            case TURTLE:
            case WITCH:
            case WITHER_SKELETON:
                return 0.25;

            case BEE:
            case CAT:
            case CAVE_SPIDER:
            case ELDER_GUARDIAN:
            case ENDERMAN:
            case FOX:
            case OCELOT:
            case RABBIT:
            case RAVAGER:
            case SPIDER:
            case WOLF:
                return 0.3;

            case PILLAGER:
            case VINDICATOR:
                return 0.35;

            case HOGLIN:
                return 0.4;

            case EVOKER:
            case GIANT:
            case GUARDIAN:
            case ILLUSIONER:
            case PIGLIN:
            case VILLAGER:
            case WANDERING_TRADER:
                return 0.5;

            case WITHER:
                return 0.6;

            case BAT:
            case COD:
            case ENDER_DRAGON:
            case GHAST:
            case PUFFERFISH:
            case SALMON:
            case SHULKER:
            case SQUID:
            case TROPICAL_FISH:
            case VEX:
                return 0.7;

            case DOLPHIN:
                return 1.2;

            case SLIME:
                Slime slime = (Slime) entity;
                return 0.2 + 0.1 * slime.getSize();

            case PANDA:
                Panda panda = (Panda) entity;
                return panda.getMainGene() == Panda.Gene.LAZY ? 0.07 : 0.15;

            // The following don't have something defined on the wiki, so we guess for these ones
            case GOAT:
                return 0.2;
            case ZOGLIN:
                return 0.15;
            case AXOLOTL:
                return 0.35;
            case PHANTOM:
                return 0.4;
            case GLOW_SQUID:
                return 0.15;
            case PIGLIN_BRUTE:
                return 0.5;
            case TRADER_LLAMA:
                return 0.2;

            default:
                MMOCraft.getInstance().getLogger().severe(String.format("No speed constant for %s, defaulting to 0.7. Is this plugin out of date?", entity.getType()));
                return 0.7;
        }

    }

}
