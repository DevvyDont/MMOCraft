package me.devvy.mmocraft.listeners;

import me.devvy.mmocraft.MMOCraft;
import me.devvy.mmocraft.entity.EntityManager;
import me.devvy.mmocraft.entity.MMOEntity;
import me.devvy.mmocraft.stats.*;
import me.devvy.mmocraft.util.EventHelpers;
import me.devvy.mmocraft.util.ParticlesHelper;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;

public class VanillaDamageOverrider implements Listener {

    public VanillaDamageOverrider() {
        MMOCraft.getInstance().getServer().getPluginManager().registerEvents(this, MMOCraft.getInstance());
    }

    // Fall damage is based on percent HP someone has, not a flat amount
    public double calculateBaseFallDamage(double distance, double maxHP) {

        // In vanilla MC, one shot fall damage is about 25 blocks
        // We make fall damage a little more lenient here though, so let's let people fall about 33 blocks
        // Higher distance = more damage
        double ONE_SHOT_DISTANCE = 33.0;  // How many blocks can we fall to one shot us
        double SAFE_DISTANCE = 5.0;  // How many blocks can we fall with no fall damage

        // First if distance is less than safe, then no damage
        if (distance <= SAFE_DISTANCE)
            return 0;

        // Since distance is > SAFE, calculate a clamped percent between SAFE and ONE SHOT
        double percent = (distance - SAFE_DISTANCE) / (ONE_SHOT_DISTANCE-SAFE_DISTANCE);
        // Higher percent means we take more fall damage, so return this multiplied with their max hp
        return maxHP * percent;

    }

