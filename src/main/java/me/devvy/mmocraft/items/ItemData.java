package me.devvy.mmocraft.items;

import me.devvy.mmocraft.MMOCraft;
import me.devvy.mmocraft.experience.Experience;
import me.devvy.mmocraft.stats.BaseStatistic;
import me.devvy.mmocraft.stats.PersistentBaseStatisticContainer;
import me.devvy.mmocraft.stats.StatisticPool;
import me.devvy.mmocraft.storage.ItemAttributes;
import me.devvy.mmocraft.storage.ItemAttributesStorage;
import me.devvy.mmocraft.util.StringFormatting;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static me.devvy.mmocraft.experience.PersistentExperienceContainer.EXPERIENCE_CONTAINER;
import static me.devvy.mmocraft.experience.PersistentExperienceContainer.EXPERIENCE_CONTAINER_KEY;
import static org.bukkit.persistence.PersistentDataType.INTEGER;
import static org.bukkit.persistence.PersistentDataType.STRING;

// An interface to make reading and writem to item stacks extremely easy
public class ItemData {

    // Any keys we need to define
    public static final NamespacedKey UNIQUE_ID = new NamespacedKey(MMOCraft.getInstance(), "unique_id");
    public static final NamespacedKey RARITY_KEY = new NamespacedKey(MMOCraft.getInstance(), "rarity");
    public static final NamespacedKey ITEM_TYPE_KEY = new NamespacedKey(MMOCraft.getInstance(), "item_type");
    public static final NamespacedKey NAME_KEY = new NamespacedKey(MMOCraft.getInstance(), "name");
    public static final NamespacedKey REQ_LEVEL_KEY = new NamespacedKey(MMOCraft.getInstance(), "level_req");

    // The codename of this item, this is what name would be stored in the items.csv spreadsheet for lookup
    // This is used so we can determine things stored there
    // If an item isn't custom, we default to vanilla minecraft material type
    private String uniqueItemID;
    // A stat pool on this item
    private StatisticPool stats;
    // An xp system on this item
    private Experience experience;
    // the level requirement to use this item
    private int levelRequirement;
    // The rarity of this item
    private ItemRarity rarity;
    // If we should show glint
    private boolean glint;
    // The type of item this is
    private ItemType customItemType;
    // The name of the item, just incase we want to display any fancy text in the display name
    private String name;
    // Some text that displays
    private String description;

    // If this item should not be allowed to be used in vanilla recipes
    private boolean ignoreVanillaCrafting;

    // Pass in an itemstack, this instance will initialize based off of the data stored on this item stack
    // If the data doesn't exist, we use the default ItemAttributes based off of the bukkit material
    // Custom items should always call the constructor that has attributes already set
    public ItemData(ItemStack item) {
        loadContainers(item);
    }

    // Pass in item along with attributes already, this should only be called when we are generating a completely
    // new item custom item 
    public ItemData(ItemStack item, ItemAttributes attributes) {
        item.setType(attributes.MATERIAL);
        this.uniqueItemID = attributes.ID_NAME;
        this.experience = getDefaultExperience();
        this.stats = attributes.constructStatPool();
        this.rarity = attributes.RARITY;
        this.glint = attributes.GLINT;
        this.customItemType = attributes.CUSTOM_TYPE;
        this.name = attributes.DISPLAY_NAME;
        this.levelRequirement = attributes.LEVEL_REQUIREMENT;

        this.description = attributes.DESCRIPTION;

        this.ignoreVanillaCrafting = attributes.IGNORE_VANILLA_RECIPES;

        // Now that all data is set, instead of loading from container we just straight save first
        saveContainers(item, item.getItemMeta());
    }

    private Experience getDefaultExperience() {
        return new Experience(1, 0, 50);
    }

    // Attempts to set data from what is stored on the item
    // If it doesn't exist, we create it
    // Here we would only initialize stuff that isn't loaded from the spreadsheet, such as timestamps, unique IDs, etc
    public void loadContainers(ItemStack item) {

        // Here load any data that isn't tracked on the spreadsheet/static, like timestamps or unique IDs
        experience = item.getItemMeta().getPersistentDataContainer().getOrDefault(EXPERIENCE_CONTAINER_KEY, EXPERIENCE_CONTAINER, getDefaultExperience());
        uniqueItemID = item.getItemMeta().getPersistentDataContainer().getOrDefault(UNIQUE_ID, STRING, item.getType().toString());

        // Updates data from what's on the spreadsheet
        update(item);
    }
    
    // Based on whatever the custom ID of this itemstack has, updates attributes that are able to be modified
    // From the spreadsheet. I.e., if we change the stats of an item in the spreadsheet, update the container
    // In this item stack to reflect that.
    public void update(ItemStack itemStack) {
        
        // Check if there are registered attributes for this item
        if (ItemAttributesStorage.itemRegistered(uniqueItemID))
            updateFromAttributes(itemStack);
        else
            updateFromDefaults(itemStack);

        saveContainers(itemStack, itemStack.getItemMeta());
        updateTitle(itemStack);
        updateLore(itemStack);
    }
    
