package me.devvy.mmocraft.experience;

import me.devvy.mmocraft.MMOCraft;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class PersistentExperienceContainer implements PersistentDataType<int[], Experience> {

    public static final PersistentExperienceContainer EXPERIENCE_CONTAINER = new PersistentExperienceContainer();
    public static final NamespacedKey EXPERIENCE_CONTAINER_KEY = new NamespacedKey(MMOCraft.getInstance(), "experience");

    private PersistentExperienceContainer(){};

    @Override
    public @NotNull Class<int[]> getPrimitiveType() {
        return int[].class;
    }

    @Override
    public @NotNull Class<Experience> getComplexType() {
        return Experience.class;
    }

    @Override
    public int @NotNull [] toPrimitive(@NotNull Experience complex, @NotNull PersistentDataAdapterContext context) {
        // Return the data stored in the class
        return new int[] {
                complex.getLevel(),
                complex.getXp(),
                complex.getTotalXpNeeded()
        };
    }

    @Override
    public @NotNull Experience fromPrimitive(int @NotNull [] primitive, @NotNull PersistentDataAdapterContext context) {
        // Construct the class from the integer array stored
        int level = primitive[0];
        int xp = primitive[1];
        int xpToNext = primitive[2];
        return new Experience(level, xp, xpToNext);
    }
}
