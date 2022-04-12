package me.devvy.mmocraft.admin;

import me.devvy.mmocraft.entity.EntityManager;
import me.devvy.mmocraft.entity.MMOEntityCreature;
import me.devvy.mmocraft.items.ItemManager;
import me.devvy.mmocraft.storage.EntityAttributes;
import me.devvy.mmocraft.storage.EntityAttributesStorage;
import me.devvy.mmocraft.storage.ItemAttributes;
import me.devvy.mmocraft.storage.ItemAttributesStorage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SpawnItemCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player))
            return true;

        if (args.length < 1) {
            sender.sendMessage("Please provide the item you would like to give yourself!");
            return true;
        }

        String itemID = args[0].toUpperCase();

        if (!ItemAttributesStorage.itemRegistered(itemID)) {
            sender.sendMessage(ChatColor.RED + "Item with unique ID " + itemID + " does not exist!");
            return true;
        }

        int amount = 1;

        if (args.length >= 2)
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException ignored) {
                sender.sendMessage(ChatColor.RED + "Please provide a valid integer for amount! " + ChatColor.DARK_RED + args[1] + ChatColor.RED + " will not work!");
                return true;
            }

        Player target = player;

        if (args.length >= 3) {
            target = Bukkit.getPlayer(args[3]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Could not find player " + args[3]);
                return true;
            }
        }

        target.getInventory().addItem(ItemManager.getInstance().generateCustomItem(itemID, amount));
        target.sendMessage(ChatColor.GREEN + "You were given " + amount + "x " + itemID);
        if (target != player)
            player.sendMessage(ChatColor.GREEN + "Successfully given " + target.getName() + " " + amount + "x " + itemID);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        // If there are no args, or we are in the processing of typing the first one, handle item names
        if (args.length <= 1) {

            // Add all entities to the list of potential args
            List<String> potentialArgs = new ArrayList<>();
            for (ItemAttributes attr : ItemAttributesStorage.getAllRegisteredItemAttributes())
                potentialArgs.add(attr.ID_NAME);

            // If there is no arg, then all items are valid
            if (args.length <= 0 || args[0].equals(""))
                return potentialArgs;

            String arg = args[0].toUpperCase();
            // Loop through all the args, if there is a match we can keep it, if not then get rid of it
            // If the entity name starts with what is typed so far, it is valid, keep it
            potentialArgs.removeIf(entId -> !entId.startsWith(arg));
            return potentialArgs;
            // If we are handling the level of the entity
        }

        return null;

    }
}
