package me.devvy.mmocraft.listeners;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import me.devvy.mmocraft.MMOCraft;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

// Any time we pickup an xp orb, we don't actually update the players xp as this is only done with the plugin.
// Instead, let's give some sort of currency or something
public class PickupXPOrbListener implements Listener {

    public PickupXPOrbListener() {
        // Register listeners
        MMOCraft.getInstance().getServer().getPluginManager().registerEvents(this, MMOCraft.getInstance());
    }

    @EventHandler
    public void onPickupXPOrb(PlayerPickupExperienceEvent event) {
        int amount = event.getExperienceOrb().getExperience();
        event.getExperienceOrb().setExperience(0);
        MMOCraft.getInstance().getLogger().info(String.format("%s picked up %d worth of XP orb. Later, we should make this add to some currency or something", event.getPlayer().getName(), amount));
    }
}
