package me.devvy.mmocraft;

import me.devvy.mmocraft.admin.*;
import me.devvy.mmocraft.commands.CommandLevelAdjuster;
import me.devvy.mmocraft.commands.CommandPlayerStats;
import me.devvy.mmocraft.crafting.CraftingManager;
import me.devvy.mmocraft.entity.EntityManager;
import me.devvy.mmocraft.items.ItemManager;
import me.devvy.mmocraft.listeners.CreatureNametagUpdater;
import me.devvy.mmocraft.listeners.PickupXPOrbListener;
import me.devvy.mmocraft.listeners.VanillaDamageOverrider;
import me.devvy.mmocraft.storage.SpreadsheetParser;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class MMOCraft extends JavaPlugin {

    private static MMOCraft INSTANCE;

    public static MMOCraft getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        INSTANCE = this;

        // TODO: Delete when this isn't intended
        SpreadsheetParser.deleteSpreadsheets();

        // Read up any data we need
        readSpreadsheets();

        // Instantiate any managers that we need
        new ItemManager();
        new EntityManager();
        new CraftingManager();

        // Register any commands that we need
        getCommand("stats").setExecutor(new CommandPlayerStats());
        getCommand("experience").setExecutor(new CommandLevelAdjuster());
        getCommand("editstats").setExecutor(new StatSetter());
        getCommand("datareset").setExecutor(new DataResetter());

        SpawnCreatureCommand spawnCreatureCommand = new SpawnCreatureCommand();
        getCommand("spawn").setExecutor(spawnCreatureCommand);
        getCommand("spawn").setTabCompleter(spawnCreatureCommand);

        SpawnItemCommand spawnItemCommand = new SpawnItemCommand();
        getCommand("mmogive").setExecutor(spawnItemCommand);
        getCommand("mmogive").setTabCompleter(spawnItemCommand);

        // Start up any listeners we need
        enableListeners();
    }

    public void enableListeners() {
        // Listeners involving interacting with vanilla mc events
        new VanillaDamageOverrider();
        new PickupXPOrbListener();

        // Listeners that update various visual aspects
        new CreatureNametagUpdater();

        // Listeners that are used specifically for debugging
        new StatViewer();
    }

    public void readSpreadsheets() {
        SpreadsheetParser.initializeSpreadsheets();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