    // Very first thing that happens when anything and everything takes damage from any source
    @EventHandler(priority = EventPriority.LOW)
    public void onEntityBaseDamage(EntityDamageEvent event) {

        double baseDamage = 0;
        double entityHP;
        double entityMaxHP = -1.0;

        if (event.getEntity() instanceof Attributable && event.getEntity() instanceof Damageable) {
            entityHP = ((Damageable) event.getEntity()).getHealth();
            entityMaxHP = ((Attributable) event.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        }

        // If for some reason they dont have a max HP, send a warning
        if (entityMaxHP == -1.0) {
            MMOCraft.getInstance().getLogger().warning("Entity " + event.getEntity().getType() + " did not have maxHP attribute?");
            return;
        }

        // First we need to setup the base amount of damage for this event for certain reasons we could have taken damage
        switch (event.getCause()) {

            // Ignored cases, this is handled from entity vs entity damage event
            case THORNS:
            case ENTITY_ATTACK:
            case PROJECTILE:
            case FALLING_BLOCK:
            case ENTITY_EXPLOSION:
            case DRAGON_BREATH:
            case ENTITY_SWEEP_ATTACK:
            case MAGIC:
            // Ignored cases that we just don't need to do anything for
            case SUICIDE:
            case CUSTOM:
            case BLOCK_EXPLOSION:  // later come up with distance logic
                return;

            // If we take fall damage
            case FALL:
            case FLY_INTO_WALL:  // maybe take another look at this
                baseDamage = this.calculateBaseFallDamage(event.getEntity().getFallDistance(), entityMaxHP);
                break;
            // If we take heat based damage, (or just small ticking stuff)
            case FIRE:
            case WITHER:
                baseDamage = .10 * entityMaxHP;  // being IN fire means 10% of max hp
                break;

            case FIRE_TICK:
            case DRYOUT:
            case POISON:
            case HOT_FLOOR:
            case FREEZE:
            case STARVATION:
            case DROWNING:
            case MELTING:
            case CONTACT:
            case SUFFOCATION:
                baseDamage = .05 * entityMaxHP;  // Being ON fire means 5% of max hp
                break;

            case LAVA:
            case CRAMMING:
                baseDamage = .20 * entityMaxHP;  // Being in lava means 20% of max hp
                break;

            case LIGHTNING:
                baseDamage = .60 * entityMaxHP;  // hit by lightning 60%
                break;

            // Void is a weird one, if the damage is enough to one shot us, let it.
            // Otherwise, do 30% damage that is unresistable
            case VOID:
                if (event.getFinalDamage() > entityMaxHP)
                    baseDamage = event.getFinalDamage();
                else
                    baseDamage = .30 * entityMaxHP;

                break;
        }

        // Cancel all base minecraft modifiers, we don't use them
        // Seems this is deprecated, maybe find another way
        for (EntityDamageEvent.DamageModifier dm : EntityDamageEvent.DamageModifier.values())
            if (event.getDamage(dm) > 0)
                event.setDamage(dm ,0);

        // Set base damage of this event
        event.setDamage(baseDamage);

        // todo: delete this when we are sure everything works as intended
        event.getEntity().sendMessage(String.format(ChatColor.RED + "[%s] BASE DAMAGE: %s", event.getCause(), (int)baseDamage));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityBaseDamageEntity(EntityDamageByEntityEvent event) {

        // If this isn't entity vs entity don't care
        if (!EventHelpers.isEntityVsEntityCause(event.getCause()))
            return;

        // Same as above, but we need to handle the cases where an entity is damaging another entity
        // This is where outgoing damage stats are considered from the one doing the attacking
        // First get both of the entities involved stats
        MMOEntity gotHit = EntityManager.getInstance().getMMOEntity(event.getEntity());
        MMOEntity dealtTheHit = EntityManager.getInstance().getMMOEntity(event.getDamager());

        double baseDamage = 0;

        // First we grab the base damage stat of the person who dealt the hit
        baseDamage += dealtTheHit.getStatPool().getStatistic(StatisticType.DAMAGE).getTotalValue();
        // Now we add bonus damage for their strength stat
        StatisticStrength strengthStat = (StatisticStrength) dealtTheHit.getStatPool().getStatistic(StatisticType.STRENGTH);
        double strengthMultiplier = strengthStat.getBonusDamageMultiplier();
        baseDamage *= strengthMultiplier;

        // Update the damage
        event.setDamage(baseDamage);

        // todo remove this debug when it works 100%
        gotHit.getEntity().sendMessage(ChatColor.RED + String.format("[%s] ENEMY DMG/STR: %d/%d BASE DMG: %d", event.getCause(), dealtTheHit.getStatPool().getStatistic(StatisticType.DAMAGE).getTotalValue(), strengthStat.getTotalValue(), (int)baseDamage));
        dealtTheHit.getEntity().sendMessage(ChatColor.GREEN + String.format("[%s] YOUR DMG/STR: %d/%d BASE DMG: %d", event.getCause(), dealtTheHit.getStatPool().getStatistic(StatisticType.DAMAGE).getTotalValue(), strengthStat.getTotalValue(), (int)baseDamage));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onConsiderCriticalHit(EntityDamageByEntityEvent e) {

        // Think of this as an extension to the method above, now that base damage is calculated we should
        // Figure out if the hit should be a critical hit
        // Again, don't care if this isn't entity vs entity
        if (!EventHelpers.isEntityVsEntityCause(e.getCause()))
            return;

        MMOEntity dealtTheHit = EntityManager.getInstance().getMMOEntity(e.getDamager());
        StatisticCriticalChance critChance = (StatisticCriticalChance) dealtTheHit.getStatPool().getStatistic(StatisticType.CRITICAL_CHANCE);

        // Let's roll it
        if (!critChance.roll())
            return;  // Failed, better luck next time

        StatisticCriticalDamage critDamage = (StatisticCriticalDamage)dealtTheHit.getStatPool().getStatistic(StatisticType.CRITICAL_DAMAGE);

        e.setDamage(e.getDamage() * critDamage.getDamageMultiplier());
        ParticlesHelper.doCriticalHitEffect(e.getEntity(), critDamage.getTotalValue() / 15);

        //todo delete debug when system works
        dealtTheHit.getEntity().sendMessage(String.format(ChatColor.AQUA + "[%s] CRITICAL! DMG: %d (+%d%%)", e.getCause(), (int)e.getDamage(), critDamage.getTotalValue()));

    }

    // Now that base damage is calculated, let's take into account defense
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDefendedDamage(EntityDamageEvent e) {

        MMOEntity gotHit = EntityManager.getInstance().getMMOEntity(e.getEntity());
        StatisticDefense defense = (StatisticDefense) gotHit.getStatPool().getStatistic(StatisticType.DEFENSE);
        double baseDamage = e.getDamage();

        // Multiply the damage by how much we should take from it
        baseDamage *= defense.getPercentDamageTaken(e.getCause());

        // Update the damage in the event
        e.setDamage(baseDamage);

        // todo: remove debug when all this works
        e.getEntity().sendMessage(String.format(ChatColor.DARK_GREEN + "[%s] YOUR DEF: %s (-%.2f%%) DMG NOW: %d", e.getCause(), defense.getTotalValue(), defense.getResistPercent(e.getCause())*100, (int)baseDamage));

    }

}
