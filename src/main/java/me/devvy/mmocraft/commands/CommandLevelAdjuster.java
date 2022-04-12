package me.devvy.mmocraft.commands;

import me.devvy.mmocraft.entity.EntityManager;
import me.devvy.mmocraft.player.MMOPlayer;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandLevelAdjuster implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length < 1)
            return false;

        if (!(sender instanceof Player))
            return false;

        String action = args[0];
        int amount = args.length >= 2 ? Integer.parseInt(args[1]) : 1;
        if (amount < 0)
            amount = 1;

        Player player = (Player) sender;
        MMOPlayer mmop = EntityManager.getInstance().getMMOPlayer(player);

        if (action.equalsIgnoreCase("give"))
            mmop.getLevelExperience().gainExperience(amount);
        else if (action.equalsIgnoreCase("reset"))
            mmop.getLevelExperience().reset();
        else
            player.sendMessage(ChatColor.RED + "Unknown action");

        return true;
    }
}