    // Given an item stack, update this items data based on what is stored in the spreadsheet, assume that the item is registered in the spreadsheet
    private void updateFromAttributes(ItemStack itemStack) {
        ItemAttributes attributes = ItemAttributesStorage.getItemAttributes(uniqueItemID);
        
        // Setup all variables needed
        itemStack.setType(attributes.MATERIAL);
        this.levelRequirement = attributes.LEVEL_REQUIREMENT;
        this.stats = attributes.constructStatPool();
        this.rarity = attributes.RARITY;
        this.glint = attributes.GLINT;
        this.customItemType = attributes.CUSTOM_TYPE;
        this.name = attributes.DISPLAY_NAME;
        this.description = attributes.DESCRIPTION;
        this.ignoreVanillaCrafting = attributes.IGNORE_VANILLA_RECIPES;
    }
    
    // Given an item stack, update this items data based on default attributes of items. This means that the item
    // is nothing special and is not defined at all in the spreadsheet. Usually this means this is some item that is
    // not special like a minecraft stick or something, essentially a COMMON rarity material
    private void updateFromDefaults(ItemStack itemStack) {
        
        // Setup all variables needed
        this.levelRequirement = 1;
        this.stats = new StatisticPool();  // zeroed out stats
        this.rarity = ItemRarity.COMMON;
        this.glint = false;
        this.customItemType = ItemType.ITEM;
        this.name = StringFormatting.cleanEnumName(itemStack.getType().name());
        this.description = "";
        this.ignoreVanillaCrafting = false;
    }

    // Saves the item attributes to the itemstack to save permanently
    public void saveContainers(ItemStack item, ItemMeta meta) {
        // Given a modified item meta, save it to the item stack we passed in
        meta.getPersistentDataContainer().set(UNIQUE_ID, STRING, uniqueItemID);
        meta.getPersistentDataContainer().set(EXPERIENCE_CONTAINER_KEY, EXPERIENCE_CONTAINER, experience);
        meta.getPersistentDataContainer().set(PersistentBaseStatisticContainer.BASE_STATISTIC_CONTAINER_KEY, PersistentBaseStatisticContainer.BASE_STATISTIC_CONTAINER, stats);
        meta.getPersistentDataContainer().set(RARITY_KEY, INTEGER, rarity.ordinal());
        meta.getPersistentDataContainer().set(ITEM_TYPE_KEY, INTEGER, customItemType.ordinal());
        meta.getPersistentDataContainer().set(NAME_KEY, STRING, name);
        meta.getPersistentDataContainer().set(REQ_LEVEL_KEY, INTEGER, levelRequirement);
        item.setItemMeta(meta);
    }

    // Update the title of the item to what it should display
    public void updateTitle(ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        String lvlPrefix = levelRequirement > 1 ? String.format("%s[Lv. %s] ",ChatColor.DARK_GRAY, levelRequirement) : "";
        meta.displayName(Component.text(lvlPrefix + rarity.CHAT_COLOR.toString() + this.name));

        item.setItemMeta(meta);
    }

    // Refreshes the lore of this item based on the data we have to work with
    public void updateLore(ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        List<Component> newLoreLines = new ArrayList<>();

        // First the stats this item has, loop over all the stats that aren't zero
        Collection<BaseStatistic> nonZeroStats = stats.getAllNonzeroStatistics();
        for (BaseStatistic statistic : nonZeroStats) {
            String posOrNeg = statistic.getTotalValue() > 0 ? "+" : "-";
            ChatColor numColor = statistic.getTotalValue() > 0 ? ChatColor.GREEN : ChatColor.RED;
            // Add the line
            newLoreLines.add(Component.text(ChatColor.GRAY + StringFormatting.cleanEnumName(statistic.getType().toString()) + ": " + numColor + posOrNeg + statistic.getTotalValue()));
        }

        // If there were stats, add an extra line break after the stats to make it look cleaner
        if (!nonZeroStats.isEmpty())
            newLoreLines.add(Component.text(""));

        if (getDescription().length() > 0) {
            newLoreLines.add(Component.text(ChatColor.translateAlternateColorCodes('&', getDescription())));
            newLoreLines.add(Component.text(""));
        }

        // If there is any other data we need add it here

        // Add the rarity of the item
        newLoreLines.add(Component.text(rarity.CHAT_COLOR.toString() + ChatColor.BOLD + rarity + " " + customItemType));

        // Update the lore
        meta.lore(newLoreLines);

        // Now some things to make our stuff look clean or any attributes we need
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        if (glint) {
            meta.addEnchant(Enchantment.DURABILITY, 1, false);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        item.setItemMeta(meta);
    }

    public StatisticPool getStats() {
        return stats;
    }

    public Experience getExperience() {
        return experience;
    }

    public int getLevelRequirement() {
        return levelRequirement;
    }

    public String getUniqueItemID() {
        return uniqueItemID;
    }

    public boolean isIgnoreVanillaCrafting() {
        return ignoreVanillaCrafting;
    }

    public ItemRarity getRarity() {
        return rarity;
    }

    public ItemType getCustomItemType() {
        return customItemType;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
