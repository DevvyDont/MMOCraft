package me.devvy.mmocraft.util;

import org.bukkit.*;
import org.bukkit.entity.Entity;

public class ParticlesHelper {

    public static void doCriticalHitEffect(Entity entityHit, int count) {
        Location loc = entityHit.getLocation();
        loc.getWorld().spawnParticle(Particle.CRIT, loc, count);
        loc.getWorld().playSound(loc, Sound.ENTITY_PLAYER_ATTACK_CRIT, 1, 1);
    }

    public static void doCriticalShootEffect(Entity entityShot) {

    }

}
