package me.devvy.mmocraft.experience;


import me.devvy.mmocraft.MMOCraft;
import me.devvy.mmocraft.entity.MMOEntity;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;

// An experience instance where we have a level, current xp, and xp to next level
// Also in charge of saving data to the persistent data container of who this experience instance belongs to
public class Experience {

    public static final int MAX_LEVEL = 100;

    private int level = 1;
    private int xp = 0;
    private int xpNeeded = getXpNeededForLevel(level+1);

    private MMOEntity owner;

    // Init with values read from the PersistentExperienceContainer
    public Experience(int level, int xp, int totalXpNeeded) {
        this.level = level;
        this.xp = xp;
        this.xpNeeded = totalXpNeeded;
    }

    // Init with default values, used when experience instance is not found
    public Experience(MMOEntity owner) {
        this.owner = owner;
    }

    public MMOEntity getOwner() {
        return owner;
    }

    public void setOwner(MMOEntity entity) {
        this.owner = entity;
    }

    public int getLevel() {
        return level;
    }

    // Forces experience instance to a certain level
    public void forceLevel(int levelToSet) {
        this.level = levelToSet;
        this.xp = 0;
        this.xpNeeded = getXpNeededForLevel(level+1);
        display();
    }

    public int getXp() {
        return xp;
    }

    public int getTotalXpNeeded() {
        return xpNeeded;
    }

    public int getXpNeededToLevel() {
        //return Math.max(0, xpNeeded - xp);  // dont show overflow
        return xpNeeded - xp;  // Show overflow
    }

    // Returns the amount of xp needed for level n
    public int getXpNeededForLevel(int n) {
        // todo better leveling formula
        return 5 * n + (20+n);
    }

    // Call this to set all internal values to their defaults, and reset the persistent data container
    // to its default state. This should only be called for first time player joins, and prestiging if it is a thing
    public void reset() {
        // Default values
        this.level = 1;
        this.xp = 0;
        this.xpNeeded = getXpNeededForLevel(level+1);
        display();
    }

    // What should we do when we level up in this experience instance? this is where we would do special things
    // like send messages or throw fireworks
    public void levelUp() {
        // First backend stuff we need to track, increment the level, reset xp to 0, and update xp needed for next
        this.level += 1;
        this.xp = 0;
        this.xpNeeded = getXpNeededForLevel(level+1);

        // Here we can put logic for anything special we want to do when we level up
        if (owner.getEntity() instanceof Player)
            owner.getEntity().sendMessage("level up! " + (level-1) + " -> " + level);
    }

    // helper method to add experience
    private void addExperience(int amount) {

        if (amount < 0)
            throw new IllegalArgumentException("Somehow amount of XP was negative, this is dangerous.");

        // Add the xp
        this.xp += amount;

        // Have we leveled up?
        if (level < MAX_LEVEL && this.xp >= this.xpNeeded) {
            // Grab overflow so we can give ourselves the xp left over when leveling up
            int overflow = this.xp - this.xpNeeded;
            levelUp();  // This will increment level, reset xp to 0 and xpneeded to whatever we need for the next level
            // Now call this method again with the leftover xp
            addExperience(overflow);
            return;
        }

        // Show final state of xp, for example if it's a player we may want to display their level and xp progress
        display();
    }

    // Call to give experience
    public void gainExperience(int amount) {
        addExperience(amount);
    }

    // What should we do to display this level/xp instance?
    public void display() {
        if (owner.getEntity() instanceof Player) {
            float progress = (float) xp / (float) xpNeeded;
            progress = (float) Math.min(progress, 0.99999);
            ((Player) owner.getEntity()).setLevel(level);
            ((Player) owner.getEntity()).setExp(progress);
        }
    }
}
