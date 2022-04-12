package me.devvy.mmocraft.listeners;

import me.devvy.mmocraft.MMOCraft;
import me.devvy.mmocraft.entity.EntityManager;
import me.devvy.mmocraft.entity.MMOEntity;
import me.devvy.mmocraft.entity.MMOEntityCreature;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class CreatureNametagUpdater implements Listener {

    public CreatureNametagUpdater() {
        MMOCraft.getInstance().getServer().getPluginManager().registerEvents(this, MMOCraft.getInstance());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCreatureDamage(EntityDamageEvent e) {

        // We don't care about things that aren't creatures with nametags showing HP
        MMOEntity me = EntityManager.getInstance().getMMOEntity(e.getEntity());
        if (!(me instanceof MMOEntityCreature))
            return;

        MMOEntityCreature creature = (MMOEntityCreature) me;
        creature.updateNametag((int) (e.getFinalDamage() * -1));
    }
}
