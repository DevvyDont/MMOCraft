package me.devvy.mmocraft.items;

import org.bukkit.ChatColor;

public enum ItemRarity {

    COMMON(ChatColor.GRAY),
    UNCOMMON(ChatColor.GREEN),
    RARE(ChatColor.BLUE),
    EPIC(ChatColor.DARK_PURPLE),
    LEGENDARY(ChatColor.GOLD),
    MYTHIC(ChatColor.LIGHT_PURPLE),
    ARTIFACT(ChatColor.RED),
    DIVINE(ChatColor.AQUA);


    public final ChatColor CHAT_COLOR;

    ItemRarity(ChatColor chatColor) {
        this.CHAT_COLOR = chatColor;
    }

    public boolean greaterThan(ItemRarity other) {
        return this.ordinal() > other.ordinal();
    }

    public boolean lessThan(ItemRarity other) {
        return this.ordinal() < other.ordinal();
    }

}


