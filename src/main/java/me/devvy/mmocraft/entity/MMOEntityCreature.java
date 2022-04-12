package me.devvy.mmocraft.entity;

import me.devvy.mmocraft.MMOCraft;
import me.devvy.mmocraft.util.StringFormatting;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;

public class MMOEntityCreature extends MMOEntity {

    public static final NamespacedKey CREATURE_NAME_KEY = new NamespacedKey(MMOCraft.getInstance(), "creature_name");

    private String creatureName = null;

    public MMOEntityCreature(Entity entity) {
        super(entity);

        // If they have a custom name defined in their container, load it
        if (entity.getPersistentDataContainer().has(CREATURE_NAME_KEY))
            creatureName = entity.getPersistentDataContainer().get(CREATURE_NAME_KEY, PersistentDataType.STRING);

        updateNametag();
    }

    private ChatColor getHealthColor(int hp, int max) {

        float ratio = (float)hp / (float)max;

        if (ratio > .7)
            return ChatColor.GREEN;

        if (ratio > .45)
            return ChatColor.YELLOW;

        if (ratio > .2)
            return ChatColor.GOLD;

        if (ratio > 0)
            return ChatColor.DARK_RED;

        return ChatColor.DARK_GRAY;
    }

    public String getNametagText() {
        return getNametagText(0);
    }

    public String getNametagText(int hpOffset) {

        String cleanName = getCreatureName();

        if (!(getEntity() instanceof Damageable))
            return String.format("f%s[Lvl %d] %s%s", ChatColor.GRAY,  getLevelExperience().getLevel(), ChatColor.WHITE, cleanName);

        int hp = (int) Math.ceil(((Damageable) getEntity()).getHealth());
        int max = (int) Math.ceil(((Damageable) getEntity()).getMaxHealth());
        hp += hpOffset;
        //todo uncomment below to make look cleaner
        //hp = Math.max(0, hp);  // Don't show anything less than 0
        return String.format("%s[Lvl %d] %s%s %s%d %s❤", ChatColor.GRAY, getLevelExperience().getLevel(), ChatColor.RED, cleanName, getHealthColor(hp, max), hp, ChatColor.RED);
    }

    public void updateNametag() {
        updateNametag(0);
    }

    // Call to update the nametag but with a certain amount of HP docked
    public void updateNametag(int hpOffset) {
        getEntity().customName(Component.text(getNametagText(hpOffset)));
        getEntity().setCustomNameVisible(true);
    }

    public String getCreatureName() {
        if (creatureName == null)
            return StringFormatting.cleanEnumName(getEntity().getType().toString());

        return creatureName;
    }

    // Updates the name of this creature to display on their nametag,
    // If their name isn't defined, we default to their minecraft type name
    public void setCreatureName(String name) {
        // Update the instance name
        creatureName = name;
        updateNametag();

        // Store the name in their container
        getEntity().getPersistentDataContainer().set(CREATURE_NAME_KEY, PersistentDataType.STRING, name);
    }
}
