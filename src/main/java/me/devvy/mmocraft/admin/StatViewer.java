package me.devvy.mmocraft.admin;

import me.devvy.mmocraft.MMOCraft;
import me.devvy.mmocraft.entity.EntityManager;
import me.devvy.mmocraft.entity.MMOEntity;
import me.devvy.mmocraft.stats.BaseStatistic;
import me.devvy.mmocraft.stats.StatisticPool;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

// Used to that when an admin right clicks on an entity, you can see its stats
public class StatViewer implements Listener {

    public StatViewer() {
        MMOCraft.getInstance().getServer().getPluginManager().registerEvents(this, MMOCraft.getInstance());
    }

    @EventHandler
    public void onRightClickEntity(PlayerInteractEntityEvent event) {

        if (!event.getPlayer().isOp())
            return;

        MMOEntity entityClicked = EntityManager.getInstance().getMMOEntity(event.getRightClicked());
        StatisticPool stats = entityClicked.getStatPool();

        event.getPlayer().sendMessage("------------------------------");
        event.getPlayer().sendMessage("Stats for " + entityClicked.getEntity().getName());
        event.getPlayer().sendMessage("");
        event.getPlayer().sendMessage("Level: " + entityClicked.getLevelExperience().getLevel());
        int xp = entityClicked.getLevelExperience().getXp();
        int xpNeed = entityClicked.getLevelExperience().getTotalXpNeeded();
        int need = entityClicked.getLevelExperience().getXpNeededToLevel();
        float ratio = (float) xp / (float) xpNeed;
        int iratio = (int)(Math.floor(ratio*100));
        event.getPlayer().sendMessage("XP Progress: " + xp + "/" + xpNeed + " - " + need + "XP to go (" + iratio + "%)");
        event.getPlayer().sendMessage("");
        for (BaseStatistic stat : stats.getAllStatistics(true))
            event.getPlayer().sendMessage(stat.getType().toString() + ": " + stat.getTotalValue());
        event.getPlayer().sendMessage("------------------------------");

    }

}
