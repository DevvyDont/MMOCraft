package me.devvy.mmocraft.items;

import me.devvy.mmocraft.MMOCraft;
import me.devvy.mmocraft.storage.ItemAttributes;
import me.devvy.mmocraft.storage.ItemAttributesStorage;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class ItemManager implements Listener {

    private static ItemManager INSTANCE;


    public static ItemManager getInstance() {
        return INSTANCE;
    }

    public ItemManager() {
        INSTANCE = this;
        MMOCraft.getInstance().getServer().getPluginManager().registerEvents(this, MMOCraft.getInstance());
    }

    // Generates a fresh new item with given ID from the spreadsheet with default attributes
    public ItemStack generateCustomItem(String uniqueIdentifier, int amount) {
        ItemAttributes attributes = ItemAttributesStorage.getItemAttributes(uniqueIdentifier);

        // Make a new itemstack of given type
        ItemStack itemStack = new ItemStack(attributes.MATERIAL, amount);

        // Force the game to transform our item with the attributes on file
        ItemData mmoItemData = new ItemData(itemStack, attributes);
        mmoItemData.update(itemStack);

        // return the item
        return itemStack;
    }

    public ItemStack generateCustomItem(String uniqueIdentifier) {
        return generateCustomItem(uniqueIdentifier, 1);
    }

    // Updates an existing item stack's attributes based on what's on file
    // As of now, data on items will always be kept in sync with what is on file
    // so we can make balance changes
    // If anything is going to be stored on the item that is unique to when it is generated,
    // then a new field needs to be made in ItemData that isn't controlled by ItemAttributes
    public ItemData getCustomItemData(ItemStack item) {
        // Runs default protocol for generating custom item data
        return new ItemData(item);
    }

    /*
    START ANY EVENTS WE NEED IN ORDER TO MAKE SURE ALL BASE ITEMS IN THE GAME ARE INSTANCES OF OUR MMO ITEMS
     */

    public void fixItemEntity(Item item, int amount) {
        ItemStack is = item.getItemStack();
        ItemData data = getCustomItemData(is);
        item.setItemStack(is);
        String amountPrefix = amount > 1 ? amount + "x " : "";
        item.customName(Component.text(data.getRarity().CHAT_COLOR.toString() + amountPrefix + data.getName()));

        // If this is rare or better, show the name of the item
        if (data.getRarity().greaterThan(ItemRarity.UNCOMMON))
            item.setCustomNameVisible(true);

        // If this is legendary or better, make it glow
        if (data.getRarity().greaterThan(ItemRarity.RARE))
            item.setGlowing(true);

        // Never let mobs pick up our shit
        item.setCanMobPickup(false);
    }

    // Handle the case where an item has spawned in the world
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemEntitySpawn(ItemSpawnEvent event) {
        fixItemEntity(event.getEntity(), event.getEntity().getItemStack().getAmount());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemMerge(ItemMergeEvent event) {
        fixItemEntity(event.getTarget(), event.getTarget().getItemStack().getAmount()+event.getEntity().getItemStack().getAmount());
    }

    // Handle the case where a new inventory was opened
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryOpened(InventoryOpenEvent event) {

        // Loop through all the items and make sure they are valid
        for (ItemStack itemStack : event.getInventory().getContents())
            if (itemStack != null && itemStack.getType() != Material.AIR)
                getCustomItemData(itemStack);

    }

    // When a player joins validate their inventory
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        // Loop through all the items and make sure they are valid
        for (ItemStack itemStack : event.getPlayer().getInventory().getContents())
            if (itemStack != null && itemStack.getType() != Material.AIR)
                getCustomItemData(itemStack);

    }

    // Make sure anything that shows up in a crafting table is fixed
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCraftResultShow(PrepareItemCraftEvent event) {

        if (event.getInventory().getResult() != null)
            if (event.getInventory().getResult().getType() != Material.AIR)
                getCustomItemData(event.getInventory().getResult());
    }

    // Make sure items that we pickup are valid
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPickupItem(EntityPickupItemEvent event) {
        if (event.getItem().getItemStack().getType() != Material.AIR)
            getCustomItemData(event.getItem().getItemStack());
    }

    // Make sure items that get smelted are valid
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFurnaceSmelt(FurnaceSmeltEvent event){
        if (event.getResult().getType() != Material.AIR)
            getCustomItemData(event.getResult());
    }

    // Do not let enchanted blocks be placed, it's pointless
    @EventHandler
    public void onItemPlace(BlockPlaceEvent e) {
        if (e.getItemInHand().getEnchantments().size() > 0)
            e.setCancelled(true);
    }


}
