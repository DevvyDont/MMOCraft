package me.devvy.mmocraft.admin;

import me.devvy.mmocraft.entity.EntityManager;
import me.devvy.mmocraft.entity.MMOEntityCreature;
import me.devvy.mmocraft.storage.EntityAttributes;
import me.devvy.mmocraft.storage.EntityAttributesStorage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SpawnCreatureCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player))
            return true;

        if (args.length < 1) {
            sender.sendMessage("Please provide the creature you would like to spawn!");
            return true;
        }

        String creatureId = args[0].toUpperCase();

        if (!EntityAttributesStorage.entityRegistered(creatureId)) {
            sender.sendMessage(ChatColor.RED + "Creature with unique ID " + creatureId + " does not exist!");
            return true;
        }


        EntityAttributes att = EntityAttributesStorage.getEntityAttributes(creatureId);

        int level = att.START_LEVEL;

        if (args.length >= 2)
            try {
                level = Integer.parseInt(args[1]);
            } catch (NumberFormatException ignored) {
                sender.sendMessage(ChatColor.RED + "Please provide a valid integer for level you want to spawn this creature! " + ChatColor.DARK_RED + args[1] + ChatColor.RED + " will not work!");
                return true;
            }

        MMOEntityCreature creature = EntityManager.getInstance().createCreature(player.getLocation(), att, level);
        sender.sendMessage(ChatColor.GREEN + "Created the creature " + creature.getCreatureName());
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        // If there are no args, or we are in the processing of typing the first one, handle entity names
        if (args.length <= 1) {

            // Add all entities to the list of potential args
            List<String> potentialArgs = new ArrayList<>();
            for (EntityAttributes attr : EntityAttributesStorage.getAllRegisteredEntityAttributes())
                potentialArgs.add(attr.ID_NAME);

            // If there is no arg, then all entities are valid
            if (args.length <= 0 || args[0].equals(""))
                return potentialArgs;

            String arg = args[0].toUpperCase();
            // Loop through all the args, if there is a match we can keep it, if not then get rid of it
            // If the entity name starts with what is typed so far, it is valid, keep it
            potentialArgs.removeIf(entId -> !entId.startsWith(arg));
            return potentialArgs;
        // If we are handling the level of the entity
        } else if (args.length == 2) {

            List<String> potentialArgs = new ArrayList<>();
            // Entity exist?
            if (!EntityAttributesStorage.entityRegistered(args[0].toUpperCase()))
                return potentialArgs;

            EntityAttributes attr = EntityAttributesStorage.getEntityAttributes(args[0]);

            for (int i = attr.START_LEVEL; i <= attr.END_LEVEL; i++)
                potentialArgs.add(String.valueOf(i));

            return potentialArgs;
        }

        return null;

    }
}
