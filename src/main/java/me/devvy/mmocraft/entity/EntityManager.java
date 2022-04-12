package me.devvy.mmocraft.entity;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import me.devvy.mmocraft.MMOCraft;
import me.devvy.mmocraft.player.MMOPlayer;
import me.devvy.mmocraft.stats.StatisticPool;
import me.devvy.mmocraft.stats.StatisticType;
import me.devvy.mmocraft.storage.EntityAttributes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attributable;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// In charge of mapping/linking base minecraft entities to extra functionality classes/instances
public class EntityManager implements Listener {

    private static EntityManager INSTANCE;

    public static EntityManager getInstance() {
        return INSTANCE;
    }

    // Map entity UUIDs to entity instance
    private final Map<UUID, MMOEntity> mmoEntityMap;

    public EntityManager() {
        INSTANCE = this;  // singleton hacks

        mmoEntityMap = new HashMap<>();

        // Register all existing entities
        for (World w : Bukkit.getWorlds()) {
            for (Entity e : w.getEntities()) {
                // Ignore entities that don't have attributes, we don't care about those
                if (!(e instanceof Attributable))
                    continue;

                getMMOEntity(e);
            }
        }

        MMOCraft.getInstance().getServer().getPluginManager().registerEvents(this, MMOCraft.getInstance());
    }

    // Called to store an entity with a set stat pool or attributes, can be used to essentially spawn custom mobs
    // Custom functionality should be a class that extends MMOEntity
    public void storeEntity(MMOEntity entity) {
        mmoEntityMap.put(entity.getEntity().getUniqueId(), entity);
    }

    public void removeEntity(MMOEntity entity) {
        removeEntity(entity.getEntity().getUniqueId());
    }

    public void removeEntity(Entity entity) {
        removeEntity(entity.getUniqueId());
    }

    public void removeEntity(UUID uuid){
        if (Bukkit.getEntity(uuid) != null)
            MMOCraft.getInstance().getLogger().finest("Removing MMOEntity instance for " + Bukkit.getEntity(uuid).getName());

        mmoEntityMap.remove(uuid);
    }

    public MMOEntity createDefaultMMOEntity(UUID uuid) {
        // Grab the entity
        Entity foundEntity = Bukkit.getEntity(uuid);
        // Does it exist?
        if (foundEntity == null)
            throw new IllegalArgumentException("Could not find entity with UUID: " + uuid);

        return createDefaultMMOEntity(foundEntity);
    }

    public MMOEntity createDefaultMMOEntity(Entity entity) {

        MMOCraft.getInstance().getLogger().finest("Creating default MMOEntity instance for " + entity.getName());

        // Now we can make the instance
        MMOEntity newEntInstance;

        // Is it a player?
        if (entity instanceof Player)
            newEntInstance = new MMOPlayer(entity);
        // Does it have HP?
        else if (entity instanceof Damageable)
            newEntInstance = new MMOEntityCreature(entity);
        // Default to a normal entity
        else
            newEntInstance = new MMOEntity(entity);

        return newEntInstance;
    }

    public MMOEntity getMMOEntity(UUID uuid) {
        // Check if they exist already
        if (mmoEntityMap.containsKey(uuid))
            return mmoEntityMap.get(uuid);

        // The entity didn't exist, this mob is probably vanilla or a player that hasn't been initialized yet
        // Normally custom mobs should be stored by calling storeEntity(), but this is a fallback
        MMOEntity newEntInstance = createDefaultMMOEntity(uuid);

        // Now that it is made, let's store it in the map for later retrieval
        storeEntity(newEntInstance);
        return newEntInstance;
    }

    public MMOEntity getMMOEntity(Entity entity) {
        return getMMOEntity(entity.getUniqueId());
    }

