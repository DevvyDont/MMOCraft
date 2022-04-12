package me.devvy.mmocraft.player;

import me.devvy.mmocraft.entity.MMOEntity;
import me.devvy.mmocraft.items.ItemData;
import me.devvy.mmocraft.items.ItemManager;
import me.devvy.mmocraft.items.ItemType;
import me.devvy.mmocraft.stats.BaseStatistic;
import me.devvy.mmocraft.stats.StatisticPool;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

// Contains a reference to a spigot player, and any extra functionality needed
public class MMOPlayer extends MMOEntity {

    public MMOPlayer(Entity entity) {
        super(entity);
        // If we didn't get an entity, something really wrong happened on the dev's end
        assert entity instanceof Player;

        considerHeldItemsStatistics();
        considerArmorStatistics();
    }

    public Player getSpigotPlayer(){
        // Safe cast, from constructor we know that the entity is a player
        return (Player) getEntity();
    }

    @Override
    public StatisticPool getDefaultBaseStats() {

        StatisticPool statPool = super.getDefaultBaseStats();

        // Differently from a normal entity, we actually already have defaults defined in stat enums
        for (BaseStatistic statistic : statPool.getAllStatistics())
            statistic.setBaseValue(statistic.getType().DEFAULT);

        return statPool;

    }

    // With the nature of how our plugin works, only players are affected by armor.
    public void considerArmorStatistics() {

        // clear bonuses of armor
        getStatPool().clearBonusesOfType("HELMET");
        getStatPool().clearBonusesOfType("CHESTPLATE");
        getStatPool().clearBonusesOfType("LEGGINGS");
        getStatPool().clearBonusesOfType("BOOTS");

        // Loop through all the item slots on the player
        for (ItemStack armor : getSpigotPlayer().getInventory().getArmorContents())
            // If the armor is an actual item,
            if (armor != null && armor.hasItemMeta()) {

                // Grab the custom data on the armor
                ItemData itemData = ItemManager.getInstance().getCustomItemData(armor);
                // Get the stats of the armor
                StatisticPool armorStats = itemData.getStats();
                // Loop through all the statistics on the armor's stat pool
                for (BaseStatistic statistic : armorStats.getAllStatistics())
                    // Update the player's bonus stats for this specific type of armor's slot
                    getStatPool().getStatistic(statistic.getType()).addBonus(itemData.getCustomItemType().toString(), statistic.getTotalValue());

            }

        applyStats();

    }

    // Similarly to armor, we only let players have their stats affected by held items
    public void considerHeldItemsStatistics() {

        // Clear bonuses of what we are holding
        getStatPool().clearBonusesOfType("MAINHAND");
        getStatPool().clearBonusesOfType("OFFHAND");

        double OFFHAND_NERF = .20;  // Nerfs the stats of what theyre holding in their offhand
        // Grab what the player is holding in both hands // TODO this might be op, since we can hold two items
        ItemStack main = getSpigotPlayer().getInventory().getItemInMainHand();
        ItemStack offhand = getSpigotPlayer().getInventory().getItemInOffHand();

        // If we should even worry about what's in their hand,
        if (main.hasItemMeta()) {
            // Get the stats
            ItemData itemData = ItemManager.getInstance().getCustomItemData(main);

            // Loop through all the item's stats
            for (BaseStatistic stat : itemData.getStats().getAllStatistics())
                // If this stat is ok to add to our bonuses
                if (!ItemType.ignoreThisStatIfHeld(itemData.getCustomItemType(), stat.getType()))
                    // Update the bonus from mainhand
                    getStatPool().getStatistic(stat.getType()).addBonus("MAINHAND", stat.getTotalValue());
        }

        // Now the same thing for the offhand
        if (offhand.hasItemMeta()) {
            // Get the stats
            ItemData itemData = ItemManager.getInstance().getCustomItemData(offhand);
            // Loop through all the item's stats
            for (BaseStatistic stat : itemData.getStats().getAllStatistics())
                if (!ItemType.ignoreThisStatIfHeld(itemData.getCustomItemType(), stat.getType()))
                    // Update the bonus from mainhand
                    getStatPool().getStatistic(stat.getType()).addBonus("OFFHAND", (int) (stat.getTotalValue()*OFFHAND_NERF));
        }

        applyStats();

    }
}