    public MMOPlayer getMMOPlayer(UUID uuid) {

        // Retrieve the entity, if it isn't an mmo player, our plugin messed up
        MMOEntity ent = getMMOEntity(uuid);
        if (!(ent instanceof MMOPlayer))
            throw new IllegalArgumentException("Entity with UUID " + uuid.toString() + " was not a player.");

        return (MMOPlayer) ent;
    }

    public MMOPlayer getMMOPlayer(Player player) {
        return getMMOPlayer(player.getUniqueId());
    }

    /**
     * Call to create a creature in the world at a location
     * @param loc The location to spawn it at
     * @param type The type of minecraft creature
     * @param stats The stat pool of the creature
     * @param displayName The name to display above this creature and in the chat if applicable
     * @param level The level to display on this creature
     * @return an MMOEntityCreature instance that has all attributes necessary
     */
    public MMOEntityCreature createCreature(Location loc, EntityType type, StatisticPool stats, String displayName, int level) {
        // First spawn the creature
        Entity ent = loc.getWorld().spawnEntity(loc, type);
        // Now get the MMOEntity instance
        MMOEntity mmoe = getMMOEntity(ent);

        // Assert that this is an instance of MMOCreature otherwise we screwed up
        assert mmoe instanceof MMOEntityCreature;
        MMOEntityCreature mmoc = (MMOEntityCreature) mmoe;

        // Apply the stats, set their name, set their level, and save the containers
        mmoc.applyNewStatPool(stats.copy());
        mmoc.getLevelExperience().forceLevel(level);
        mmoc.setCreatureName(displayName);
        mmoc.saveContainers();

        return mmoc;
    }

    public MMOEntityCreature createCreature(Location loc, EntityAttributes attributes, int level) {
        return createCreature(loc, attributes.MINECRAFT_TYPE, attributes.constructStatPool(level), attributes.DISPLAY_NAME, level);
    }

    public MMOEntityCreature createCreature(Location loc, EntityAttributes attributes) {
        return createCreature(loc, attributes, attributes.START_LEVEL);
    }

    // If an entity is being removed, we don't keep track of it anymore
    // Ignore cancelled because if the entity is NOT being removed i.e., event was canceled, we don't want to remove it
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityRemoved(EntityRemoveFromWorldEvent e) {

        // Ignore entities that don't have attributes, we don't care about those
        if (!(e.getEntity() instanceof Attributable))
            return;

        removeEntity(e.getEntity());
        MMOCraft.getInstance().getLogger().finest("Removing entity " + e.getEntity().getName());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityAdded(EntityAddToWorldEvent e) {

        // Ignore entities that don't have attributes, we don't care about those
        if (!(e.getEntity() instanceof Attributable))
            return;

        MMOCraft.getInstance().getLogger().finest("Adding entity " + e.getEntity().getName());
        MMOEntity me = getMMOEntity(e.getEntity().getUniqueId());
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent event) {
        MMOEntity whoShot = EntityManager.getInstance().getMMOEntity(event.getEntity());
        UUID arrowID = event.getProjectile().getUniqueId();
        // Copy the stats over to the arrow on the next tick if it exists
        new BukkitRunnable() {
            @Override
            public void run() {

                Entity ent = Bukkit.getEntity(arrowID);
                if (ent == null)
                    return;

                MMOEntity arrow = EntityManager.getInstance().getMMOEntity(arrowID);
                arrow.applyNewStatPool(whoShot.getStatPool().copy());
            }
        }.runTaskLater(MMOCraft.getInstance(), 0);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerChangedArmor(PlayerArmorChangeEvent event) {

        MMOPlayer mmop = getMMOPlayer(event.getPlayer());
        // Simply just reconsider armor stats
        mmop.considerArmorStatistics();

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerSwitchedSlot(PlayerItemHeldEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                MMOPlayer mmop = getMMOPlayer(event.getPlayer());
                mmop.considerHeldItemsStatistics();
            }
        }.runTaskLater(MMOCraft.getInstance(), 0);
    }

}
